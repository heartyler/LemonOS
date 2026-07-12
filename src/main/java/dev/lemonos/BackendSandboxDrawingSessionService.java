package dev.lemonos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;

final class BackendSandboxDrawingSessionService {
    private final Map<UUID, Object> drawings = new HashMap<UUID, Object>();
    private final Map<UUID, BukkitTask> idleTimeouts = new HashMap<UUID, BukkitTask>();

    boolean contains(UUID uuid) {
        return uuid != null && this.drawings.containsKey(uuid);
    }

    Object get(UUID uuid) {
        return uuid == null ? null : this.drawings.get(uuid);
    }

    void put(UUID uuid, Object drawing) {
        if (uuid != null && drawing != null) {
            this.drawings.put(uuid, drawing);
        }
    }

    Object remove(UUID uuid) {
        return uuid == null ? null : this.drawings.remove(uuid);
    }

    void setIdleTimeout(UUID uuid, BukkitTask task) {
        if (uuid != null && task != null) {
            this.idleTimeouts.put(uuid, task);
        }
    }

    BukkitTask removeIdleTimeout(UUID uuid) {
        return uuid == null ? null : this.idleTimeouts.remove(uuid);
    }

    List<BukkitTask> idleTimeouts() {
        return List.copyOf(this.idleTimeouts.values());
    }

    List<DrawingEntry> drawingEntries() {
        ArrayList<DrawingEntry> entries = new ArrayList<DrawingEntry>();
        for (Map.Entry<UUID, Object> entry : this.drawings.entrySet()) {
            entries.add(new DrawingEntry(entry.getKey(), entry.getValue()));
        }
        return entries;
    }

    void clear() {
        this.drawings.clear();
        this.idleTimeouts.clear();
    }

    record DrawingEntry(UUID uuid, Object drawing) {
    }
}
