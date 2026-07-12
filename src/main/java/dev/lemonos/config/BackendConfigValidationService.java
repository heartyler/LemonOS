package dev.lemonos.config;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/** Validates supported LemonOS configuration policy without mutating configuration. */
public final class BackendConfigValidationService {
    private final Logger logger;

    public BackendConfigValidationService(Logger logger) {
        this.logger = logger;
    }

    public void validate(
            FileConfiguration config,
            FileConfiguration boards,
            FileConfiguration atmosphere,
            FileConfiguration sandbox,
            FileConfiguration survival,
            FileConfiguration places,
            List<PlacePolicy> placePolicies) {
        this.warnStringValue(config, "config.yml", "server", "auto", "backend identity should be detected from the runtime server folder");
        this.warnBooleanValue(config, "config.yml", "cubee.enabled", true, "disabling Cubee removes the main Honey player surface");
        this.warnIntValue(config, "config.yml", "cubee.slot", 8, "Honey expects Cubee in hotbar slot 9");
        this.warnBooleanValue(config, "config.yml", "auth.java-login", true, "this is part of Honey login behavior");
        this.warnBooleanValue(config, "config.yml", "auth.bedrock-trusted", true, "this is part of Honey Bedrock trust behavior");
        this.warnBooleanValue(config, "config.yml", "ui.hidden-command-suggestions", true, "this is part of command protection and player-facing UX");
        this.warnClampedInt(config, "config.yml", "auth.session-minutes", 0, 10080);
        this.warnClampedInt(config, "config.yml", "tab.update-ticks", 5, 1200);
        this.warnClampedInt(boards, "boards.yml", "boards.stayed-close.refresh-minutes", 1, 1440);
        this.warnClampedInt(boards, "boards.yml", "boards.stayed-close.top", 1, 10);
        this.warnClampedInt(boards, "boards.yml", "boards.stayed-close.name-width", 4, 16);
        this.warnClampedInt(boards, "boards.yml", "boards.stayed-close.display.background-alpha", 0, 255);
        this.warnClampedInt(boards, "boards.yml", "boards.stayed-close.display.view-range", 1, 128);
        this.warnClampedInt(boards, "boards.yml", "boards.stayed-close.display.line-width", 60, 500);
        this.warnClampedInt(atmosphere, "atmosphere.yml", "atmosphere.actionbar.duration-seconds", 1, 10);
        this.warnClampedInt(atmosphere, "atmosphere.yml", "atmosphere.actionbar.repeat-seconds", 1, 5);
        this.warnClampedInt(atmosphere, "atmosphere.yml", "atmosphere.actionbar.cooldown-seconds", 10, 600);
        this.warnClampedInt(config, "config.yml", "rest.idle-minutes", 1, 1440);
        this.warnClampedInt(config, "config.yml", "rest.sleep-minutes", 1, 1440);
        this.warnClampedInt(config, "config.yml", "rest.wake-delay-seconds", 0, 120);
        this.warnClampedInt(atmosphere, "atmosphere.yml", "atmosphere.music.delay-seconds", 0, 120);
        this.warnClampedInt(atmosphere, "atmosphere.yml", "atmosphere.music.gap-seconds", 0, 300);
        this.warnClampedInt(sandbox, "sandbox.yml", "sandbox.max-blocks", 1, 32768);
        this.warnClampedInt(sandbox, "sandbox.yml", "sandbox.history-limit", 1, 100);
        this.warnMaterialValue(sandbox, "sandbox.yml", "sandbox.basic-tool");
        this.warnMaterialValue(sandbox, "sandbox.yml", "sandbox.more-tool");
        this.warnMaterialValue(sandbox, "sandbox.yml", "sandbox.default-material");
        this.warnMaterialValue(sandbox, "sandbox.yml", "sandbox.replace-source-material");
        this.warnMaterialValue(sandbox, "sandbox.yml", "sandbox.replace-target-material");
        this.warnClampedInt(survival, "survival.yml", "survival.tree.max-blocks", 1, 512);
        this.warnClampedInt(survival, "survival.yml", "survival.miner.max-blocks", 1, 256);
        this.warnClampedInt(survival, "survival.yml", "survival.autocrop.max-blocks", 1, 512);
        this.warnClampedInt(survival, "survival.yml", "survival.auto-plant.max-columns", 1, 256);
        this.warnClampedInt(survival, "survival.yml", "survival.auto-plant.max-blocks", 1, 256);
        this.warnClampedInt(survival, "survival.yml", "survival.auto-plant.radius", 0, 32);
        this.warnClampedInt(survival, "survival.yml", "survival.auto-plant.reach", 1, 32);
        this.warnClampedInt(survival, "survival.yml", "survival.auto-plant.bamboo.radius", 0, 32);
        this.warnClampedInt(survival, "survival.yml", "survival.auto-plant.sugar-cane.radius", 0, 32);
        this.warnClampedInt(survival, "survival.yml", "survival.auto-plant.cactus.radius", 0, 32);
        this.warnClampedInt(survival, "survival.yml", "survival.auto-plant.kelp.radius", 0, 32);
        for (PlacePolicy placePolicy : placePolicies) {
            String path = "places." + placePolicy.serverId();
            this.warnStringValue(places, "places.yml", path + ".server", placePolicy.serverId(), "place server metadata should match Honey's fixed backend id");
            this.warnMaterialValue(places, "places.yml", path + ".item");
        }
    }

    private void warnStringValue(FileConfiguration configuration, String fileName, String path, String expected, String policy) {
        if (configuration == null || !configuration.isSet(path)) {
            return;
        }
        String value = configuration.getString(path, expected);
        if (!expected.equalsIgnoreCase(String.valueOf(value).trim())) {
            this.warnPolicy(fileName, path, policy + "; expected " + expected + ".");
        }
    }

    private void warnBooleanValue(FileConfiguration configuration, String fileName, String path, boolean expected, String policy) {
        if (configuration != null && configuration.isSet(path) && configuration.getBoolean(path, expected) != expected) {
            this.warnPolicy(fileName, path, policy + "; expected " + expected + ".");
        }
    }

    private void warnIntValue(FileConfiguration configuration, String fileName, String path, int expected, String policy) {
        if (configuration != null && configuration.isSet(path) && configuration.getInt(path, expected) != expected) {
            this.warnPolicy(fileName, path, policy + "; expected " + expected + ".");
        }
    }

    private void warnClampedInt(FileConfiguration configuration, String fileName, String path, int minimum, int maximum) {
        if (configuration == null || !configuration.isSet(path)) {
            return;
        }
        int value = configuration.getInt(path, minimum);
        if (value < minimum || value > maximum) {
            this.warnPolicy(fileName, path, "value " + value + " is outside supported range " + minimum + "-" + maximum + " and will be clamped.");
        }
    }

    private void warnMaterialValue(FileConfiguration configuration, String fileName, String path) {
        if (configuration == null || !configuration.isSet(path)) {
            return;
        }
        String value = configuration.getString(path, "");
        if (Material.matchMaterial(String.valueOf(value).trim().toUpperCase(Locale.ROOT).replace(' ', '_')) == null) {
            this.warnPolicy(fileName, path, "material '" + value + "' is invalid and the plugin default will be used.");
        }
    }

    private void warnPolicy(String fileName, String path, String detail) {
        this.logger.warning("LemonOS config policy: " + fileName + " " + path + " - " + detail);
    }

    public record PlacePolicy(String serverId) {
    }
}
