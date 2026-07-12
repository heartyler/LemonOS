package dev.lemonos;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendConfigDefaultGroupService {
    private final BackendConfigMigrationService configMigrationService;

    BackendConfigDefaultGroupService(BackendConfigMigrationService configMigrationService) {
        this.configMigrationService = configMigrationService;
    }

    boolean applyMessageDefaults(FileConfiguration messages) {
        boolean changed = false;
        changed |= this.setMissing(messages, "package.name", "Honey");
        changed |= this.setMissing(messages, "package.version", "26.2");
        changed |= this.setMissing(messages, "lemonos.version", "1.0");
        changed |= this.setMissing(messages, "items.cubee.name", "Cubee");
        changed |= this.setMissing(messages, "items.cubee.lore", "green tea");
        changed |= this.setMissing(messages, "items.login.name", "Login");
        changed |= this.setMissing(messages, "items.login.lore", "enter passcode.");
        changed |= this.setMissing(messages, "prompts.enter-passcode", "Enter Passcode");
        changed |= this.setMissing(messages, "prompts.create-passcode", "Create Passcode");
        changed |= this.setMissing(messages, "prompts.passcode-length", "4-8 numbers");
        changed |= this.setMissing(messages, "prompts.use-basic-tool", "Use wooden axe.");
        changed |= this.setMissing(messages, "prompts.use-more-tool", "Use wooden hoe.");
        changed |= this.replaceExact(messages, "prompts.use-basic-tool", "use wooden axe.", "Use wooden axe.");
        changed |= this.replaceExact(messages, "prompts.use-more-tool", "use wooden hoe.", "Use wooden hoe.");
        changed |= this.setMissing(messages, "labels.create", "Create");
        changed |= this.setMissing(messages, "labels.enter", "Sign in");
        changed |= this.setMissing(messages, "results.youre-in", "you're in.");
        changed |= this.setMissing(messages, "results.youre-here", "you're here.");
        changed |= this.setMissing(messages, "results.try-again", "try again.");
        changed |= this.setMissing(messages, "results.too-short", "too short.");
        changed |= this.setMissing(messages, "results.too-long", "too long.");
        changed |= this.setMissing(messages, "results.not-available-here", "out of range.");
        changed |= this.setMissing(messages, "results.unavailable", "no signal.");
        changed |= this.setMissing(messages, "atmosphere.time.morning", List.of("morning is here.", "the day begins.", "light comes in.", "the world wakes up.", "there is room for today."));
        changed |= this.setMissing(messages, "atmosphere.time.day", List.of("the day is open.", "the world is bright.", "there is still time.", "the world feels near.", "light fills the room."));
        changed |= this.setMissing(messages, "atmosphere.time.sunset", List.of("evening is here.", "the sky turns soft.", "home feels close.", "the day slows down.", "light starts to fade."));
        changed |= this.setMissing(messages, "atmosphere.time.night", List.of("night is here.", "the house is quiet.", "home is close.", "the world is quiet.", "stay close."));
        changed |= this.setMissing(messages, "atmosphere.weather.clear", List.of("the sky is clear.", "light comes back.", "the world opens.", "the air feels light.", "everything feels bright."));
        changed |= this.setMissing(messages, "atmosphere.weather.rain", List.of("rain is here.", "the air turns soft.", "the rain feels calm.", "the world slows down.", "stay close."));
        changed |= this.setMissing(messages, "atmosphere.weather.thunder", List.of("the storm is near.", "the sky is loud.", "the house feels safe.", "stay close.", "the world feels far."));
        changed |= this.setMissing(messages, "atmosphere.activity.break-blocks", List.of("little by little.", "the work is showing.", "it adds up.", "you made room.", "more space now.", "that took time.", "steady work."));
        changed |= this.setMissing(messages, "atmosphere.activity.place-blocks", List.of("something is taking shape.", "you made room here.", "the shape is clearer now.", "this place is changing.", "it starts to feel right.", "piece by piece.", "the work is showing."));
        changed |= this.setMissing(messages, "atmosphere.activity.pickup-items", List.of("you brought a lot back.", "things add up.", "worth keeping.", "a little more gathered.", "you found plenty.", "more for later.", "keep it close."));
        changed |= this.setMissing(messages, "atmosphere.activity.craft-items", List.of("made with care.", "ready for later.", "that will help.", "a little more ready.", "things are coming together.", "you made what you needed."));
        changed |= this.setMissing(messages, "atmosphere.activity.damage-survived", List.of("that was close.", "still here.", "you made it through.", "keep steady.", "not done yet."));
        changed |= this.setMissing(messages, "atmosphere.activity.session-minutes", List.of("you're still here.", "the world is still here.", "there is still time.", "everything close.", "the quiet feels familiar.", "this place feels close."));
        return changed;
    }

    boolean applyPlaceDefaults(FileConfiguration places, List<PlaceDefault> defaults) {
        boolean changed = false;
        changed |= this.setMissing(places, "package.name", "Honey");
        changed |= this.setMissing(places, "package.version", "26.2");
        changed |= this.setMissing(places, "lemonos.version", "1.0");
        for (PlaceDefault place : defaults) {
            String path = "places." + place.server() + ".";
            changed |= this.setMissing(places, path + "name", place.name());
            changed |= this.setMissing(places, path + "server", place.server());
            changed |= this.setMissing(places, path + "status", "ready");
            changed |= this.setMissing(places, path + "item", place.item());
            changed |= this.setMissing(places, path + "lore", place.lore());
        }
        return changed;
    }

    boolean applySandboxDefaults(FileConfiguration sandbox) {
        boolean changed = false;
        changed |= this.setMissing(sandbox, "package.name", "Honey");
        changed |= this.setMissing(sandbox, "package.version", "26.2");
        changed |= this.setMissing(sandbox, "lemonos.version", "1.0");
        changed |= this.setMissing(sandbox, "sandbox.enabled", true);
        changed |= this.setMissing(sandbox, "sandbox.max-blocks", 32768);
        changed |= this.setMissing(sandbox, "sandbox.history-limit", 30);
        changed |= this.setMissing(sandbox, "sandbox.basic-tool", "WOODEN_AXE");
        changed |= this.setMissing(sandbox, "sandbox.more-tool", "WOODEN_HOE");
        changed |= this.setMissing(sandbox, "sandbox.default-material", "STONE");
        changed |= this.setMissing(sandbox, "sandbox.replace-source-material", "STONE");
        changed |= this.setMissing(sandbox, "sandbox.replace-target-material", "OAK_PLANKS");
        return changed;
    }

    boolean applySurvivalDefaults(FileConfiguration survival) {
        boolean changed = false;
        changed |= this.setMissing(survival, "package.name", "Honey");
        changed |= this.setMissing(survival, "package.version", "26.2");
        changed |= this.setMissing(survival, "lemonos.version", "1.0");
        changed |= this.setMissing(survival, "survival.tree.enabled", true);
        changed |= this.setMissing(survival, "survival.tree.require-sneak", true);
        changed |= this.setMissing(survival, "survival.tree.max-blocks", 160);
        changed |= this.setMissing(survival, "survival.miner.enabled", true);
        changed |= this.setMissing(survival, "survival.miner.require-sneak", true);
        changed |= this.setMissing(survival, "survival.miner.max-blocks", 96);
        changed |= this.setMissing(survival, "survival.autocrop.enabled", true);
        changed |= this.setMissing(survival, "survival.autocrop.require-sneak", true);
        changed |= this.setMissing(survival, "survival.autocrop.max-blocks", 128);
        changed |= this.setMissing(survival, "survival.autocrop.replant", false);
        changed |= this.setMissing(survival, "survival.auto-plant.enabled", true);
        changed |= this.setMissing(survival, "survival.auto-plant.require-sneak", true);
        changed |= this.setMissing(survival, "survival.auto-plant.require-tool", false);
        changed |= this.setMissing(survival, "survival.auto-plant.max-columns", 128);
        changed |= this.setMissing(survival, "survival.auto-plant.max-blocks", 128);
        changed |= this.setMissing(survival, "survival.auto-plant.radius", 8);
        changed |= this.setMissing(survival, "survival.auto-plant.reach", 10);
        changed |= this.setMissing(survival, "survival.auto-plant.bamboo.enabled", true);
        changed |= this.setMissing(survival, "survival.auto-plant.bamboo.keep-bottom", 1);
        changed |= this.setMissing(survival, "survival.auto-plant.bamboo.radius", 8);
        changed |= this.setMissing(survival, "survival.auto-plant.sugar-cane.enabled", true);
        changed |= this.setMissing(survival, "survival.auto-plant.sugar-cane.keep-bottom", 1);
        changed |= this.setMissing(survival, "survival.auto-plant.sugar-cane.radius", 32);
        changed |= this.setMissing(survival, "survival.auto-plant.cactus.enabled", true);
        changed |= this.setMissing(survival, "survival.auto-plant.cactus.keep-bottom", 1);
        changed |= this.setMissing(survival, "survival.auto-plant.cactus.radius", 16);
        changed |= this.setMissing(survival, "survival.auto-plant.kelp.enabled", false);
        changed |= this.setMissing(survival, "survival.auto-plant.kelp.keep-bottom", 1);
        changed |= this.setMissing(survival, "survival.auto-plant.kelp.radius", 8);
        changed |= this.setMissing(survival, "survival.auto-plant.pumpkin.enabled", false);
        changed |= this.setMissing(survival, "survival.auto-plant.melon.enabled", false);
        changed |= this.setMissing(survival, "survival.auto-plant.cocoa.enabled", false);
        changed |= this.setMissing(survival, "survival.chest-sort.enabled", true);
        changed |= this.setMissing(survival, "survival.chest-sort.require-sneak", true);
        changed |= this.setMissing(survival, "survival.chest-sort.require-empty-hand", true);
        changed |= this.setMissing(survival, "survival.chest-sort.containers", List.of("CHEST", "BARREL", "SHULKER_BOX"));
        changed |= this.setMissing(survival, "survival.tool-switch.enabled", true);
        changed |= this.setMissing(survival, "survival.tool-switch.require-sneak", true);
        changed |= this.setMissing(survival, "survival.tool-switch.allow-golden-tools", false);
        changed |= this.setMissing(survival, "survival.tool-switch.min-durability-left", 2);
        changed |= this.setMissing(survival, "survival.refill.enabled", true);
        changed |= this.setMissing(survival, "survival.refill.blocks", true);
        changed |= this.setMissing(survival, "survival.refill.tools", true);
        changed |= this.setMissing(survival, "survival.refill.weapons", true);
        changed |= this.setMissing(survival, "survival.refill.prefer-inventory-first", true);
        changed |= this.setMissing(survival, "survival.recipe-book.unlock-all", true);
        changed |= this.setMissing(survival, "survival.recipe-book.silent", true);
        changed |= this.setMissing(survival, "survival.chain-status.enabled", true);
        return changed;
    }

    private boolean setMissing(FileConfiguration configuration, String path, Object value) {
        return this.configMigrationService.setMissing(configuration, path, value);
    }

    private boolean replaceExact(FileConfiguration configuration, String path, String previousValue, String canonicalValue) {
        if (!previousValue.equals(configuration.getString(path))) {
            return false;
        }
        configuration.set(path, canonicalValue);
        return true;
    }

    record PlaceDefault(String name, String server, String item, String lore) {
    }
}
