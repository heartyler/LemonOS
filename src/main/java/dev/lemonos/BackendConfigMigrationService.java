package dev.lemonos;

import org.bukkit.configuration.file.FileConfiguration;

final class BackendConfigMigrationService {
    boolean setMissing(FileConfiguration configuration, String path, Object value) {
        if (configuration == null || configuration.isSet(path)) {
            return false;
        }
        configuration.set(path, value);
        return true;
    }
}
