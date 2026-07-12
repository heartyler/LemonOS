package dev.lemonos;

import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendFeatureConfigMigrationService {
    private final BackendConfigMigrationService migration;

    BackendFeatureConfigMigrationService(BackendConfigMigrationService migration) {
        this.migration = migration;
    }

    boolean migrateBoards(FileConfiguration legacy, FileConfiguration boards, boolean overwriteFromLegacy) {
        boolean changed = this.metadata(boards);
        changed |= this.copySection(legacy, "stayed-close", boards, "boards.stayed-close", overwriteFromLegacy, false);
        changed |= this.copySection(legacy, "hud.made-room", boards, "boards.made-room", overwriteFromLegacy, false);
        changed |= this.copySection(legacy, "hud.grew-here", boards, "boards.grew-here", overwriteFromLegacy, false);
        changed |= this.copySection(legacy, "hud.auto-chain", boards, "boards.auto-chain", overwriteFromLegacy, false);
        for (String key : new String[]{"stayed-close", "made-room", "grew-here", "auto-chain"}) {
            String path = "boards." + key + ".enabled";
            if (!boards.isBoolean(path)) {
                boards.set(path, false);
                changed = true;
            }
        }
        return changed;
    }

    boolean migrateAtmosphere(FileConfiguration legacy, FileConfiguration atmosphere, boolean overwriteFromLegacy) {
        boolean changed = this.metadata(atmosphere);
        changed |= this.copySection(legacy, "atmosphere", atmosphere, "atmosphere", overwriteFromLegacy, true);
        if (atmosphere.contains("atmosphere.servers")) {
            atmosphere.set("atmosphere.servers", null);
            changed = true;
        }
        changed |= this.migration.setMissing(atmosphere, "atmosphere.enabled", true);
        return changed;
    }

    private boolean metadata(FileConfiguration target) {
        boolean changed = false;
        changed |= this.migration.setMissing(target, "package.name", "Honey");
        changed |= this.migration.setMissing(target, "package.version", "26.2");
        changed |= this.migration.setMissing(target, "lemonos.version", "1.0");
        return changed;
    }

    private boolean copySection(FileConfiguration source, String sourcePath, FileConfiguration target,
            String targetPath, boolean overwrite, boolean skipServers) {
        ConfigurationSection section = source == null ? null : source.getConfigurationSection(sourcePath);
        if (section == null) return false;
        boolean changed = false;
        for (Map.Entry<String, Object> entry : section.getValues(true).entrySet()) {
            String relative = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof ConfigurationSection) continue;
            if (skipServers && ("servers".equals(relative) || relative.startsWith("servers."))) continue;
            String path = targetPath + "." + relative;
            if (!overwrite && target.contains(path)) continue;
            Object current = target.get(path);
            if (current == null || !current.equals(value)) {
                target.set(path, value);
                changed = true;
            }
        }
        return changed;
    }
}
