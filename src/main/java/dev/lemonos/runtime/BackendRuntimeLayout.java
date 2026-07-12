package dev.lemonos.runtime;

import java.io.File;

public final class BackendRuntimeLayout {
    private final File runtimeRoot;
    private final File configRoot;
    private final File dataRoot;

    private BackendRuntimeLayout(File runtimeRoot) {
        this.runtimeRoot = runtimeRoot.getAbsoluteFile();
        this.configRoot = new File(this.runtimeRoot, "LemonOS");
        this.dataRoot = new File(this.runtimeRoot, "lemonos-data");
    }

    public static BackendRuntimeLayout resolve() {
        String configuredRoot = System.getProperty("lemonos.runtimeRoot");
        if (configuredRoot == null || configuredRoot.isBlank()) {
            configuredRoot = System.getenv("LEMONOS_RUNTIME_ROOT");
        }
        if (configuredRoot != null && !configuredRoot.isBlank()) {
            return new BackendRuntimeLayout(new File(configuredRoot));
        }
        File workingDirectory = new File(System.getProperty("user.dir")).getAbsoluteFile();
        File parent = workingDirectory.getParentFile();
        return new BackendRuntimeLayout(parent == null ? workingDirectory : parent);
    }

    public File runtimeRoot() { return this.runtimeRoot; }
    public File configRoot() { return this.configRoot; }
    public File dataRoot() { return this.dataRoot; }
    public File configFile(String name) { return new File(this.configRoot, name); }
    public File dataFile(String name) { return new File(this.dataRoot, name); }
    public File boardChunkState(String backend) { return new File(this.dataRoot, "board-chunks/" + backend + ".yml"); }
    public File adminCommandDirectory() { return new File(this.dataRoot, "admin_commands"); }
    public File adminCommandQueue(String backend) { return new File(this.adminCommandDirectory(), backend + ".queue"); }
    public File adminCommandAck(String backend) { return new File(this.adminCommandDirectory(), backend + ".ack"); }
}
