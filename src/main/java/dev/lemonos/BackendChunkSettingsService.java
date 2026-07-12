/*
 * Backend-side LemonOS chunk generation settings persistence.
 */
package dev.lemonos;

import org.bukkit.configuration.file.FileConfiguration;

final class BackendChunkSettingsService {
    boolean ensureDefaults(FileConfiguration chunks, String serverProxyName, String defaultDimension, int defaultSize, String defaultStatus) {
        String path = this.chunkPath(serverProxyName);
        boolean changed = false;
        if (!chunks.isString(path + ".dimension")) {
            chunks.set(path + ".dimension", (Object)defaultDimension);
            changed = true;
        }
        if (!chunks.isInt(path + ".size")) {
            chunks.set(path + ".size", (Object)defaultSize);
            changed = true;
        }
        if (!chunks.isString(path + ".status")) {
            chunks.set(path + ".status", (Object)defaultStatus);
            return true;
        }
        String status = chunks.getString(path + ".status");
        if ("not started.".equalsIgnoreCase(status) || "unavailable.".equalsIgnoreCase(status)) {
            chunks.set(path + ".status", (Object)defaultStatus);
            changed = true;
        }
        return changed;
    }

    String dimension(FileConfiguration chunks, String serverProxyName, String defaultDimension) {
        return chunks.getString(this.chunkPath(serverProxyName) + ".dimension", defaultDimension);
    }

    int size(FileConfiguration chunks, String serverProxyName, int defaultSize) {
        return chunks.getInt(this.chunkPath(serverProxyName) + ".size", defaultSize);
    }

    String status(FileConfiguration chunks, String serverProxyName) {
        String status = chunks == null ? null : chunks.getString(this.chunkPath(serverProxyName) + ".status", "idle.");
        if ("not started.".equalsIgnoreCase(String.valueOf(status))) {
            return "idle.";
        }
        if ("waiting.".equals(status) || "running.".equals(status) || "done.".equals(status) || "unavailable.".equals(status) || "idle.".equals(status) || String.valueOf(status).matches("[0-9]{1,3}% ready")) {
            return status;
        }
        return "idle.";
    }

    void setStatus(FileConfiguration chunks, String serverProxyName, String status) {
        chunks.set(this.chunkPath(serverProxyName) + ".status", (Object)status);
    }

    void setStatusAndDimension(FileConfiguration chunks, String serverProxyName, String status, String dimension) {
        String path = this.chunkPath(serverProxyName);
        chunks.set(path + ".status", (Object)status);
        chunks.set(path + ".dimension", (Object)dimension);
    }

    void setDimension(FileConfiguration chunks, String serverProxyName, String dimension) {
        chunks.set(this.chunkPath(serverProxyName) + ".dimension", (Object)dimension);
    }

    void setSize(FileConfiguration chunks, String serverProxyName, int size) {
        chunks.set(this.chunkPath(serverProxyName) + ".size", (Object)size);
    }

    void setCenter(FileConfiguration chunks, String serverProxyName, String dimension, int x, int z) {
        String path = this.centerPath(serverProxyName, dimension);
        chunks.set(path + ".x", (Object)x);
        chunks.set(path + ".z", (Object)z);
    }

    boolean hasCenter(FileConfiguration chunks, String serverProxyName, String dimension) {
        String path = this.centerPath(serverProxyName, dimension);
        return chunks != null && chunks.isInt(path + ".x") && chunks.isInt(path + ".z");
    }

    int centerX(FileConfiguration chunks, String serverProxyName, String dimension) {
        return chunks.getInt(this.centerPath(serverProxyName, dimension) + ".x");
    }

    int centerZ(FileConfiguration chunks, String serverProxyName, String dimension) {
        return chunks.getInt(this.centerPath(serverProxyName, dimension) + ".z");
    }

    private String centerPath(String serverProxyName, String dimension) {
        return this.chunkPath(serverProxyName) + ".centers." + dimension;
    }

    private String chunkPath(String serverProxyName) {
        return "chunks." + serverProxyName;
    }
}
