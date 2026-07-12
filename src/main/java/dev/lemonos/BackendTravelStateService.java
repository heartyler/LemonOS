/*
 * Backend-side LemonOS token-aware travel state lifecycle.
 */
package dev.lemonos;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

final class BackendTravelStateService<T> {
    private final BackendOperationRegistry<UUID, TravelState<T>> travels = new BackendOperationRegistry<>();
    private final AtomicLong generationCounter = new AtomicLong();
    private final ResumeDelay resumeDelay;
    private final BackendActionBarCoordinator actionBarCoordinator;

    BackendTravelStateService(ResumeDelay resumeDelay, BackendActionBarCoordinator actionBarCoordinator) {
        this.resumeDelay = resumeDelay;
        this.actionBarCoordinator = actionBarCoordinator;
    }

    TravelState<T> get(UUID uuid) {
        return uuid == null ? null : this.travels.current(uuid).map(BackendOperationRegistry.Entry::operation).orElse(null);
    }

    boolean contains(UUID uuid) {
        return this.get(uuid) != null;
    }

    boolean isCurrent(UUID uuid, BackendOperationToken token) {
        return uuid != null && this.travels.isCurrent(uuid, token);
    }

    boolean replaceTaskIfCurrent(UUID uuid, BackendOperationToken token, BukkitTask task) {
        if (task == null) return false;
        if (!this.travels.useIfCurrent(uuid, token, state -> state.taskSlot.replace(task))) {
            task.cancel();
            return false;
        }
        return true;
    }

    boolean showIfCurrent(UUID uuid, BackendOperationToken token, String status) {
        if (uuid == null || status == null || status.isBlank()) return false;
        return this.travels.useIfCurrent(uuid, token,
                state -> state.statusLease.publish(Component.text(status, NamedTextColor.GRAY)));
    }

    BackendOperationToken begin(UUID uuid, T target, boolean wake, String status, Function<BackendOperationToken, BukkitTask> scheduler) {
        if (uuid == null || scheduler == null) return null;
        BackendOperationToken token = this.nextToken();
        TravelState<T> state = new TravelState<>(uuid, token, target, wake,
                new BackendOperationStatusLease(this.actionBarCoordinator, uuid, BackendActionBarCoordinator.Owner.TRAVEL));
        if (!this.travels.beginIfAbsent(uuid, token, state)) {
            state.statusLease.close();
            return null;
        }
        if (status != null && !status.isBlank()) state.statusLease.publish(Component.text(status, NamedTextColor.GRAY));
        try {
            BukkitTask task = scheduler.apply(token);
            if (task == null) throw new IllegalStateException("travel scheduler returned no task");
            state.taskSlot.replace(task);
            return token;
        }
        catch (RuntimeException exception) {
            this.travels.removeIfCurrent(uuid, token);
            this.cleanup(uuid, state);
            return null;
        }
    }

    TravelState<T> endIfCurrent(UUID uuid, BackendOperationToken token) {
        TravelState<T> state = this.travels.removeIfCurrent(uuid, token)
                .map(BackendOperationRegistry.Entry::operation)
                .orElse(null);
        if (state != null) this.cleanup(uuid, state);
        return state;
    }

    void cancel(Player player, boolean notify) {
        TravelState<T> state = this.travels.remove(player.getUniqueId())
                .map(BackendOperationRegistry.Entry::operation)
                .orElse(null);
        if (state == null) return;
        this.cleanup(player.getUniqueId(), state);
        if (notify && player.isOnline()) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
        }
    }

    void cancelTasksAndClearStatuses() {
        this.travels.clear(state -> this.cleanup(state.owner, state));
    }

    void clear() {
        this.travels.clear(state -> this.cleanup(state.owner, state));
    }

    private BackendOperationToken nextToken() {
        long generation = this.generationCounter.updateAndGet(value -> value == Long.MAX_VALUE ? 1L : value + 1L);
        return BackendOperationToken.create(generation);
    }

    private void cleanup(UUID uuid, TravelState<T> state) {
        state.taskSlot.cancel();
        state.statusLease.close();
        this.resumeDelay.delay(uuid);
    }

    interface ResumeDelay {
        void delay(UUID uuid);
    }

    static final class TravelState<T> {
        private final UUID owner;
        private final BackendOperationToken token;
        private final T target;
        private final boolean wake;
        private final BackendOperationTaskSlot taskSlot = new BackendOperationTaskSlot();
        private final BackendOperationStatusLease statusLease;

        private TravelState(UUID owner, BackendOperationToken token, T target, boolean wake, BackendOperationStatusLease statusLease) {
            this.owner = owner;
            this.token = token;
            this.target = target;
            this.wake = wake;
            this.statusLease = statusLease;
        }

        BackendOperationToken token() { return this.token; }
        T target() { return this.target; }
        boolean wake() { return this.wake; }
    }
}
