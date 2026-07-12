package dev.lemonos;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class BackendSandboxHistoryService {
    private final Map<UUID, Deque<Object>> undoHistory = new HashMap<UUID, Deque<Object>>();
    private final Map<UUID, Deque<Object>> redoHistory = new HashMap<UUID, Deque<Object>>();

    Object popUndo(UUID uuid) {
        Deque<Object> history = this.undoHistory.get(uuid);
        return history == null || history.isEmpty() ? null : history.pop();
    }

    Object popRedo(UUID uuid) {
        Deque<Object> history = this.redoHistory.get(uuid);
        return history == null || history.isEmpty() ? null : history.pop();
    }

    void restoreUndo(UUID uuid, Object change) {
        this.push(this.undoHistory, uuid, change);
    }

    void restoreRedo(UUID uuid, Object change) {
        this.push(this.redoHistory, uuid, change);
    }

    void moveUndoToRedo(UUID uuid, Object change) {
        this.push(this.redoHistory, uuid, change);
    }

    void moveRedoToUndo(UUID uuid, Object change) {
        this.push(this.undoHistory, uuid, change);
    }

    void record(UUID uuid, Object change, int historyLimit) {
        if (uuid == null || change == null) {
            return;
        }
        this.redoHistory.remove(uuid);
        Deque<Object> history = this.undoHistory.computeIfAbsent(uuid, ignored -> new ArrayDeque<Object>());
        history.push(change);
        while (history.size() > historyLimit) {
            history.removeLast();
        }
    }

    void clearRedo(UUID uuid) {
        if (uuid != null) {
            this.redoHistory.remove(uuid);
        }
    }

    void clear() {
        this.undoHistory.clear();
        this.redoHistory.clear();
    }

    private void push(Map<UUID, Deque<Object>> histories, UUID uuid, Object change) {
        if (uuid != null && change != null) {
            histories.computeIfAbsent(uuid, ignored -> new ArrayDeque<Object>()).push(change);
        }
    }
}
