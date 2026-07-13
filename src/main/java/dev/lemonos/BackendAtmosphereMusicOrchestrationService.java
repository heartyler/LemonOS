package dev.lemonos;

/** Pure scheduling policy for Lobby music. */
final class BackendAtmosphereMusicOrchestrationService {
    record Schedule(boolean active, long initialDelayTicks, long periodTicks) {
    }

    Schedule schedule(boolean lobby, boolean musicEnabled) {
        return lobby && musicEnabled
                ? new Schedule(true, 20L, 20L)
                : new Schedule(false, 0L, 0L);
    }

    boolean shouldTick(boolean lobby, boolean musicEnabled) {
        return lobby && musicEnabled;
    }
}
