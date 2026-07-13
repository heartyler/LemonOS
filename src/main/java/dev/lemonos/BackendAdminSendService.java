package dev.lemonos;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/** Token-aware lifecycle for sending another player to a local spawn or proxy place. */
final class BackendAdminSendService<T> {
    private static final long WAKE_TIMEOUT_MILLIS = 120000L;
    private static final long WAKE_INITIAL_DELAY_TICKS = 1L;
    private static final long WAKE_POLL_PERIOD_TICKS = 20L;
    private static final long RESULT_TIMEOUT_TICKS = 200L;

    private final BackendOperationRegistry<UUID, SendOperation<T>> operations = new BackendOperationRegistry<>();
    private final AtomicLong generationCounter = new AtomicLong();
    private final Scheduler scheduler;
    private final LongSupplier monotonicMillis;
    private final PlayerLookup playerLookup;
    private final Predicate<Player> admin;
    private final BiPredicate<Player, Player> canTarget;
    private final Predicate<T> available;
    private final Predicate<T> wakeable;
    private final BiConsumer<Player, T> wakeRequest;
    private final BiConsumer<T, String> runtimeStatus;
    private final Supplier<String> wakingStatus;
    private final BiConsumer<Player, T> identityTransferSaver;
    private final Consumer<Player> identityTransferClearer;
    private final Supplier<Location> currentSpawn;
    private final Function<T, String> destinationName;
    private final ProtocolSender<T> protocolSender;
    private final StatusLeaseFactory statusLeaseFactory;
    private final Notifier notifier;
    private final FailureReporter failureReporter;

