package dev.lemonos.proxy.runtime;

import java.nio.file.Path;

public final class ProxyRuntimeLayout {
    public final Path runtimeRoot;
    public final Path sharedDataFolder;
    public final Path accessFile;
    public final Path onlineFile;
    public final Path placesFile;
    public final Path playtimeFile;
    public final Path boardsConfigFile;

    private ProxyRuntimeLayout(Path runtimeRoot) {
        this.runtimeRoot = runtimeRoot.toAbsolutePath().normalize();
        this.sharedDataFolder = this.runtimeRoot.resolve("lemonos-data");
        this.accessFile = this.sharedDataFolder.resolve("access.yml");
        this.onlineFile = this.sharedDataFolder.resolve("online.yml");
        this.placesFile = this.sharedDataFolder.resolve("places.yml");
        this.playtimeFile = this.sharedDataFolder.resolve("playtime.yml");
        this.boardsConfigFile = this.runtimeRoot.resolve("LemonOS").resolve("boards.yml");
    }

    public static ProxyRuntimeLayout resolve() {
        String configuredRoot = System.getProperty("lemonos.runtimeRoot");
        if (configuredRoot == null || configuredRoot.isBlank()) {
            configuredRoot = System.getenv("LEMONOS_RUNTIME_ROOT");
        }
        if (configuredRoot != null && !configuredRoot.isBlank()) {
            return new ProxyRuntimeLayout(Path.of(configuredRoot));
        }
        Path workingDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        if ("velocity".equalsIgnoreCase(workingDirectory.getFileName().toString())) {
            return new ProxyRuntimeLayout(workingDirectory.getParent());
        }
        return new ProxyRuntimeLayout(workingDirectory);
    }
}
