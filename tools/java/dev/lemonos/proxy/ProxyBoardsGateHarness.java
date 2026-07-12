package dev.lemonos.proxy;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ProxyBoardsGateHarness {
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("lemonos-boards-gate-");
        try {
            Path boards = root.resolve("boards.yml");
            ProxyLifecycleService lifecycle = new ProxyLifecycleService(null);
            Files.writeString(boards, "boards:\n  stayed-close:\n    enabled: false\n");
            if (lifecycle.stayedCloseCollectionEnabled(boards)) {
                throw new IllegalStateException("Disabled canonical board enabled collection.");
            }
            Files.writeString(boards, "boards:\n  stayed-close:\n    enabled: true\n  made-room:\n    enabled: false\n");
            if (!lifecycle.stayedCloseCollectionEnabled(boards)) {
                throw new IllegalStateException("Enabled canonical board did not enable collection.");
            }
            Files.writeString(boards, "stayed-close:\n  enabled: true\n");
            if (lifecycle.stayedCloseCollectionEnabled(boards)) {
                throw new IllegalStateException("Legacy root enabled canonical collection.");
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
