package dev.lemonos;

import java.util.Objects;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/** Owns async reachability probing and main-thread place status refresh. */
final class BackendPlaceRuntimeLifecycleService {
    private static final long ERROR_LOG_INTERVAL_NANOS = 30_000_000_000L;
    private final Plugin plugin;
    private final Consumer<RuntimeException> errorHandler;
    private BukkitTask probeTask;
    private BukkitTask statusTask;
    private long lastErrorLogNanos;

    BackendPlaceRuntimeLifecycleService(Plugin plugin, Consumer<RuntimeException> errorHandler) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.errorHandler = Objects.requireNonNull(errorHandler, "errorHandler");
    }

    void start(long initialDelayTicks, long periodTicks, Runnable probeAction, Runnable statusAction) {
        this.stop();
        if (probeAction == null || statusAction == null || periodTicks <= 0L || !this.plugin.isEnabled()) return;
        long safeInitialDelay = Math.max(0L, initialDelayTicks);
        long safePeriod = Math.max(1L, periodTicks);
        this.probeTask = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                this.plugin,
                () -> this.runSafely(probeAction),
                safeInitialDelay,
                safePeriod);
        this.statusTask = this.plugin.getServer().getScheduler().runTaskTimer(
                this.plugin,
                () -> this.runSafely(statusAction),
                safeInitialDelay,
                safePeriod);
    }

    void stop() {
        if (this.probeTask != null) {
            this.probeTask.cancel();
            this.probeTask = null;
        }
        if (this.statusTask != null) {
            this.statusTask.cancel();
            this.statusTask = null;
        }
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
