package dev.lemonos;

final class BackendAtmosphereOrchestrationService {
    record Schedule(boolean active, long initialDelayTicks, long periodTicks) {
    }

    Schedule schedule(boolean survival, BackendAtmosphereConfig config) {
        return survival && config.enabled()
                ? new Schedule(true, 40L, 20L)
                : new Schedule(false, 0L, 0L);
    }

    boolean shouldTick(BackendAtmosphereConfig config, boolean resting, boolean suspendWhileResting) {
        return config.enabled() && !(resting && suspendWhileResting);
    }
}
