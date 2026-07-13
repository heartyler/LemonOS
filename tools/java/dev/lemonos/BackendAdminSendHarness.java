package dev.lemonos;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public final class BackendAdminSendHarness {
    private enum Place { LOBBY, SURVIVAL, CREATIVE }

    public static void main(String[] args) {
        UUID actorId = UUID.fromString("00000000-0000-0000-0000-000000000101");
        UUID targetId = UUID.fromString("00000000-0000-0000-0000-000000000102");
        PlayerState actorState = new PlayerState(actorId);
        PlayerState targetState = new PlayerState(targetId);
        Player actor = player(actorState);
        Player target = player(targetState);
        Map<UUID, Player> players = new HashMap<>();
        players.put(actorId, actor);
        players.put(targetId, target);

        AtomicBoolean available = new AtomicBoolean(true);
        AtomicBoolean wakeable = new AtomicBoolean(false);
        AtomicLong now = new AtomicLong(1000L);
        AtomicInteger wakes = new AtomicInteger();
        AtomicInteger saves = new AtomicInteger();
        AtomicInteger clears = new AtomicInteger();
        AtomicReference<UUID> operationId = new AtomicReference<>();
        AtomicReference<Place> sentPlace = new AtomicReference<>();
        List<String> notices = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        FakeScheduler scheduler = new FakeScheduler();
        BackendActionBarCoordinator coordinator = new BackendActionBarCoordinator();

        BackendAdminSendService<Place> service = new BackendAdminSendService<>(
                scheduler,
                now::get,
                players::get,
                player -> player == actor,
                (source, selected) -> source == actor && selected == target && selected.isOnline(),
                ignored -> available.get(),
                ignored -> wakeable.get(),
                (source, place) -> wakes.incrementAndGet(),
                (place, status) -> require("waking up.".equals(status), "wake status changed"),
                () -> "waking up.",
                (selected, place) -> saves.incrementAndGet(),
                selected -> clears.incrementAndGet(),
                () -> new Location(null, 0.0, 64.0, 0.0),
                place -> place.name().toLowerCase(),
                (source, selectedId, place, tokenId) -> {
                    require(selectedId.equals(targetId), "protocol target changed");
                    sentPlace.set(place);
                    operationId.set(tokenId);
                },
                owner -> new BackendOperationStatusLease(coordinator, owner, BackendActionBarCoordinator.Owner.ADMIN_SEND),
                (source, component) -> notices.add(component.toString()),
                (context, failure) -> failures.add(context));

        service.start(actor, target, Place.CREATIVE);
        require(saves.get() == 1, "available send did not save transfer");
        require(sentPlace.get() == Place.CREATIVE && operationId.get() != null, "available send was not dispatched");
        require(actorState.closeCount == 1, "available send did not close actor inventory");
        require(scheduler.later != null, "result timeout was not scheduled");
        service.finishResult(actorId, targetId, "creative", operationId.get(), BackendAdminProtocol.SEND_RESULT_SENT);
        require(lastContains(notices, "sent"), "successful result did not notify actor");
        require(scheduler.lastLaterTask.cancelled.get(), "successful result did not cancel timeout");

        available.set(false);
        wakeable.set(true);
        operationId.set(null);
        service.start(actor, target, Place.SURVIVAL);
        require(wakes.get() == 1 && scheduler.timer != null, "wake send did not start polling");
        require(coordinator.owns(actorId, BackendActionBarCoordinator.Owner.ADMIN_SEND), "wake send did not own status lease");
        available.set(true);
        scheduler.timer.run();
        require(sentPlace.get() == Place.SURVIVAL && operationId.get() != null, "ready wake send was not dispatched");
        service.finishResult(actorId, targetId, "survival", operationId.get(), BackendAdminProtocol.SEND_RESULT_TRY_AGAIN);
        require(clears.get() >= 1, "failed proxy result did not clear transfer");
        require(lastContains(notices, "try again"), "failed proxy result did not notify actor");

        available.set(false);
        wakeable.set(true);
        service.start(actor, target, Place.CREATIVE);
        int clearsBeforeCancel = clears.get();
        service.start(actor, target, Place.CREATIVE);
        require(clears.get() == clearsBeforeCancel + 1, "duplicate send did not cancel and clear transfer");
        require(lastContains(notices, "nothing changed"), "duplicate send did not report cancellation");

        wakeable.set(false);
        service.start(actor, target, Place.CREATIVE);
        require(lastContains(notices, "out of range"), "unreachable destination did not fail closed");

        available.set(true);
        targetState.teleportResult = true;
        int closesBeforeSpawn = actorState.closeCount;
        service.sendToCurrentSpawn(actor, target);
        require(targetState.teleportCount == 1, "current-spawn send did not teleport target");
        require(actorState.closeCount == closesBeforeSpawn + 1, "current-spawn send did not close inventory");

        available.set(false);
        wakeable.set(true);
        scheduler.failTimer = true;
        service.start(actor, target, Place.SURVIVAL);
        require(!failures.isEmpty(), "scheduler failure was swallowed");
        require(lastContains(notices, "try again"), "scheduler failure did not fail safely");
        service.clear();
        require(!coordinator.owns(actorId, BackendActionBarCoordinator.Owner.ADMIN_SEND), "service clear leaked status lease");
    }

    private static Player player(PlayerState state) {
        return (Player)Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class}, (proxy, method, args) -> {
            return switch (method.getName()) {
                case "getUniqueId" -> state.uuid;
                case "isOnline" -> state.online;
                case "closeInventory" -> {
                    state.closeCount++;
                    yield null;
                }
                case "teleport" -> {
                    state.teleportCount++;
                    yield state.teleportResult;
                }
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                case "toString" -> "Player[" + state.uuid + "]";
                default -> defaultValue(method.getReturnType());
            };
        });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte)0;
        if (type == short.class) return (short)0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        if (type == char.class) return '\0';
        return null;
    }

    private static boolean lastContains(List<String> messages, String expected) {
        return !messages.isEmpty() && messages.get(messages.size() - 1).contains(expected);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    private static final class PlayerState {
        private final UUID uuid;
        private boolean online = true;
        private boolean teleportResult;
        private int closeCount;
        private int teleportCount;

        private PlayerState(UUID uuid) {
            this.uuid = uuid;
        }
    }

    private static final class FakeScheduler implements BackendAdminSendService.Scheduler {
        private Runnable timer;
        private Runnable later;
        private FakeTask lastTimerTask;
        private FakeTask lastLaterTask;
        private boolean failTimer;

        @Override
        public BukkitTask timer(Runnable runnable, long delayTicks, long periodTicks) {
            if (this.failTimer) throw new IllegalStateException("timer unavailable");
            this.timer = runnable;
            this.lastTimerTask = new FakeTask();
            return this.lastTimerTask.proxy;
        }

        @Override
        public BukkitTask later(Runnable runnable, long delayTicks) {
            this.later = runnable;
            this.lastLaterTask = new FakeTask();
            return this.lastLaterTask.proxy;
        }
    }

    private static final class FakeTask {
        private final AtomicBoolean cancelled = new AtomicBoolean();
        private final BukkitTask proxy = (BukkitTask)Proxy.newProxyInstance(
                BukkitTask.class.getClassLoader(), new Class<?>[]{BukkitTask.class}, (ignored, method, args) -> {
                    return switch (method.getName()) {
                        case "cancel" -> {
                            this.cancelled.set(true);
                            yield null;
                        }
                        case "isCancelled" -> this.cancelled.get();
                        case "isSync" -> true;
                        case "getTaskId" -> 1;
                        default -> defaultValue(method.getReturnType());
                    };
                });
    }
}
