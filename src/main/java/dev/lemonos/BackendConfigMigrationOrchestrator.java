package dev.lemonos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    MigrationResult migrate(Target target, LegacyDefaults legacyDefaults, List<BackendConfigDefaultGroupService.PlaceDefault> placeDefaults) {
        boolean hudChanged = this.featureMigrationService.migrateHud(
                target.config(), target.legacyBoards(), target.hud(), target.hudFileCreated());
        BackendFeatureConfigMigrationService.RecipeMigration recipeMigration = this.featureMigrationService.migrateRecipes(
                target.survival(), target.recipes(), target.recipesFileCreated());
        LegacyDefaultResult legacy = legacyDefaults.apply();
        boolean atmosphereChanged = this.featureMigrationService.migrateAtmosphere(
                target.config(), target.atmosphere(), target.atmosphereFileCreated());
        boolean atmosphereSaved = this.save(
                target.atmosphereFile(), target.atmosphere(), atmosphereChanged || legacy.atmosphereChanged());
        boolean hudSaved = this.save(target.hudFile(), target.hud(), hudChanged || legacy.hudChanged());
        boolean recipesSaved = this.save(target.recipesFile(), target.recipes(), recipeMigration.recipesChanged());
        boolean configChanged = legacy.configChanged();
        if (hudSaved) {
            if (target.config().contains("stayed-close")) {
                target.config().set("stayed-close", null);
                configChanged = true;
            }
            if (target.config().contains("hud")) {
                target.config().set("hud", null);
                configChanged = true;
            }
            this.removeLegacyBoardsFile(target.legacyBoardsFile());
        }
        if (atmosphereSaved && target.config().contains("atmosphere")) {
            target.config().set("atmosphere", null);
            configChanged = true;
        }
        this.save(target.configFile(), target.config(), configChanged);

        this.save(target.messagesFile(), target.messages(), this.defaultGroupService.applyMessageDefaults(target.messages()));
        this.save(target.placesFile(), target.places(), this.defaultGroupService.applyPlaceDefaults(target.places(), placeDefaults));
        this.save(target.sandboxFile(), target.sandbox(), this.defaultGroupService.applySandboxDefaults(target.sandbox()));
        boolean survivalChanged = this.defaultGroupService.applySurvivalDefaults(target.survival());
        if (recipesSaved && recipeMigration.retireLegacySurvivalSection()
                && target.survival().contains("survival.recipe-book")) {
            target.survival().set("survival.recipe-book", null);
            survivalChanged = true;
        }
        this.save(target.survivalFile(), target.survival(), survivalChanged);
        return new MigrationResult(hudSaved, atmosphereSaved, recipesSaved);
    }

    private void removeLegacyBoardsFile(File file) {
        if (file == null || !file.isFile()) return;
        try {
            Files.deleteIfExists(file.toPath());
            Files.deleteIfExists(file.toPath().resolveSibling(file.getName() + ".lock"));
            this.logger.info("LemonOS config migration retired legacy boards.yml after hud.yml was saved.");
        }
        catch (IOException exception) {
            this.logger.warning("Unable to retire legacy LemonOS config file boards.yml: " + exception.getMessage());
        }
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

    record LegacyDefaultResult(boolean configChanged, boolean hudChanged, boolean atmosphereChanged) {
    }

    record MigrationResult(boolean hudReady, boolean atmosphereReady, boolean recipesReady) {
    }

    record Target(
            File configFile,
            File messagesFile,
            File placesFile,
            File sandboxFile,
            File survivalFile,
            File hudFile,
            File legacyBoardsFile,
            File atmosphereFile,
            File recipesFile,
            FileConfiguration config,
            FileConfiguration messages,
            FileConfiguration places,
            FileConfiguration sandbox,
            FileConfiguration survival,
            FileConfiguration hud,
            FileConfiguration legacyBoards,
            FileConfiguration atmosphere,
            FileConfiguration recipes,
            boolean hudFileCreated,
            boolean atmosphereFileCreated,
            boolean recipesFileCreated) {
    }
}
