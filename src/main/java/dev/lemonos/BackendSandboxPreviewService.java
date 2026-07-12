package dev.lemonos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class BackendSandboxPreviewService {
    private final Map<UUID, Object> clonePreviews = new HashMap<UUID, Object>();
    private final Map<UUID, Object> clearPreviews = new HashMap<UUID, Object>();
    private final Map<UUID, Object> rotatePreviews = new HashMap<UUID, Object>();
    private final Map<UUID, Object> flipPreviews = new HashMap<UUID, Object>();

    boolean hasAny(UUID uuid) {
        return this.hasClone(uuid) || this.hasClear(uuid) || this.hasRotate(uuid) || this.hasFlip(uuid);
    }

    boolean hasClone(UUID uuid) {
        return uuid != null && this.clonePreviews.containsKey(uuid);
    }

    Object clonePreview(UUID uuid) {
        return uuid == null ? null : this.clonePreviews.get(uuid);
    }

    boolean hasClear(UUID uuid) {
        return uuid != null && this.clearPreviews.containsKey(uuid);
    }

    boolean hasRotate(UUID uuid) {
        return uuid != null && this.rotatePreviews.containsKey(uuid);
    }

    boolean hasFlip(UUID uuid) {
        return uuid != null && this.flipPreviews.containsKey(uuid);
    }

    void setClone(UUID uuid, Object preview) {
        this.set(this.clonePreviews, uuid, preview);
    }

    void setClear(UUID uuid, Object preview) {
        this.set(this.clearPreviews, uuid, preview);
    }

    void setRotate(UUID uuid, Object preview) {
        this.set(this.rotatePreviews, uuid, preview);
    }

    void setFlip(UUID uuid, Object preview) {
        this.set(this.flipPreviews, uuid, preview);
    }

    Object removeClone(UUID uuid) {
        return this.remove(this.clonePreviews, uuid);
    }

    Object removeClear(UUID uuid) {
        return this.remove(this.clearPreviews, uuid);
    }

    Object removeRotate(UUID uuid) {
        return this.remove(this.rotatePreviews, uuid);
    }

    Object removeFlip(UUID uuid) {
        return this.remove(this.flipPreviews, uuid);
    }

    boolean clearAllFor(UUID uuid) {
        boolean changed = this.removeClone(uuid) != null;
        changed = this.removeClear(uuid) != null || changed;
        changed = this.removeRotate(uuid) != null || changed;
        changed = this.removeFlip(uuid) != null || changed;
        return changed;
    }

    void clearAll() {
        this.clonePreviews.clear();
        this.clearPreviews.clear();
        this.rotatePreviews.clear();
        this.flipPreviews.clear();
    }

    List<PreviewEntry> cloneEntries() {
        return this.entries(this.clonePreviews);
    }

    List<PreviewEntry> clearEntries() {
        return this.entries(this.clearPreviews);
    }

    List<PreviewEntry> rotateEntries() {
        return this.entries(this.rotatePreviews);
    }

    List<PreviewEntry> flipEntries() {
        return this.entries(this.flipPreviews);
    }

    private void set(Map<UUID, Object> previews, UUID uuid, Object preview) {
        if (uuid != null && preview != null) {
            previews.put(uuid, preview);
        }
    }

    private Object remove(Map<UUID, Object> previews, UUID uuid) {
        return uuid == null ? null : previews.remove(uuid);
    }

    private List<PreviewEntry> entries(Map<UUID, Object> previews) {
        ArrayList<PreviewEntry> entries = new ArrayList<PreviewEntry>();
        for (Map.Entry<UUID, Object> entry : previews.entrySet()) {
            entries.add(new PreviewEntry(entry.getKey(), entry.getValue()));
        }
        return entries;
    }

    record PreviewEntry(UUID uuid, Object preview) {
    }
}
