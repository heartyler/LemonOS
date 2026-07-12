/*
 * Backend-side LemonOS atmosphere world phase state.
 */
package dev.lemonos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class BackendAtmosphereWorldService<T, W> {
    private final Map<String, WorldAtmosphereState<T, W>> worldAtmosphereStates = new ConcurrentHashMap<String, WorldAtmosphereState<T, W>>();

    void clear() {
        this.worldAtmosphereStates.clear();
    }

    PhaseChange observe(String worldId, T timePhase, W weatherPhase) {
        if (worldId == null || worldId.isBlank()) {
            return new PhaseChange(false, false);
        }
        WorldAtmosphereState<T, W> state = this.worldAtmosphereStates.get(worldId);
        if (state == null) {
            this.worldAtmosphereStates.put(worldId, new WorldAtmosphereState<T, W>(timePhase, weatherPhase));
            return new PhaseChange(false, false);
        }
        boolean timeChanged = state.timePhase != timePhase;
        boolean weatherChanged = state.weatherPhase != weatherPhase;
        if (timeChanged) {
            state.timePhase = timePhase;
        }
        if (weatherChanged) {
            state.weatherPhase = weatherPhase;
        }
        return new PhaseChange(timeChanged, weatherChanged);
    }

    static final class PhaseChange {
        private final boolean timeChanged;
        private final boolean weatherChanged;

        PhaseChange(boolean timeChanged, boolean weatherChanged) {
            this.timeChanged = timeChanged;
            this.weatherChanged = weatherChanged;
        }

        boolean timeChanged() {
            return this.timeChanged;
        }

        boolean weatherChanged() {
            return this.weatherChanged;
        }
    }

    private static final class WorldAtmosphereState<T, W> {
        private T timePhase;
        private W weatherPhase;

        private WorldAtmosphereState(T timePhase, W weatherPhase) {
            this.timePhase = timePhase;
            this.weatherPhase = weatherPhase;
        }
    }
}
