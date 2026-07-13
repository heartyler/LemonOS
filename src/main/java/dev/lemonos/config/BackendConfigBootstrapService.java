package dev.lemonos.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Logger;
import dev.lemonos.runtime.BackendRuntimeLayout;
import dev.lemonos.storage.BackendYamlStore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class BackendConfigBootstrapService {
    private static final String[] FILES = {
        "config.yml", "messages.yml", "places.yml", "sandbox.yml",
        "survival.yml", "hud.yml", "atmosphere.yml", "recipes.yml"
    };

    private final JavaPlugin plugin;
    private final Logger logger;
    private final BackendYamlStore yamlStore;

    public BackendConfigBootstrapService(JavaPlugin plugin, BackendYamlStore yamlStore) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.yamlStore = yamlStore;
    }

    public LoadedConfig load(BackendRuntimeLayout layout) {
        File configRoot = layout.configRoot();
        if (!configRoot.isDirectory() && !configRoot.mkdirs()) {
            throw new IllegalStateException("Unable to create LemonOS config directory: " + configRoot);
        }
        for (String name : FILES) {
            this.ensureBundledPreset(layout.configFile(name), name);
        }
        File configFile = layout.configFile("config.yml");
        File messagesFile = layout.configFile("messages.yml");
        File placesFile = layout.configFile("places.yml");
        File sandboxFile = layout.configFile("sandbox.yml");
        File survivalFile = layout.configFile("survival.yml");
        File hudFile = layout.configFile("hud.yml");
        File legacyBoardsFile = layout.configFile("boards.yml");
        File atmosphereFile = layout.configFile("atmosphere.yml");
        File recipesFile = layout.configFile("recipes.yml");
        return new LoadedConfig(
            configFile, messagesFile, placesFile, sandboxFile, survivalFile, hudFile, legacyBoardsFile, atmosphereFile, recipesFile,
            this.yamlStore.load(configFile),
            this.yamlStore.load(messagesFile),
            this.yamlStore.load(placesFile),
            this.yamlStore.load(sandboxFile),
            this.yamlStore.load(survivalFile),
            this.yamlStore.load(hudFile),
            this.yamlStore.load(legacyBoardsFile),
            this.yamlStore.load(atmosphereFile),
            this.yamlStore.load(recipesFile));
    }

    private void ensureBundledPreset(File target, String name) {
        if (target.isFile()) return;
        try (InputStream input = this.plugin.getResource("defaults/LemonOS/" + name)) {
            if (input == null) {
                throw new IllegalStateException("Bundled LemonOS preset is missing: " + name);
            }
            Files.writeString(target.toPath(), new String(input.readAllBytes(), StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }
        catch (IOException exception) {
            this.logger.severe("Unable to create LemonOS config " + name + ": " + exception.getMessage());
            throw new IllegalStateException("Unable to create LemonOS config: " + name, exception);
        }
    }

    public record LoadedConfig(
        File configFile, File messagesFile, File placesFile, File sandboxFile,
        File survivalFile, File hudFile, File legacyBoardsFile, File atmosphereFile, File recipesFile,
        FileConfiguration config, FileConfiguration messages, FileConfiguration places,
        FileConfiguration sandbox, FileConfiguration survival, FileConfiguration hud,
        FileConfiguration legacyBoards,
        FileConfiguration atmosphere,
        FileConfiguration recipes) {
    }
}
