package dev.lemonos;

import org.bukkit.configuration.file.FileConfiguration;

/** Typed ownership boundary for LemonOS/recipes.yml. */
final class BackendRecipeBookConfig {
    private static final String ROOT = "recipe-book.unlock-all.";
    private final FileConfiguration source;

    BackendRecipeBookConfig(FileConfiguration source) {
        this.source = source;
    }

    boolean unlockAll(String serverId) {
        if (!"survival".equals(serverId) && !"creative".equals(serverId)) return false;
        String path = ROOT + serverId;
        if (this.source == null || !this.source.isSet(path)) return true;
        return this.source.isBoolean(path) && this.source.getBoolean(path);
    }
}
