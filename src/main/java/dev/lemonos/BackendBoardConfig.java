package dev.lemonos;

import org.bukkit.configuration.file.FileConfiguration;

/** Typed ownership boundary for LemonOS/boards.yml. */
final class BackendBoardConfig implements BackendDisplayConfig {
    static final String STAYED_CLOSE = "stayed-close";
    static final String MADE_ROOM = "made-room";
    static final String GREW_HERE = "grew-here";
    static final String AUTO_CHAIN = "auto-chain";

    private final FileConfiguration source;

    BackendBoardConfig(FileConfiguration source) {
        this.source = source;
    }

    String path(String boardKey) {
        return "boards." + boardKey;
    }

    boolean enabled(String boardKey) {
        return this.booleanValue(this.path(boardKey) + ".enabled", false);
    }

    int refreshMinutes(String boardKey) {
        return this.intValue(this.path(boardKey) + ".refresh-minutes", 1, 1, 1440);
    }

    int top(String boardKey) {
        return this.intValue(this.path(boardKey) + ".top", 5, 1, 10);
    }

    boolean trackBlocksChanged(String boardKey) {
        return this.booleanValue(this.path(boardKey) + ".scoring.track-blocks-changed", true);
    }

    boolean bedrockEnabled(String boardKey) {
        return this.booleanValue(this.path(boardKey) + ".display.bedrock.enabled", false);
    }

    boolean bedrockEnabledAtPath(String boardPath) {
        return this.booleanValue(boardPath + ".display.bedrock.enabled", false);
    }

    @Override
    public boolean booleanValue(String path, boolean fallback) {
        return this.source == null ? fallback : this.source.getBoolean(path, fallback);
    }

    @Override
    public int intValue(String path, int fallback, int minimum, int maximum) {
        int value = this.source == null ? fallback : this.source.getInt(path, fallback);
        return Math.max(minimum, Math.min(maximum, value));
    }

    @Override
    public double doubleValue(String path, double fallback, double minimum, double maximum) {
        double value = this.source == null ? fallback : this.source.getDouble(path, fallback);
        return Math.max(minimum, Math.min(maximum, value));
    }

    @Override
    public double doubleValue(String primaryPath, String legacyPath, double fallback, double minimum, double maximum) {
        return this.source != null && !this.source.contains(primaryPath)
                ? this.doubleValue(legacyPath, fallback, minimum, maximum)
                : this.doubleValue(primaryPath, fallback, minimum, maximum);
    }

    @Override
    public String stringValue(String path, String fallback) {
        return this.source == null ? fallback : this.source.getString(path, fallback);
    }
}
