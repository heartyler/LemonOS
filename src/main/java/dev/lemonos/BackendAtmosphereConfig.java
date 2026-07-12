package dev.lemonos;

import org.bukkit.configuration.file.FileConfiguration;

/** Typed ownership boundary for LemonOS/atmosphere.yml. */
final class BackendAtmosphereConfig {
    private static final String ROOT = "atmosphere.";
    private final FileConfiguration source;

    BackendAtmosphereConfig(FileConfiguration source) {
        this.source = source;
    }

    boolean enabled() { return this.bool("enabled", true); }
    int actionBarDurationSeconds() { return this.integer("actionbar.duration-seconds", 4, 1, 10); }
    int actionBarRepeatSeconds() { return this.integer("actionbar.repeat-seconds", 1, 1, 5); }
    int actionBarCooldownSeconds() { return this.integer("actionbar.cooldown-seconds", 600, 10, 600); }
    boolean musicEnabled() { return this.bool("music.enabled", true); }
    double musicVolume() { return this.decimal("music.volume", 0.16, 0.0, 1.0); }
    double musicPitch() { return this.decimal("music.pitch", 1.0, 0.5, 2.0); }
    int musicDelaySeconds() { return this.integer("music.delay-seconds", 8, 0, 120); }
    int musicGapSeconds() { return this.integer("music.gap-seconds", 20, 0, 300); }
    boolean musicActionBarEnabled() { return this.bool("music.actionbar.enabled", false); }
    String musicActionBarFormat() { return this.string("music.actionbar.format", "playing {music}"); }
    int musicActionBarRefreshTicks() { return this.integer("music.actionbar.refresh-ticks", 20, 10, 100); }
    int musicActionBarResumeDelayTicks() { return this.integer("music.actionbar.resume-delay-ticks", 80, 0, 200); }
    String musicDisplayName(String key, String fallback) { return this.string("music.display-names." + key, fallback); }
    int musicWeight(String key) { return this.integer("music.weight." + key, 0, 0, 100); }
    int musicTrackSeconds(String key) { return this.integer("music.track-seconds." + key, 180, 30, 600); }
    boolean activityEnabled() { return this.bool("activity.enabled", true); }
    boolean activityTriggerEnabled(String key) { return this.bool("activity." + key + ".enabled", true); }
    int activityGlobalCooldownSeconds() { return this.integer("activity.global-cooldown-seconds", 90, 0, 3600); }
    int activityThreshold(String key, int fallback) { return this.integer("activity." + key + ".threshold", fallback, 1, 100000); }
    int activityCooldownSeconds(String key) { return this.integer("activity." + key + ".cooldown-seconds", 240, 0, 3600); }

    private boolean bool(String path, boolean fallback) {
        return this.source == null ? fallback : this.source.getBoolean(ROOT + path, fallback);
    }

    private int integer(String path, int fallback, int minimum, int maximum) {
        int value = this.source == null ? fallback : this.source.getInt(ROOT + path, fallback);
        return Math.max(minimum, Math.min(maximum, value));
    }

    private double decimal(String path, double fallback, double minimum, double maximum) {
        double value = this.source == null ? fallback : this.source.getDouble(ROOT + path, fallback);
        return Math.max(minimum, Math.min(maximum, value));
    }

    private String string(String path, String fallback) {
        return this.source == null ? fallback : this.source.getString(ROOT + path, fallback);
    }
}
