package dev.lemonos;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class BackendCareWorldStatusService {
    private final Map<UUID, String> lastStatuses = new ConcurrentHashMap<UUID, String>();
    private final java.util.Set<UUID> viewers = ConcurrentHashMap.newKeySet();

    boolean shouldActivate(boolean bedrockPlayer) {
        return !bedrockPlayer;
    }

    void activate(UUID uuid) {
        if (uuid != null) {
            this.viewers.add(uuid);
        }
    }

    boolean contains(UUID uuid) {
        return uuid != null && this.viewers.contains(uuid);
    }

    List<UUID> viewerSnapshot() {
        return List.copyOf(this.viewers);
    }

    void clear(UUID uuid) {
        if (uuid == null) {
            return;
        }
        this.viewers.remove(uuid);
        this.lastStatuses.remove(uuid);
    }

    void clearAll() {
        this.viewers.clear();
        this.lastStatuses.clear();
    }

    boolean removeLastStatus(UUID uuid) {
        return uuid != null && this.lastStatuses.remove(uuid) != null;
    }

    String takeLastStatus(UUID uuid) {
        return uuid == null ? null : this.lastStatuses.remove(uuid);
    }

    String rememberStatus(UUID uuid, String status) {
        if (uuid == null) {
            return null;
        }
        return status == null ? this.lastStatuses.remove(uuid) : this.lastStatuses.put(uuid, status);
    }
}
