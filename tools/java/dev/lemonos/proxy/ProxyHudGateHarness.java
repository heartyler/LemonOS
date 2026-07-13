package dev.lemonos.proxy;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ProxyHudGateHarness {
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("lemonos-hud-gate-");
        try {
            Path hud = root.resolve("hud.yml");
            ProxyLifecycleService lifecycle = new ProxyLifecycleService(null);
            Files.writeString(hud, "hud:\n  stayed-close:\n    enabled: false\n");
            if (lifecycle.stayedCloseCollectionEnabled(hud)) {
                throw new IllegalStateException("Disabled canonical HUD enabled collection.");
            }
            Files.writeString(hud, "hud:\n  stayed-close:\n    enabled: true\n  made-room:\n    enabled: false\n");
            if (!lifecycle.stayedCloseCollectionEnabled(hud)) {
                throw new IllegalStateException("Enabled canonical HUD did not enable collection.");
            }
            Files.writeString(hud, "stayed-close:\n  enabled: true\n");
            if (lifecycle.stayedCloseCollectionEnabled(hud)) {
                throw new IllegalStateException("Legacy root enabled canonical collection.");
            }
            Files.delete(hud);
            Files.writeString(root.resolve("boards.yml"), "boards:\n  stayed-close:\n    enabled: true\n");
            if (!lifecycle.stayedCloseCollectionEnabled(hud)) {
                throw new IllegalStateException("Legacy boards.yml fallback did not preserve collection during migration.");
            }
            Files.writeString(hud, "hud:\n  stayed-close:\n    enabled: false\n");
            if (lifecycle.stayedCloseCollectionEnabled(hud)) {
                throw new IllegalStateException("Legacy boards.yml overrode an existing canonical HUD gate.");
            }
            Files.delete(hud);
            Files.delete(root.resolve("boards.yml"));
            Files.writeString(root.resolve("config.yml"), "stayed-close:\n  enabled: true\n");
            if (!lifecycle.stayedCloseCollectionEnabled(hud)) {
                throw new IllegalStateException("Legacy config.yml fallback did not preserve collection during migration.");
            }
        } finally {
            try (var paths = Files.walk(root)) {
                paths.sorted((left, right) -> right.compareTo(left)).forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (Exception ignored) { }
                });
            }
        }
    }
}
