package dev.lemonos;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;

final class BackendPasscodeLifecycleService {
    void cancelTasks(Collection<BukkitTask> tasks) {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
    }

    void clearStatus(UUID playerId, Map<UUID, String> titleStatuses, Map<UUID, BukkitTask> statusTasks) {
        titleStatuses.remove(playerId);
        this.cancelRemoved(statusTasks.remove(playerId));
    }

    void clearSuccess(UUID playerId, Map<UUID, BukkitTask> successTasks) {
        this.cancelRemoved(successTasks.remove(playerId));
    }

    void clearOverflow(UUID playerId, Set<UUID> overflowWarnings, Map<UUID, BukkitTask> overflowTasks) {
        overflowWarnings.remove(playerId);
        this.cancelRemoved(overflowTasks.remove(playerId));
    }

    void clearAll(
            UUID playerId,
            Map<UUID, String> titleStatuses,
            Map<UUID, BukkitTask> statusTasks,
            Map<UUID, BukkitTask> successTasks,
            Set<UUID> overflowWarnings,
            Map<UUID, BukkitTask> overflowTasks) {
        this.clearStatus(playerId, titleStatuses, statusTasks);
        this.clearSuccess(playerId, successTasks);
        this.clearOverflow(playerId, overflowWarnings, overflowTasks);
    }

    private void cancelRemoved(BukkitTask task) {
        if (task != null) {
            task.cancel();
        }
    }
}
