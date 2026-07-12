package dev.lemonos;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
        Path boardsPath = outputDirectory.resolve("boards.yml");
        Path atmospherePath = outputDirectory.resolve("atmosphere.yml");
        Files.copy(legacyPath, configPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(presetDirectory.resolve("boards.yml"), boardsPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(presetDirectory.resolve("atmosphere.yml"), atmospherePath, StandardCopyOption.REPLACE_EXISTING);

        FileConfiguration config = YamlConfiguration.loadConfiguration(configPath.toFile());
        FileConfiguration boards = YamlConfiguration.loadConfiguration(boardsPath.toFile());
        FileConfiguration atmosphere = YamlConfiguration.loadConfiguration(atmospherePath.toFile());
        BackendConfigMigrationService base = new BackendConfigMigrationService();
        BackendMainConfigDefaultService main = new BackendMainConfigDefaultService(base);
        BackendFeatureConfigMigrationService features = new BackendFeatureConfigMigrationService(base);

        boolean configChanged = main.applyCoreDefaults(config);
        configChanged |= main.applyCubeeDefaults(config);
        configChanged |= main.applyTabDefaults(config);
        boolean boardsChanged = features.migrateBoards(config, boards, true);
        boolean atmosphereChanged = features.migrateAtmosphere(config, atmosphere, true);
        if (configChanged) config.save(configPath.toFile());
        if (boardsChanged) boards.save(boardsPath.toFile());
        if (atmosphereChanged) atmosphere.save(atmospherePath.toFile());

        assertValue(config, "tab.update-ticks", 1200);
        assertValue(config, "tab.time.zone", "Asia/Bangkok");
        assertValue(config, "tab.time.format", "EEEdd HH:mm");
        if (!config.getStringList("tab.footer.lines").contains("<gray>%time%")) {
            throw new IllegalStateException("Migrated TAB footer is missing %time%.");
        }
        for (String key : new String[]{"stayed-close", "made-room", "grew-here", "auto-chain"}) {
            if (!boards.isBoolean("boards." + key + ".enabled")) {
                throw new IllegalStateException("Board enabled value is not boolean: " + key);
            }
        }
        if (atmosphere.contains("atmosphere.servers")) {
            throw new IllegalStateException("Legacy atmosphere.servers survived migration.");
        }
        assertValue(boards, "boards.stayed-close.enabled", true);
        assertValue(boards, "boards.stayed-close.title", "Legacy Stayclose");
        assertValue(boards, "boards.made-room.enabled", true);
        assertValue(boards, "boards.made-room.title", "Legacy Made Room");
        assertValue(atmosphere, "atmosphere.enabled", false);
        boolean secondPassChanged = main.applyCoreDefaults(config);
        secondPassChanged |= main.applyCubeeDefaults(config);
        secondPassChanged |= main.applyTabDefaults(config);
        secondPassChanged |= features.migrateBoards(config, boards, false);
        secondPassChanged |= features.migrateAtmosphere(config, atmosphere, false);
        if (secondPassChanged) {
            throw new IllegalStateException("Config migration is not idempotent on its second pass.");
        }
        System.out.println("Staged config migration OK: " + outputDirectory);
    }

    private static void assertValue(FileConfiguration config, String path, Object expected) {
        Object actual = config.get(path);
        if (!expected.equals(actual)) {
            throw new IllegalStateException(path + " expected " + expected + " but was " + actual);
        }
    }
}
