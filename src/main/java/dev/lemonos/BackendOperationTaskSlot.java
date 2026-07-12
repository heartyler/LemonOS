package dev.lemonos;

import org.bukkit.scheduler.BukkitTask;

/** Owns at most one task and prevents a stale task from clearing its replacement. */
final class BackendOperationTaskSlot {
    private BukkitTask task;

    synchronized void replace(BukkitTask next) {
        if (this.task == next) return;
        BukkitTask previous = this.task;
        this.task = next;
        if (previous != null) previous.cancel();
    }

    synchronized boolean clearIfCurrent(BukkitTask expected) {
        if (expected == null || this.task != expected) return false;
        this.task = null;
        expected.cancel();
        return true;
    }

    synchronized void cancel() {
        BukkitTask previous = this.task;
        this.task = null;
        if (previous != null) previous.cancel();
    }

    synchronized boolean active() {
        return this.task != null && !this.task.isCancelled();
    }
}
