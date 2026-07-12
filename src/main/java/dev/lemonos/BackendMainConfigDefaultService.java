package dev.lemonos;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendMainConfigDefaultService {
    private final BackendConfigMigrationService configMigrationService;

    BackendMainConfigDefaultService(BackendConfigMigrationService configMigrationService) {
        this.configMigrationService = configMigrationService;
    }

    boolean applyCoreDefaults(FileConfiguration config) {
        boolean changed = false;
        changed |= this.setMissing(config, "package.name", "Honey");
        changed |= this.setMissing(config, "package.version", "26.2");
        changed |= this.setMissing(config, "lemonos.version", "1.0");
        changed |= this.setMissing(config, "server", "auto");
        return changed;
    }

    boolean applyCubeeDefaults(FileConfiguration config) {
        boolean changed = false;
        changed |= this.setMissing(config, "cubee.enabled", true);
        changed |= this.setMissing(config, "cubee.slot", 8);
        changed |= this.setMissing(config, "look.enabled", true);
        changed |= this.setMissing(config, "auth.java-login", true);
        changed |= this.setMissing(config, "auth.bedrock-trusted", true);
        changed |= this.setMissing(config, "auth.session-minutes", 10);
        changed |= this.setMissing(config, "ui.hidden-command-suggestions", true);
        return changed;
    }

    boolean applyTabDefaults(FileConfiguration config) {
        boolean changed = false;
        if (config.isInt("tab.update-ticks") && config.getInt("tab.update-ticks") == 6) {
            config.set("tab.update-ticks", 1200);
            changed = true;
        }
        if (config.getStringList("tab.footer.lines").equals(List.of("", "<gray>%server%"))
                || config.getStringList("tab.footer.lines").equals(List.of("", "%server%"))) {
            config.set("tab.footer.lines", List.of("", "<gray>%server%", "<gray>%time%"));
            changed = true;
        }
        changed |= this.setMissing(config, "tab.enabled", true);
        changed |= this.setMissing(config, "tab.update-ticks", 1200);
        changed |= this.setMissing(config, "tab.time.zone", "Asia/Bangkok");
        changed |= this.setMissing(config, "tab.time.format", "EEEdd HH:mm");
        changed |= this.setMissing(config, "tab.header.lines", List.of("<#FAF9F6>Honey", "<#C9D8B6>green tea", ""));
        changed |= this.setMissing(config, "tab.footer.lines", List.of("", "<gray>%server%", "<gray>%time%"));
        return changed;
    }

    boolean applyRestDefaults(FileConfiguration config) {
        boolean changed = false;
        changed |= this.setMissing(config, "rest.enabled", true);
        changed |= this.setMissing(config, "rest.idle-minutes", 5);
        changed |= this.setMissing(config, "rest.auto-stop", true);
        changed |= this.setMissing(config, "rest.sleep-minutes", 30);
        changed |= this.setMissing(config, "rest.wake-delay-seconds", 2);
        changed |= this.setMissing(config, "rest.status.resting", "resting.");
        changed |= this.setMissing(config, "rest.status.waking-up", "waking up.");
        changed |= this.setMissing(config, "rest.status.sleep", "sleep.");
        changed |= this.setMissing(config, "rest.suspend.atmosphere", true);
        changed |= this.setMissing(config, "rest.suspend.care-world-status", true);
        return changed;
    }

    private boolean setMissing(FileConfiguration config, String path, Object value) {
        return this.configMigrationService.setMissing(config, path, value);
    }
}
