/*
 * Backend-side LemonOS backup metadata persistence.
 */
package dev.lemonos;

import org.bukkit.configuration.file.FileConfiguration;

final class BackendBackupMetadataService {
    String lastCopy(FileConfiguration backups, String serverProxyName) {
        String value = backups == null ? null : backups.getString(this.lastCopyPath(serverProxyName));
        return value == null || value.isBlank() ? null : value;
    }

    void setLastCopy(FileConfiguration backups, String serverProxyName, String timestamp) {
        backups.set(this.lastCopyPath(serverProxyName), (Object)timestamp);
    }

    private String lastCopyPath(String serverProxyName) {
        return "last_copy." + serverProxyName;
    }
}
