package dev.lemonos;

import java.util.Objects;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/** Owns the asynchronous place availability probe lifecycle. */
final class BackendPlaceRuntimeLifecycleService {
    private static final long ERROR_LOG_INTERVAL_NANOS = 30_000_000_000L;
    private final Plugin plugin;
    private final Consumer<RuntimeException> errorHandler;
    private BukkitTask task;
    private long lastErrorLogNanos;

    BackendPlaceRuntimeLifecycleService(Plugin plugin, Consumer<RuntimeException> errorHandler) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.errorHandler = Objects.requireNonNull(errorHandler, "errorHandler");
    }

    void start(long initialDelayTicks, long periodTicks, Runnable action) {
        this.stop();
        if (action == null || periodTicks <= 0L || !this.plugin.isEnabled()) return;
        this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                this.plugin,
                () -> this.runSafely(action),
                Math.max(0L, initialDelayTicks),
                Math.max(1L, periodTicks));
    }

    void stop() {
        if (this.task == null) return;
        this.task.cancel();
        this.task = null;
    }

    private void runSafely(Runnable action) {
        try {
            action.run();
        }
        catch (RuntimeException exception) {
            long now = System.nanoTime();
            if (now - this.lastErrorLogNanos < ERROR_LOG_INTERVAL_NANOS) return;
            this.lastErrorLogNanos = now;
            this.errorHandler.accept(exception);
        }
    }
}
