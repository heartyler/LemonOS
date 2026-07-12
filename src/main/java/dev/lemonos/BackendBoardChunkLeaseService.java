package dev.lemonos;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

/** Owns only the force-loaded chunks acquired by LemonOS Boards. */
final class BackendBoardChunkLeaseService {
    private final File stateFile;
    private final Logger logger;
    private final Set<ChunkKey> owned = new HashSet<>();
    private final Set<ChunkKey> desired = new HashSet<>();

    BackendBoardChunkLeaseService(File stateFile, Logger logger) {
        this.stateFile = stateFile;
        this.logger = logger;
        this.load();
    }

    void recoverOwnedChunks() {
        for (ChunkKey key : Set.copyOf(this.owned)) {
            World world = Bukkit.getWorld(key.worldName());
            if (world == null) continue;
            world.setChunkForceLoaded(key.x(), key.z(), false);
            this.owned.remove(key);
        }
        this.save();
    }

    void recoverWorld(World world) {
        for (ChunkKey key : Set.copyOf(this.owned)) {
            if (!key.worldName().equals(world.getName())) continue;
            world.setChunkForceLoaded(key.x(), key.z(), false);
            this.owned.remove(key);
        }
        this.save();
    }

    void cleanupLegacyForcedChunksIfRequested() {
        File marker = new File(this.stateFile.getParentFile(), this.stateFile.getName() + ".cleanup-all");
        if (!marker.isFile()) return;
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (org.bukkit.Chunk chunk : Set.copyOf(world.getForceLoadedChunks())) {
                world.setChunkForceLoaded(chunk.getX(), chunk.getZ(), false);
                count++;
            }
        }
        if (!marker.delete()) this.logger.warning("Could not delete Board cleanup marker: " + marker);
        this.logger.info("Board forced-chunk one-time cleanup released " + count + " chunks.");
    }

    void beginCycle() { this.desired.clear(); }

    void acquire(World world, int x, int z) {
        ChunkKey key = new ChunkKey(world.getName(), x, z);
        this.desired.add(key);
        if (this.owned.contains(key)) return;
        if (world.isChunkForceLoaded(x, z)) {
            this.logger.warning("Board uses an already force-loaded chunk without ownership: " + key);
            return;
        }
        world.setChunkForceLoaded(x, z, true);
        this.owned.add(key);
        if (!this.save()) {
            this.owned.remove(key);
            world.setChunkForceLoaded(x, z, false);
            this.logger.severe("Released Board chunk because ownership could not be persisted: " + key);
        }
    }

    void endCycle() {
        boolean changed = false;
        for (ChunkKey key : Set.copyOf(this.owned)) {
            if (this.desired.contains(key)) continue;
            World world = Bukkit.getWorld(key.worldName());
            if (world == null) continue;
            world.setChunkForceLoaded(key.x(), key.z(), false);
            this.owned.remove(key);
            changed = true;
        }
        if (changed) this.save();
    }

    void releaseAll() {
        this.desired.clear();
        for (ChunkKey key : Set.copyOf(this.owned)) {
            World world = Bukkit.getWorld(key.worldName());
            if (world == null) continue;
            world.setChunkForceLoaded(key.x(), key.z(), false);
            this.owned.remove(key);
        }
        this.save();
    }

    private void load() {
        if (!this.stateFile.isFile()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.stateFile);
        for (String encoded : yaml.getStringList("owned")) {
            String[] parts = encoded.split(":", 3);
            if (parts.length != 3) continue;
            try { this.owned.add(new ChunkKey(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]))); }
            catch (NumberFormatException exception) { this.logger.warning("Ignoring invalid Board chunk ownership: " + encoded); }
        }
    }

    private boolean save() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("owned", this.owned.stream().map(ChunkKey::toString).sorted().toList());
        File temporary = new File(this.stateFile.getParentFile(), this.stateFile.getName() + ".tmp");
        try {
            File parent = this.stateFile.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs()) throw new IOException("Could not create " + parent);
            yaml.save(temporary);
            try {
                Files.move(temporary.toPath(), this.stateFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary.toPath(), this.stateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException exception) {
            if (temporary.isFile() && !temporary.delete()) this.logger.warning("Could not remove Board ownership temporary file: " + temporary);
            this.logger.severe("Could not persist Board chunk ownership: " + exception.getMessage());
            return false;
        }
    }

    private record ChunkKey(String worldName, int x, int z) {
        @Override public String toString() { return this.worldName + ":" + this.x + ":" + this.z; }
    }
}
