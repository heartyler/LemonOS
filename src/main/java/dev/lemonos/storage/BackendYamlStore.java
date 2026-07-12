package dev.lemonos.storage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class BackendYamlStore {
    private final Map<FileConfiguration, Map<String, Object>> baselines = new WeakHashMap<>();

    public synchronized FileConfiguration load(File file) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        this.baselines.put(configuration, this.snapshot(configuration));
        return configuration;
    }

    /**
     * Persists only the changes made since this store loaded the configuration.
     * Reloading and merging under an OS file lock preserves unrelated updates
     * written by another LemonOS backend process.
     */
    public synchronized void saveAtomic(FileConfiguration configuration, File file) throws IOException {
        if (configuration == null || file == null) {
            throw new IllegalArgumentException("Configuration and file are required.");
        }
        File parent = file.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Unable to create data directory: " + parent);
        }
        Path target = file.toPath();
        Path lockPath = target.resolveSibling(file.getName() + ".lock");
        try (FileChannel channel = FileChannel.open(lockPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock ignored = channel.lock()) {
            FileConfiguration merged = Files.isRegularFile(target)
                    ? this.loadStrict(target)
                    : new YamlConfiguration();
            Map<String, Object> baseline = this.baselines.getOrDefault(configuration, Map.of());
            Map<String, Object> desired = this.snapshot(configuration);
            Map<String, Object> mergedValues = BackendYamlDeltaMerge.merge(
                    baseline, desired, this.snapshot(merged));
            this.replaceValues(merged, mergedValues);
            this.writeAtomic(merged, target);
            this.replaceValues(configuration, merged);
            this.baselines.put(configuration, this.snapshot(merged));
        }
    }

    private FileConfiguration loadStrict(Path path) throws IOException {
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.loadFromString(Files.readString(path, StandardCharsets.UTF_8));
            return configuration;
        }
        catch (InvalidConfigurationException exception) {
            throw new IOException("Refusing to overwrite invalid YAML: " + path, exception);
        }
    }

    private void writeAtomic(FileConfiguration configuration, Path target) throws IOException {
        Path temporary = target.resolveSibling(target.getFileName() + ".tmp-" + UUID.randomUUID());
        try {
            Files.writeString(temporary, configuration.saveToString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            try {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        finally {
            Files.deleteIfExists(temporary);
        }
    }

    private Map<String, Object> snapshot(FileConfiguration configuration) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : configuration.getValues(true).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection)) {
                values.put(entry.getKey(), entry.getValue());
            }
        }
        return values;
    }

    private void replaceValues(FileConfiguration destination, FileConfiguration source) {
        this.replaceValues(destination, this.snapshot(source));
    }

    private void replaceValues(FileConfiguration destination, Map<String, Object> values) {
        for (String key : destination.getKeys(false)) {
            destination.set(key, null);
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            destination.set(entry.getKey(), entry.getValue());
        }
    }
}
