/*
 * Backend-side LemonOS activity actionbar message state.
 */
package dev.lemonos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class BackendActivityMessageService {
    private final Map<UUID, ActivityState> activityStates = new ConcurrentHashMap<UUID, ActivityState>();

    void remove(UUID uuid) {
        if (uuid != null) {
            this.activityStates.remove(uuid);
        }
    }

    boolean sessionReady(UUID uuid, long nowMillis, int thresholdMinutes) {
        ActivityState state = this.state(uuid);
        if (state == null) {
            return false;
        }
        if (state.sessionStartedMillis <= 0L) {
            state.sessionStartedMillis = nowMillis;
            return false;
        }
        long thresholdMillis = (long)thresholdMinutes * 60000L;
        return nowMillis - state.sessionStartedMillis >= thresholdMillis;
    }

    void markSessionShown(UUID uuid, long nowMillis) {
        ActivityState state = this.state(uuid);
        if (state != null) {
            state.sessionStartedMillis = nowMillis;
        }
    }

    boolean recordProgress(UUID uuid, String trigger, int amount, int threshold) {
        ActivityState state = this.state(uuid);
        if (state == null || trigger == null || amount <= 0) {
            return false;
        }
        int total = state.counters.getOrDefault(trigger, 0) + amount;
        state.counters.put(trigger, total);
        return total >= threshold;
    }

    void resetProgress(UUID uuid, String trigger) {
        ActivityState state = this.state(uuid);
        if (state != null && trigger != null) {
            state.counters.put(trigger, 0);
        }
    }

    boolean canShow(UUID uuid, String trigger, long nowMillis, int globalCooldownSeconds, int triggerCooldownSeconds) {
        ActivityState state = this.state(uuid);
        if (state == null || trigger == null) {
            return false;
        }
        if (nowMillis - state.lastGlobalMillis < (long)globalCooldownSeconds * 1000L) {
            return false;
        }
        long lastTriggerMillis = state.lastShownMillis.getOrDefault(trigger, 0L);
        return lastTriggerMillis <= 0L || nowMillis - lastTriggerMillis >= (long)triggerCooldownSeconds * 1000L;
    }

    void markShown(UUID uuid, String trigger, long nowMillis) {
        ActivityState state = this.state(uuid);
        if (state != null && trigger != null) {
            state.lastShownMillis.put(trigger, nowMillis);
            state.lastGlobalMillis = nowMillis;
        }
    }

    int defaultThreshold(String trigger) {
        return switch (trigger) {
            case "break-blocks" -> 500;
            case "place-blocks" -> 350;
            case "pickup-items" -> 240;
            case "craft-items" -> 64;
            case "damage-survived" -> 7;
            case "session-minutes" -> 30;
            default -> 100;
        };
    }

    private ActivityState state(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return this.activityStates.computeIfAbsent(uuid, ignored -> new ActivityState());
    }

    private static final class ActivityState {
        private final Map<String, Integer> counters = new HashMap<String, Integer>();
        private final Map<String, Long> lastShownMillis = new HashMap<String, Long>();
        private long lastGlobalMillis;
        private long sessionStartedMillis;

        private ActivityState() {
        }
    }
}
