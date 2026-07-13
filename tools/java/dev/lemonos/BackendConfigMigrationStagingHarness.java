package dev.lemonos;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;
import dev.lemonos.storage.BackendYamlStore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class BackendConfigMigrationStagingHarness {
    private BackendConfigMigrationStagingHarness() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("Expected: <legacy-config> <preset-directory> <output-directory>");
        }
        Path legacyPath = Path.of(args[0]);
        Path presetDirectory = Path.of(args[1]);
        Path outputDirectory = Path.of(args[2]);
        Files.createDirectories(outputDirectory);

        Path configPath = outputDirectory.resolve("config.yml");
        Path hudPath = outputDirectory.resolve("hud.yml");
        Path atmospherePath = outputDirectory.resolve("atmosphere.yml");
        Path recipesPath = outputDirectory.resolve("recipes.yml");
        Files.copy(legacyPath, configPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(presetDirectory.resolve("hud.yml"), hudPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(presetDirectory.resolve("atmosphere.yml"), atmospherePath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(presetDirectory.resolve("recipes.yml"), recipesPath, StandardCopyOption.REPLACE_EXISTING);

        FileConfiguration config = YamlConfiguration.loadConfiguration(configPath.toFile());
        FileConfiguration hud = YamlConfiguration.loadConfiguration(hudPath.toFile());
        FileConfiguration legacyBoards = new YamlConfiguration();
        legacyBoards.set("boards.grew-here.title", "Current Grow");
        FileConfiguration atmosphere = YamlConfiguration.loadConfiguration(atmospherePath.toFile());
        FileConfiguration recipes = YamlConfiguration.loadConfiguration(recipesPath.toFile());
        FileConfiguration legacySurvival = new YamlConfiguration();
        legacySurvival.set("survival.recipe-book.unlock-all", false);
        legacySurvival.set("survival.recipe-book.silent", true);
        BackendConfigMigrationService base = new BackendConfigMigrationService();
        BackendMainConfigDefaultService main = new BackendMainConfigDefaultService(base);
        BackendFeatureConfigMigrationService features = new BackendFeatureConfigMigrationService(base);
        verifyStayedCloseDefaults(base);

        boolean configChanged = main.applyCoreDefaults(config);
        configChanged |= main.applyCubeeDefaults(config);
        configChanged |= main.applyTabDefaults(config);
        boolean hudChanged = features.migrateHud(config, legacyBoards, hud, true);
        boolean atmosphereChanged = features.migrateAtmosphere(config, atmosphere, true);
        BackendFeatureConfigMigrationService.RecipeMigration recipeMigration = features.migrateRecipes(legacySurvival, recipes, true);
        if (configChanged) config.save(configPath.toFile());
        if (hudChanged) hud.save(hudPath.toFile());
        if (atmosphereChanged) atmosphere.save(atmospherePath.toFile());
        if (recipeMigration.recipesChanged()) recipes.save(recipesPath.toFile());

        assertValue(config, "tab.update-ticks", 1200);
        assertValue(config, "tab.time.zone", "Asia/Bangkok");
        assertValue(config, "tab.time.format", "EEEdd HH:mm");
        if (!config.getStringList("tab.footer.lines").contains("<gray>%time%")) {
            throw new IllegalStateException("Migrated TAB footer is missing %time%.");
        }
        for (String key : new String[]{"stayed-close", "made-room", "grew-here", "auto-chain"}) {
            if (!hud.isBoolean("hud." + key + ".enabled")) {
                throw new IllegalStateException("HUD enabled value is not boolean: " + key);
            }
        }
        if (atmosphere.contains("atmosphere.servers")) {
            throw new IllegalStateException("Legacy atmosphere.servers survived migration.");
        }
        assertValue(hud, "hud.stayed-close.enabled", true);
        assertValue(hud, "hud.stayed-close.title", "Legacy Stayclose");
        assertValue(hud, "hud.made-room.enabled", true);
        assertValue(hud, "hud.made-room.title", "Legacy Made Room");
        assertValue(hud, "hud.grew-here.title", "Current Grow");
        assertValue(atmosphere, "atmosphere.enabled", false);
        assertValue(recipes, "recipe-book.unlock-all.survival", false);
        assertValue(recipes, "recipe-book.unlock-all.creative", true);
        if (!recipeMigration.retireLegacySurvivalSection()) {
            throw new IllegalStateException("Legacy Survival Recipe Book ownership was not detected.");
        }
        boolean secondPassChanged = main.applyCoreDefaults(config);
        secondPassChanged |= main.applyCubeeDefaults(config);
        secondPassChanged |= main.applyTabDefaults(config);
        secondPassChanged |= features.migrateHud(config, legacyBoards, hud, false);
        secondPassChanged |= features.migrateAtmosphere(config, atmosphere, false);
        secondPassChanged |= features.migrateRecipes(new YamlConfiguration(), recipes, false).recipesChanged();
        if (secondPassChanged) {
            throw new IllegalStateException("Config migration is not idempotent on its second pass.");
        }
        verifyOrchestratedMigration(legacyPath, presetDirectory, outputDirectory.resolve("orchestrated"), false, false);
        verifyOrchestratedMigration(legacyPath, presetDirectory, outputDirectory.resolve("failed-hud-target"), true, false);
        verifyOrchestratedMigration(legacyPath, presetDirectory, outputDirectory.resolve("failed-recipes-target"), false, true);
        System.out.println("Staged config migration OK: " + outputDirectory);
    }

    private static void verifyStayedCloseDefaults(BackendConfigMigrationService base) {
        BackendHudConfigMigrationService defaults = new BackendHudConfigMigrationService(base);
        FileConfiguration partialHud = new YamlConfiguration();
        BackendHudConfigMigrationService.Hud stayedClose = new BackendHudConfigMigrationService.Hud(
                "stayed-close", "hud.stayed-close", "Stayclose", "where small steps stay.", "time spent here.",
                5.42, -60.86, -8.5, false);
        if (!defaults.setHudDefaults(partialHud, stayedClose)) {
            throw new IllegalStateException("Stayed Close defaults were not added to a partial canonical HUD.");
        }
        assertValue(partialHud, "hud.stayed-close.display.bedrock.title-offset-y", 0.27);
        assertValue(partialHud, "hud.stayed-close.display.bedrock.subtitle-offset-y", -0.20);
        assertValue(partialHud, "hud.stayed-close.display.bedrock.row-gap", -0.27);
        if (defaults.setHudDefaults(partialHud, stayedClose)) {
            throw new IllegalStateException("Stayed Close defaults are not idempotent.");
        }
    }

    private static void verifyOrchestratedMigration(Path legacyPath, Path presetDirectory, Path root,
            boolean invalidHudTarget, boolean invalidRecipesTarget) throws Exception {
        Files.createDirectories(root);
        Path configPath = root.resolve("config.yml");
        Path boardsPath = root.resolve("boards.yml");
        Path hudPath = root.resolve("hud.yml");
        Path survivalPath = root.resolve("survival.yml");
        Path recipesPath = root.resolve("recipes.yml");
        Files.copy(legacyPath, configPath, StandardCopyOption.REPLACE_EXISTING);
        Files.writeString(boardsPath, "boards:\n  grew-here:\n    title: Current Grow\n", StandardCharsets.UTF_8);
        Files.copy(presetDirectory.resolve("hud.yml"), hudPath, StandardCopyOption.REPLACE_EXISTING);
        Files.writeString(survivalPath, "survival:\n  recipe-book:\n    unlock-all: false\n    silent: true\n", StandardCharsets.UTF_8);
        Files.copy(presetDirectory.resolve("recipes.yml"), recipesPath, StandardCopyOption.REPLACE_EXISTING);

        BackendYamlStore store = new BackendYamlStore();
        BackendConfigMigrationService base = new BackendConfigMigrationService();
        BackendConfigMigrationOrchestrator orchestrator = new BackendConfigMigrationOrchestrator(
                new BackendFeatureConfigMigrationService(base),
                new BackendConfigDefaultGroupService(base),
                Logger.getLogger("hud-migration-test"),
                store);
        FileConfiguration config = store.load(configPath.toFile());
        FileConfiguration hud = store.load(hudPath.toFile());
        FileConfiguration legacyBoards = store.load(boardsPath.toFile());
        FileConfiguration recipes = store.load(recipesPath.toFile());
        if (invalidHudTarget) {
            Files.writeString(hudPath, "hud: [invalid\n", StandardCharsets.UTF_8);
        }
        if (invalidRecipesTarget) {
            Files.writeString(recipesPath, "recipe-book: [invalid\n", StandardCharsets.UTF_8);
        }
        BackendConfigMigrationOrchestrator.Target target = new BackendConfigMigrationOrchestrator.Target(
                configPath.toFile(), root.resolve("messages.yml").toFile(), root.resolve("places.yml").toFile(),
                root.resolve("sandbox.yml").toFile(), survivalPath.toFile(), hudPath.toFile(),
                boardsPath.toFile(), root.resolve("atmosphere.yml").toFile(), recipesPath.toFile(), config,
                store.load(root.resolve("messages.yml").toFile()), store.load(root.resolve("places.yml").toFile()),
                store.load(root.resolve("sandbox.yml").toFile()), store.load(survivalPath.toFile()),
                hud, legacyBoards, store.load(root.resolve("atmosphere.yml").toFile()), recipes, true, true, true);
        BackendConfigMigrationOrchestrator.MigrationResult result = orchestrator.migrate(
                target, () -> new BackendConfigMigrationOrchestrator.LegacyDefaultResult(false, false, false), List.of());

        FileConfiguration persistedConfig = YamlConfiguration.loadConfiguration(configPath.toFile());
        if (invalidHudTarget) {
            if (!Files.isRegularFile(boardsPath) || !persistedConfig.contains("stayed-close") || !persistedConfig.contains("hud")) {
                throw new IllegalStateException("Failed HUD persistence removed a backward-compatible source.");
            }
            return;
        }
        FileConfiguration persistedSurvival = YamlConfiguration.loadConfiguration(survivalPath.toFile());
        if (invalidRecipesTarget) {
            if (result.recipesReady()) {
                throw new IllegalStateException("Invalid Recipe Book target was reported ready.");
            }
            if (!persistedSurvival.contains("survival.recipe-book")) {
                throw new IllegalStateException("Failed Recipe Book persistence removed the legacy Survival policy.");
            }
            return;
        }
        if (!result.recipesReady()) {
            throw new IllegalStateException("Valid Recipe Book migration was not reported ready.");
        }
        FileConfiguration persistedHud = YamlConfiguration.loadConfiguration(hudPath.toFile());
        assertValue(persistedHud, "hud.stayed-close.title", "Legacy Stayclose");
        assertValue(persistedHud, "hud.grew-here.title", "Current Grow");
        if (persistedConfig.contains("stayed-close") || persistedConfig.contains("hud")) {
            throw new IllegalStateException("Legacy HUD roots survived a successful canonical migration.");
        }
        if (Files.exists(boardsPath)) {
            throw new IllegalStateException("Legacy boards.yml survived a successful canonical migration.");
        }
        FileConfiguration persistedRecipes = YamlConfiguration.loadConfiguration(recipesPath.toFile());
        assertValue(persistedRecipes, "recipe-book.unlock-all.survival", false);
        assertValue(persistedRecipes, "recipe-book.unlock-all.creative", true);
        if (persistedSurvival.contains("survival.recipe-book")) {
            throw new IllegalStateException("Legacy Survival Recipe Book section survived a successful canonical migration.");
        }
    }

    private static void assertValue(FileConfiguration config, String path, Object expected) {
        Object actual = config.get(path);
        if (!expected.equals(actual)) {
            throw new IllegalStateException(path + " expected " + expected + " but was " + actual);
        }
    }
}
