/*
 * Backend-side LemonOS atmosphere actionbar state.
 */
package dev.lemonos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

final class BackendAtmosphereActionBarService {
    private final Map<UUID, AtmosphereState> atmosphereStates = new ConcurrentHashMap<UUID, AtmosphereState>();

    void clear() {
        this.atmosphereStates.clear();
    }

    void remove(UUID uuid) {
        if (uuid != null) {
            this.atmosphereStates.remove(uuid);
        }
    }

    boolean periodicReady(UUID uuid, long nowMillis, int cooldownSeconds) {
        AtmosphereState state = this.state(uuid);
        if (state == null) {
            return false;
        }
        if (state.lastShownMillis <= 0L) {
            state.lastShownMillis = nowMillis;
            return false;
        }
        return state.activeMessage == null && nowMillis - state.lastShownMillis >= (long)cooldownSeconds * 1000L;
    }

    boolean showReady(UUID uuid, long nowMillis, int cooldownSeconds) {
        AtmosphereState state = this.state(uuid);
        if (state == null) {
            return false;
        }
        if (state.lastShownMillis <= 0L) {
            state.lastShownMillis = nowMillis;
            return false;
        }
        return nowMillis - state.lastShownMillis >= (long)cooldownSeconds * 1000L;
    }

    boolean hasActiveMessage(UUID uuid, long nowMillis) {
        AtmosphereState state = this.state(uuid);
        return state != null && state.activeMessage != null && nowMillis <= state.activeUntilMillis;
    }

    String pickMessage(UUID uuid, String key, List<String> messages) {
        ArrayList<String> candidates = new ArrayList<String>();
        for (String message : messages == null ? List.<String>of() : messages) {
            if (message == null || message.isBlank()) {
                continue;
            }
            candidates.add(message.trim());
        }
        if (candidates.isEmpty()) {
            return null;
        }
        AtmosphereState state = this.state(uuid);
        if (state == null) {
            return null;
        }
        if (candidates.size() == 1) {
            state.lastMessages.put(key, candidates.get(0));
            return candidates.get(0);
        }
        String lastMessage = state.lastMessages.get(key);
        String picked = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        if (picked.equals(lastMessage)) {
            ArrayList<String> alternates = new ArrayList<String>(candidates);
            alternates.remove(lastMessage);
            picked = alternates.get(ThreadLocalRandom.current().nextInt(alternates.size()));
        }
        state.lastMessages.put(key, picked);
        return picked;
    }

    void activate(UUID uuid, String message, long nowMillis, int durationSeconds, boolean markShown) {
        AtmosphereState state = this.state(uuid);
        if (state == null || message == null || message.isBlank()) {
            return;
        }
        state.activeMessage = message;
        state.activeUntilMillis = nowMillis + (long)durationSeconds * 1000L;
        state.lastRepeatMillis = nowMillis;
        if (markShown) {
            state.lastShownMillis = nowMillis;
        }
    }

    List<RepeatMessage> repeatMessages(long nowMillis, int repeatSeconds) {
        ArrayList<RepeatMessage> messages = new ArrayList<RepeatMessage>();
        long repeatMillis = (long)repeatSeconds * 1000L;
        for (Map.Entry<UUID, AtmosphereState> entry : this.atmosphereStates.entrySet()) {
            AtmosphereState state = entry.getValue();
            if (state.activeMessage == null || nowMillis > state.activeUntilMillis) {
                state.activeMessage = null;
                continue;
            }
            if (nowMillis - state.lastRepeatMillis < repeatMillis) {
                continue;
            }
            messages.add(new RepeatMessage(entry.getKey(), state.activeMessage));
        }
        return messages;
    }

    void markRepeated(UUID uuid, long nowMillis) {
        AtmosphereState state = this.state(uuid);
        if (state != null) {
            state.lastRepeatMillis = nowMillis;
        }
    }

    private AtmosphereState state(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return this.atmosphereStates.computeIfAbsent(uuid, ignored -> new AtmosphereState());
    }

    static final class RepeatMessage {
        private final UUID uuid;
        private final String message;

        RepeatMessage(UUID uuid, String message) {
            this.uuid = uuid;
            this.message = message;
        }

        UUID uuid() {
            return this.uuid;
        }

        String message() {
            return this.message;
        }
    }

    private static final class AtmosphereState {
        private final Map<String, String> lastMessages = new HashMap<String, String>();
        private String activeMessage;
        private long activeUntilMillis;
        private long lastRepeatMillis;
        private long lastShownMillis;

        private AtmosphereState() {
        }
    }
}