    BackendAdminSendService(
            Scheduler scheduler,
            LongSupplier monotonicMillis,
            PlayerLookup playerLookup,
            Predicate<Player> admin,
            BiPredicate<Player, Player> canTarget,
            Predicate<T> available,
            Predicate<T> wakeable,
            BiConsumer<Player, T> wakeRequest,
            BiConsumer<T, String> runtimeStatus,
            Supplier<String> wakingStatus,
            BiConsumer<Player, T> identityTransferSaver,
            Consumer<Player> identityTransferClearer,
            Supplier<Location> currentSpawn,
            Function<T, String> destinationName,
            ProtocolSender<T> protocolSender,
            StatusLeaseFactory statusLeaseFactory,
            Notifier notifier,
            FailureReporter failureReporter) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.monotonicMillis = Objects.requireNonNull(monotonicMillis, "monotonicMillis");
        this.playerLookup = Objects.requireNonNull(playerLookup, "playerLookup");
        this.admin = Objects.requireNonNull(admin, "admin");
        this.canTarget = Objects.requireNonNull(canTarget, "canTarget");
        this.available = Objects.requireNonNull(available, "available");
        this.wakeable = Objects.requireNonNull(wakeable, "wakeable");
        this.wakeRequest = Objects.requireNonNull(wakeRequest, "wakeRequest");
        this.runtimeStatus = Objects.requireNonNull(runtimeStatus, "runtimeStatus");
        this.wakingStatus = Objects.requireNonNull(wakingStatus, "wakingStatus");
        this.identityTransferSaver = Objects.requireNonNull(identityTransferSaver, "identityTransferSaver");
        this.identityTransferClearer = Objects.requireNonNull(identityTransferClearer, "identityTransferClearer");
        this.currentSpawn = Objects.requireNonNull(currentSpawn, "currentSpawn");
        this.destinationName = Objects.requireNonNull(destinationName, "destinationName");
        this.protocolSender = Objects.requireNonNull(protocolSender, "protocolSender");
        this.statusLeaseFactory = Objects.requireNonNull(statusLeaseFactory, "statusLeaseFactory");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
        this.failureReporter = Objects.requireNonNull(failureReporter, "failureReporter");
    }

    void start(Player actor, Player target, T destination) {
        if (!this.valid(actor, target, destination)) {
            this.notify(actor, "try again", NamedTextColor.DARK_GRAY);
            return;
        }
        UUID actorId = actor.getUniqueId();
        SendOperation<T> existing = this.current(actorId);
        if (existing != null) {
            if (existing.targetId.equals(target.getUniqueId()) && Objects.equals(existing.destination, destination)) {
                this.cancel(actorId, true);
                return;
            }
            this.cancel(actorId, false);
        }
        if (!this.available(destination) && !this.wakeable(destination)) {
            this.notify(actor, "out of range", NamedTextColor.DARK_GRAY);
            return;
        }
        BackendOperationToken token;
        SendOperation<T> pending;
        try {
            token = this.nextToken();
            pending = new SendOperation<>(actorId, target.getUniqueId(), destination, token,
                    this.statusLeaseFactory.create(actorId));
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Unable to initialize admin send", exception);
            this.notify(actor, "try again", NamedTextColor.DARK_GRAY);
            return;
        }
        if (!this.operations.beginIfAbsent(actorId, token, pending)) {
            pending.statusLease.close();
            this.notify(actor, "try again", NamedTextColor.DARK_GRAY);
            return;
        }
        if (this.available(destination)) {
            this.dispatch(pending);
            return;
        }
        try {
            pending.statusLease.publish(Component.text("waiting", NamedTextColor.GRAY));
            this.wakeRequest.accept(actor, destination);
            this.runtimeStatus.accept(destination, this.wakingStatus.get());
            long timeoutAt = saturatingAdd(this.monotonicMillis.getAsLong(), WAKE_TIMEOUT_MILLIS);
            BukkitTask wakeTask = this.scheduler.timer(() -> this.pollWake(pending, timeoutAt),
                    WAKE_INITIAL_DELAY_TICKS, WAKE_POLL_PERIOD_TICKS);
            if (wakeTask == null) throw new IllegalStateException("admin send wake scheduler returned no task");
            pending.taskSlot.replace(wakeTask);
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Unable to start admin send wake lifecycle", exception);
            this.fail(pending, "try again");
        }
    }

    void sendToCurrentSpawn(Player actor, Player target) {
        UUID actorId = actor == null ? null : actor.getUniqueId();
        if (actorId != null && this.current(actorId) != null) this.cancel(actorId, false);
        if (!this.valid(actor, target, null, false)) {
            this.notify(actor, "try again", NamedTextColor.DARK_GRAY);
            return;
        }
        try {
            Location spawn = this.currentSpawn.get();
            if (spawn == null || !target.teleport(spawn)) {
                this.notify(actor, "try again", NamedTextColor.DARK_GRAY);
                return;
            }
            actor.closeInventory();
            this.notify(actor, "sent", NamedTextColor.GRAY);
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Unable to send admin target to current spawn", exception);
            this.notify(actor, "try again", NamedTextColor.DARK_GRAY);
        }
    }

    void finishResult(UUID actorId, UUID targetId, String place, UUID operationId, String result) {
        SendOperation<T> pending = this.current(actorId);
        String expectedPlace = pending == null ? null : this.destinationName(pending.destination);
        if (pending == null || operationId == null || !pending.token.operationId().equals(operationId)
                || targetId == null || !pending.targetId.equals(targetId) || place == null || expectedPlace == null
                || !expectedPlace.equalsIgnoreCase(place)) return;
        if (this.operations.removeIfCurrent(actorId, pending.token).isEmpty()) return;
        this.cleanup(pending, false);
        Player actor = this.player(actorId);
        if (BackendAdminProtocol.SEND_RESULT_SENT.equals(result)) {
            this.notify(actor, "sent", NamedTextColor.GRAY);
            return;
        }
        this.clearTransfer(this.player(targetId));
        this.notify(actor, "try again", NamedTextColor.DARK_GRAY);
    }

    void cancel(UUID actorId, boolean notify) {
        if (actorId == null) return;
        SendOperation<T> pending = this.operations.remove(actorId).map(BackendOperationRegistry.Entry::operation).orElse(null);
        if (pending == null) return;
        this.cleanup(pending, true);
        if (notify) this.notify(this.player(actorId), "nothing changed", NamedTextColor.DARK_GRAY);
    }

    void clear() {
        this.operations.clear(operation -> this.cleanup(operation, false));
    }

    private void pollWake(SendOperation<T> pending, long timeoutAt) {
        if (!this.current(pending)) return;
        try {
            Player actor = this.player(pending.actorId);
            Player target = this.player(pending.targetId);
            if (!this.valid(actor, target, pending.destination)) {
                this.fail(pending, "try again");
                return;
            }
            if (this.available(pending.destination)) {
                this.dispatch(pending);
                return;
            }
            if (this.monotonicMillis.getAsLong() >= timeoutAt) {
                this.fail(pending, "out of range");
                return;
            }
            pending.statusLease.publish(Component.text("waiting", NamedTextColor.GRAY));
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Admin send wake poll failed", exception);
            this.fail(pending, "try again");
        }
    }

    private void dispatch(SendOperation<T> pending) {
        if (!this.current(pending)) return;
        pending.taskSlot.cancel();
        Player actor = this.player(pending.actorId);
        Player target = this.player(pending.targetId);
        if (!this.valid(actor, target, pending.destination) || !this.available(pending.destination)) {
            this.fail(pending, "try again");
            return;
        }
        try {
            this.identityTransferSaver.accept(target, pending.destination);
            this.protocolSender.send(actor, pending.targetId, pending.destination, pending.token.operationId());
            actor.closeInventory();
            BukkitTask timeoutTask = this.scheduler.later(() -> {
                if (this.current(pending)) this.fail(pending, "try again");
            }, RESULT_TIMEOUT_TICKS);
            if (timeoutTask == null) throw new IllegalStateException("admin send result scheduler returned no task");
            pending.taskSlot.replace(timeoutTask);
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Unable to dispatch admin send", exception);
            this.fail(pending, "try again");
        }
    }

    private boolean valid(Player actor, Player target, T destination) {
        return this.valid(actor, target, destination, true);
    }

    private boolean valid(Player actor, Player target, T destination, boolean destinationRequired) {
        try {
            return actor != null && actor.isOnline() && this.admin.test(actor) && this.canTarget.test(actor, target)
                    && (!destinationRequired || destination != null);
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Admin send validation failed", exception);
            return false;
        }
    }

    private boolean available(T destination) {
        try {
            return destination != null && this.available.test(destination);
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Admin send availability check failed", exception);
            return false;
        }
    }

    private boolean wakeable(T destination) {
        try {
            return destination != null && this.wakeable.test(destination);
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Admin send wakeability check failed", exception);
            return false;
        }
    }

    private String destinationName(T destination) {
        try {
            return destination == null ? null : this.destinationName.apply(destination);
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Admin send destination lookup failed", exception);
            return null;
        }
    }

    private void fail(SendOperation<T> pending, String message) {
        if (pending == null || this.operations.removeIfCurrent(pending.actorId, pending.token).isEmpty()) return;
        this.cleanup(pending, true);
        this.notify(this.player(pending.actorId), message, NamedTextColor.DARK_GRAY);
    }

    private boolean current(SendOperation<T> pending) {
        return pending != null && this.operations.isCurrent(pending.actorId, pending.token);
    }

    private SendOperation<T> current(UUID actorId) {
        return actorId == null ? null : this.operations.current(actorId)
                .map(BackendOperationRegistry.Entry::operation).orElse(null);
    }

    private BackendOperationToken nextToken() {
        long generation = this.generationCounter.updateAndGet(value -> value == Long.MAX_VALUE ? 1L : value + 1L);
        return BackendOperationToken.create(generation);
    }

    private void cleanup(SendOperation<T> operation, boolean clearTransfer) {
        operation.taskSlot.cancel();
        operation.statusLease.close();
        if (clearTransfer) this.clearTransfer(this.player(operation.targetId));
    }

    private Player player(UUID uuid) {
        if (uuid == null) return null;
        try {
            return this.playerLookup.find(uuid);
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Admin send player lookup failed", exception);
            return null;
        }
    }

    private void clearTransfer(Player target) {
        if (target == null) return;
        try {
            this.identityTransferClearer.accept(target);
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Unable to clear admin send identity transfer", exception);
        }
    }

    private void notify(Player actor, String message, NamedTextColor color) {
        try {
            if (actor == null || !actor.isOnline()) return;
            this.notifier.notify(actor, Component.text(message, color));
        }
        catch (RuntimeException exception) {
            this.failureReporter.report("Unable to publish admin send status", exception);
        }
    }

    private static long saturatingAdd(long value, long increment) {
        return value > Long.MAX_VALUE - increment ? Long.MAX_VALUE : value + increment;
    }

    private static final class SendOperation<T> {
        private final UUID actorId;
        private final UUID targetId;
        private final T destination;
        private final BackendOperationToken token;
        private final BackendOperationTaskSlot taskSlot = new BackendOperationTaskSlot();
        private final BackendOperationStatusLease statusLease;

        private SendOperation(UUID actorId, UUID targetId, T destination, BackendOperationToken token,
                BackendOperationStatusLease statusLease) {
            this.actorId = actorId;
            this.targetId = targetId;
            this.destination = destination;
            this.token = token;
            this.statusLease = Objects.requireNonNull(statusLease, "statusLease");
        }
    }

    interface Scheduler {
        BukkitTask timer(Runnable runnable, long delayTicks, long periodTicks);
        BukkitTask later(Runnable runnable, long delayTicks);
    }

    interface PlayerLookup {
        Player find(UUID uuid);
    }

    interface ProtocolSender<T> {
        void send(Player actor, UUID targetId, T destination, UUID operationId);
    }

    interface StatusLeaseFactory {
        BackendOperationStatusLease create(UUID actorId);
    }

    interface Notifier {
        void notify(Player actor, Component component);
    }

    interface FailureReporter {
        void report(String context, Throwable failure);
    }
}
