package dev.lemonos;

import org.bukkit.configuration.file.FileConfiguration;

final class BackendHudConfigMigrationService {
    private final BackendConfigMigrationService configMigrationService;

    BackendHudConfigMigrationService(BackendConfigMigrationService configMigrationService) {
        this.configMigrationService = configMigrationService;
    }

    boolean setBoardDefaults(FileConfiguration config, Board board) {
        String configPath = board.configPath();
        boolean changed = false;
        changed |= this.configMigrationService.setMissing(config, configPath + ".enabled", false);
        changed |= this.configMigrationService.setMissing(config, configPath + ".title", board.defaultTitle());
        changed |= this.configMigrationService.setMissing(config, configPath + ".subtitle", board.defaultSubtitle());
        changed |= this.configMigrationService.setMissing(config, configPath + ".bottom-line", board.defaultBottomLine());
        changed |= this.configMigrationService.setMissing(config, configPath + ".refresh-minutes", 1);
        changed |= this.configMigrationService.setMissing(config, configPath + ".top", 5);
        changed |= this.configMigrationService.setMissing(config, configPath + ".name-width", 12);
        if (board.trackBlocksChanged()) {
            changed |= this.configMigrationService.setMissing(config, configPath + ".scoring.track-blocks-changed", true);
        }
        String displayPath = configPath + ".display";
        changed |= this.setDisplayDefaults(config, displayPath, board.defaultX(), board.defaultY(), board.defaultZ());
        changed |= this.migrateGeneratedDefaults(config, board, displayPath);
        changed |= this.migrateAutoChainDisplay(config, board, displayPath);
        return changed;
    }

    private boolean setDisplayDefaults(FileConfiguration config, String path, double x, double y, double z) {
        boolean changed = false;
        changed |= this.configMigrationService.setMissing(config, path + ".world", "world");
        changed |= this.configMigrationService.setMissing(config, path + ".x", x);
        changed |= this.configMigrationService.setMissing(config, path + ".y", y);
        changed |= this.configMigrationService.setMissing(config, path + ".z", z);
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
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.title-offset-y", 0.15);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.title-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.subtitle-offset-x", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.subtitle-offset-y", -0.10);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.subtitle-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.row-start-offset-x", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.row-start-offset-y", -0.34);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.row-start-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.row-gap", -0.16);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-offset-x", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-offset-y", -1.02);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-offset-z", 0.0);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-scale", 0.42);
        changed |= this.configMigrationService.setMissing(config, path + ".bedrock.bottom-line-width", 260);
        return changed;
    }

    private boolean migrateGeneratedDefaults(FileConfiguration config, Board board, String displayPath) {
        boolean changed = false;
        String configPath = board.configPath();
        if ("made-room".equals(board.dataKey())) {
            if (this.positionMatches(config, displayPath, 9.20, -60.86, 0.5)) {
                this.applyDisplayBlueprintDefaults(config, board, displayPath);
                changed = true;
            }
        } else if ("grew-here".equals(board.dataKey()) && this.positionMatches(config, displayPath, 12.90, -60.86, 0.5)) {
            this.applyDisplayBlueprintDefaults(config, board, displayPath);
            changed = true;
        }
        return changed;
    }

    private boolean migrateAutoChainDisplay(FileConfiguration config, Board board, String displayPath) {
        if (!"auto-chain".equals(board.dataKey())) {
            return false;
        }
        boolean changed = false;
        String configPath = board.configPath();
        if ("Chain".equals(config.getString(configPath + ".title"))) {
            config.set(configPath + ".title", board.defaultTitle());
            changed = true;
        }
        if ("where work carries on.".equals(config.getString(configPath + ".subtitle"))) {
            config.set(configPath + ".subtitle", board.defaultSubtitle());
            changed = true;
        }
        if ("chains completed.".equals(config.getString(configPath + ".bottom-line"))) {
            config.set(configPath + ".bottom-line", board.defaultBottomLine());
            changed = true;
        }
        boolean firstLegacyPosition = this.positionMatches(config, displayPath, 12.90, -60.86, 0.5);
        boolean secondLegacyPosition = this.positionMatches(config, displayPath, 5.42, -60.86, -0.5);
        if (!firstLegacyPosition && !secondLegacyPosition) {
            return changed;
        }
        this.applyDisplayBlueprintDefaults(config, board, displayPath);
        return true;
    }

    private boolean positionMatches(FileConfiguration config, String path, double x, double y, double z) {
        return this.nearly(config.getDouble(path + ".x"), x)
                && this.nearly(config.getDouble(path + ".y"), y)
                && this.nearly(config.getDouble(path + ".z"), z);
    }

    private void applyDisplayBlueprintDefaults(FileConfiguration config, Board board, String path) {
        config.set(path + ".x", board.defaultX());
        config.set(path + ".y", board.defaultY());
        config.set(path + ".z", board.defaultZ());
        config.set(path + ".title-offset-y", 0.15);
        config.set(path + ".subtitle-offset-y", 0.0);
        config.set(path + ".bottom-offset-y", -1.52);
        config.set(path + ".name-offset-z", -0.30);
        config.set(path + ".value-offset-z", 0.46);
    }

    private boolean nearly(double value, double expected) {
        return Math.abs(value - expected) < 1.0E-6;
    }

    record Board(String dataKey, String configPath, String defaultTitle, String defaultSubtitle, String defaultBottomLine,
                 double defaultX, double defaultY, double defaultZ, boolean trackBlocksChanged) {
    }
}
