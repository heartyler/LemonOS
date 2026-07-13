package dev.lemonos;

import org.bukkit.configuration.file.FileConfiguration;

final class BackendHudConfigMigrationService {
    private final BackendConfigMigrationService configMigrationService;

    BackendHudConfigMigrationService(BackendConfigMigrationService configMigrationService) {
        this.configMigrationService = configMigrationService;
    }

    boolean setHudDefaults(FileConfiguration config, Hud hud) {
        String configPath = hud.configPath();
        boolean changed = false;
        changed |= this.configMigrationService.setMissing(config, configPath + ".enabled", false);
        changed |= this.configMigrationService.setMissing(config, configPath + ".title", hud.defaultTitle());
        changed |= this.configMigrationService.setMissing(config, configPath + ".subtitle", hud.defaultSubtitle());
        changed |= this.configMigrationService.setMissing(config, configPath + ".bottom-line", hud.defaultBottomLine());
        changed |= this.configMigrationService.setMissing(config, configPath + ".refresh-minutes", 1);
        changed |= this.configMigrationService.setMissing(config, configPath + ".top", 5);
        changed |= this.configMigrationService.setMissing(config, configPath + ".name-width", 12);
        if (hud.trackBlocksChanged()) {
            changed |= this.configMigrationService.setMissing(config, configPath + ".scoring.track-blocks-changed", true);
        }
        String displayPath = configPath + ".display";
        changed |= this.setDisplayDefaults(config, hud, displayPath);
        changed |= this.migrateGeneratedDefaults(config, hud, displayPath);
        changed |= this.migrateAutoChainDisplay(config, hud, displayPath);
        return changed;
    }

    private boolean setDisplayDefaults(FileConfiguration config, Hud hud, String path) {
        boolean changed = false;
        changed |= this.configMigrationService.setMissing(config, path + ".world", "world");
        changed |= this.configMigrationService.setMissing(config, path + ".x", hud.defaultX());
        changed |= this.configMigrationService.setMissing(config, path + ".y", hud.defaultY());
        changed |= this.configMigrationService.setMissing(config, path + ".z", hud.defaultZ());
        changed |= this.configMigrationService.setMissing(config, path + ".yaw", 90.0);
        changed |= this.configMigrationService.setMissing(config, path + ".pitch", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".billboard", "fixed");
        changed |= this.configMigrationService.setMissing(config, path + ".scale", 0.53);
        changed |= this.configMigrationService.setMissing(config, path + ".title-offset-x", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".title-offset-y", 0.15);
        changed |= this.configMigrationService.setMissing(config, path + ".title-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".subtitle-offset-y", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".row-start-offset-y", -0.34);
        changed |= this.configMigrationService.setMissing(config, path + ".row-gap", -0.13);
        changed |= this.configMigrationService.setMissing(config, path + ".bottom-offset-y", -1.52);
        changed |= this.configMigrationService.setMissing(config, path + ".bottom-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bottom-scale", 0.42);
        changed |= this.configMigrationService.setMissing(config, path + ".bottom-line-width", 260);
        changed |= this.configMigrationService.setMissing(config, path + ".name-offset-z", -0.30);
        changed |= this.configMigrationService.setMissing(config, path + ".value-offset-z", 0.46);
        changed |= this.configMigrationService.setMissing(config, path + ".background-alpha", 0);
        changed |= this.configMigrationService.setMissing(config, path + ".view-range", 32);
        changed |= this.configMigrationService.setMissing(config, path + ".line-width", 220);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.enabled", false);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.scale", 0.53);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.line-width", 260);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.background-alpha", 0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.name-width", 12);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.title-offset-x", 0.0);
        boolean stayedClose = "stayed-close".equals(hud.dataKey());
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.title-offset-y", stayedClose ? 0.27 : 0.15);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.title-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.subtitle-offset-x", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.subtitle-offset-y", stayedClose ? -0.20 : -0.10);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.subtitle-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.row-start-offset-x", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.row-start-offset-y", -0.34);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.row-start-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.row-gap", stayedClose ? -0.27 : -0.16);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-offset-x", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-offset-y", -1.02);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-scale", 0.42);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-line-width", 260);
        return changed;
    }

    private boolean migrateGeneratedDefaults(FileConfiguration config, Hud hud, String displayPath) {
        boolean changed = false;
        if ("made-room".equals(hud.dataKey())) {
            if (this.positionMatches(config, displayPath, 9.20, -60.86, 0.5)) {
                this.applyDisplayBlueprintDefaults(config, hud, displayPath);
                changed = true;
            }
        } else if ("grew-here".equals(hud.dataKey()) && this.positionMatches(config, displayPath, 12.90, -60.86, 0.5)) {
            this.applyDisplayBlueprintDefaults(config, hud, displayPath);
            changed = true;
        }
        return changed;
    }

    private boolean migrateAutoChainDisplay(FileConfiguration config, Hud hud, String displayPath) {
        if (!"auto-chain".equals(hud.dataKey())) {
            return false;
        }
        boolean changed = false;
        String configPath = hud.configPath();
        if ("Chain".equals(config.getString(configPath + ".title"))) {
            config.set(configPath + ".title", hud.defaultTitle());
            changed = true;
        }
        if ("where work carries on.".equals(config.getString(configPath + ".subtitle"))) {
            config.set(configPath + ".subtitle", hud.defaultSubtitle());
            changed = true;
        }
        if ("chains completed.".equals(config.getString(configPath + ".bottom-line"))) {
            config.set(configPath + ".bottom-line", hud.defaultBottomLine());
            changed = true;
        }
        boolean firstLegacyPosition = this.positionMatches(config, displayPath, 12.90, -60.86, 0.5);
        boolean secondLegacyPosition = this.positionMatches(config, displayPath, 5.42, -60.86, -0.5);
        if (!firstLegacyPosition && !secondLegacyPosition) {
            return changed;
        }
        this.applyDisplayBlueprintDefaults(config, hud, displayPath);
        return true;
    }

    private boolean positionMatches(FileConfiguration config, String path, double x, double y, double z) {
        return this.nearly(config.getDouble(path + ".x"), x)
                && this.nearly(config.getDouble(path + ".y"), y)
                && this.nearly(config.getDouble(path + ".z"), z);
    }

    private void applyDisplayBlueprintDefaults(FileConfiguration config, Hud hud, String path) {
        config.set(path + ".x", hud.defaultX());
        config.set(path + ".y", hud.defaultY());
        config.set(path + ".z", hud.defaultZ());
        config.set(path + ".title-offset-y", 0.15);
        config.set(path + ".subtitle-offset-y", 0.0);
        config.set(path + ".bottom-offset-y", -1.52);
        config.set(path + ".name-offset-z", -0.30);
        config.set(path + ".value-offset-z", 0.46);
    }

    private boolean nearly(double value, double expected) {
        return Math.abs(value - expected) < 1.0E-6;
    }

    record Hud(String dataKey, String configPath, String defaultTitle, String defaultSubtitle, String defaultBottomLine,
                 double defaultX, double defaultY, double defaultZ, boolean trackBlocksChanged) {
    }
}
