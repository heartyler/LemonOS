package dev.lemonos;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import dev.lemonos.storage.BackendYamlStore;
import org.bukkit.configuration.file.FileConfiguration;

/** Coordinates config migration, persistence, and grouped defaults during startup. */
final class BackendConfigMigrationOrchestrator {
    private final BackendFeatureConfigMigrationService featureMigrationService;
    private final BackendConfigDefaultGroupService defaultGroupService;
    private final Logger logger;
    private final BackendYamlStore yamlStore;

    BackendConfigMigrationOrchestrator(
            BackendFeatureConfigMigrationService featureMigrationService,
            BackendConfigDefaultGroupService defaultGroupService,
            Logger logger,
            BackendYamlStore yamlStore) {
        this.featureMigrationService = featureMigrationService;
        this.defaultGroupService = defaultGroupService;
        this.logger = logger;
        this.yamlStore = yamlStore;
    }

    void migrate(Target target, LegacyDefaults legacyDefaults, List<BackendConfigDefaultGroupService.PlaceDefault> placeDefaults) {
        LegacyDefaultResult legacy = legacyDefaults.apply();

        boolean boardsChanged = this.featureMigrationService.migrateBoards(
                target.config(), target.boards(), target.boardsFileCreated());
        boolean atmosphereChanged = this.featureMigrationService.migrateAtmosphere(
                target.config(), target.atmosphere(), target.atmosphereFileCreated());
        boolean atmosphereSaved = this.save(
                target.atmosphereFile(), target.atmosphere(), atmosphereChanged || legacy.atmosphereChanged());
        this.save(target.boardsFile(), target.boards(), boardsChanged || legacy.boardsChanged());
        boolean configChanged = legacy.configChanged();
        if (atmosphereSaved && target.config().contains("atmosphere")) {
            target.config().set("atmosphere", null);
            configChanged = true;
        }
        this.save(target.configFile(), target.config(), configChanged);

        this.save(target.messagesFile(), target.messages(), this.defaultGroupService.applyMessageDefaults(target.messages()));
        this.save(target.placesFile(), target.places(), this.defaultGroupService.applyPlaceDefaults(target.places(), placeDefaults));
        this.save(target.sandboxFile(), target.sandbox(), this.defaultGroupService.applySandboxDefaults(target.sandbox()));
        this.save(target.survivalFile(), target.survival(), this.defaultGroupService.applySurvivalDefaults(target.survival()));
    }

    private boolean save(File file, FileConfiguration configuration, boolean changed) {
        if (!changed) {
            return true;
        }
        if (file == null || configuration == null) {
            return false;
        }
        try {
            this.yamlStore.saveAtomic(configuration, file);
            this.logger.info("LemonOS config migration added missing defaults to " + file.getName() + ".");
            return true;
        }
        catch (IOException exception) {
            this.logger.warning("Unable to save migrated LemonOS config file " + file.getName() + ": " + exception.getMessage());
            return false;
        }
    }

    @FunctionalInterface
    interface LegacyDefaults {
        LegacyDefaultResult apply();
    }

    record LegacyDefaultResult(boolean configChanged, boolean boardsChanged, boolean atmosphereChanged) {
    }

    record Target(
            File configFile,
            File messagesFile,
            File placesFile,
            File sandboxFile,
            File survivalFile,
            File boardsFile,
            File atmosphereFile,
            FileConfiguration config,
            FileConfiguration messages,
            FileConfiguration places,
            FileConfiguration sandbox,
            FileConfiguration survival,
            FileConfiguration boards,
            FileConfiguration atmosphere,
            boolean boardsFileCreated,
            boolean atmosphereFileCreated) {
    }
}
