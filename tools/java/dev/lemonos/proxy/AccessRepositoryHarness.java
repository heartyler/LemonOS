package dev.lemonos.proxy;

import java.nio.file.Files;
import java.nio.file.Path;

public final class AccessRepositoryHarness {
    private AccessRepositoryHarness() {
    }

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("lemonos-access-");
        try {
            Path access = root.resolve("access.yml");
            Files.writeString(access, "version: \"2.0\"\nadmins:\n  - legacy\n");
            AccessRepository first = new AccessRepository(access, null);
            AccessRepository second = new AccessRepository(access, null);
            first.load();
            if (!first.hasAdminAccess(null, "legacy")) {
                throw new IllegalStateException("Schema 2.0 admin was not loaded.");
            }
            first.updateNameAccess("alpha", true);
            second.updateNameAccess("beta", true);
            second.updateNameAccess("legacy", false);
            AccessRepository verified = new AccessRepository(access, null);
            verified.load();
            if (!verified.hasAdminAccess(null, "alpha") || !verified.hasAdminAccess(null, "beta")) {
                throw new IllegalStateException("Locked access updates lost an administrator.");
            }
            if (verified.hasAdminAccess(null, "legacy")) {
                throw new IllegalStateException("Locked access removal was lost.");
            }
            String content = Files.readString(access);
            if (!content.contains("version: \"3.0\"")) {
                throw new IllegalStateException("Access repository did not write canonical schema 3.0.");
            }
            Path unreadable = root.resolve("unreadable-access.yml");
            Files.createDirectory(unreadable);
            try {
                new AccessRepository(unreadable, null).updateNameAccess("must-not-write", true);
                throw new IllegalStateException("Unreadable access storage was overwritten.");
            }
            catch (java.io.IOException expected) {
            }
            System.out.println("Proxy access repository compatibility/locking harness OK");
        }
        finally {
            try (var paths = Files.walk(root)) {
                paths.sorted((left, right) -> right.compareTo(left)).forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (Exception ignored) { }
                });
            }
        }
    }
}
