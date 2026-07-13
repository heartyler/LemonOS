package dev.lemonos;

import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendFeatureConfigMigrationService {
    private final BackendConfigMigrationService migration;

    BackendFeatureConfigMigrationService(BackendConfigMigrationService migration) {
        this.migration = migration;
    }

    boolean migrateHud(FileConfiguration legacy, FileConfiguration legacyBoards, FileConfiguration hud, boolean overwriteFromLegacy) {
        boolean changed = this.metadata(hud);
        changed |= this.copySection(legacy, "stayed-close", hud, "hud.stayed-close", overwriteFromLegacy, false);
        changed |= this.copySection(legacy, "hud", hud, "hud", overwriteFromLegacy, false);
        changed |= this.copySection(legacyBoards, "boards", hud, "hud", overwriteFromLegacy, false);
        for (String key : new String[]{"stayed-close", "made-room", "grew-here", "auto-chain"}) {
            String path = "hud." + key + ".enabled";
            if (!hud.isBoolean(path)) {
                hud.set(path, false);
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

    RecipeMigration migrateRecipes(FileConfiguration survival, FileConfiguration recipes, boolean overwriteFromLegacy) {
        boolean recipesChanged = this.metadata(recipes);
        String legacyPath = "survival.recipe-book.unlock-all";
        String canonicalPath = "recipe-book.unlock-all.survival";
        if (survival != null && survival.isBoolean(legacyPath)
                && (overwriteFromLegacy || !recipes.isSet(canonicalPath))) {
            boolean legacyValue = survival.getBoolean(legacyPath, true);
            if (!recipes.isBoolean(canonicalPath) || recipes.getBoolean(canonicalPath) != legacyValue) {
                recipes.set(canonicalPath, legacyValue);
                recipesChanged = true;
            }
        }
        recipesChanged |= this.migration.setMissing(recipes, canonicalPath, true);
        recipesChanged |= this.migration.setMissing(recipes, "recipe-book.unlock-all.creative", true);
        return new RecipeMigration(recipesChanged, survival != null && survival.contains("survival.recipe-book"));
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

    record RecipeMigration(boolean recipesChanged, boolean retireLegacySurvivalSection) {
    }
}
