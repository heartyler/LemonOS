package dev.lemonos;

import org.bukkit.Location;
import org.bukkit.World;

/** Resolves an exact collision-safe first-join location from the canonical world spawn. */
final class BackendFirstJoinSpawnService {
    Location resolve(World world) {
        if (world == null) return null;
        Location spawn = world.getSpawnLocation();
        Location exact = this.centered(spawn);
        if (this.safe(exact)) return exact;
        int maximum = Math.min(world.getMaxHeight() - 2, spawn.getBlockY() + 16);
        for (int y = spawn.getBlockY() + 1; y <= maximum; y++) {
            Location candidate = new Location(world, spawn.getBlockX() + 0.5, y, spawn.getBlockZ() + 0.5, spawn.getYaw(), spawn.getPitch());
            if (this.safe(candidate)) return candidate;
        }
        return null;
    }

    boolean safe(Location location) {
        if (location == null || location.getWorld() == null) return false;
        int y = location.getBlockY();
        World world = location.getWorld();
        if (y <= world.getMinHeight() || y + 1 >= world.getMaxHeight()) return false;
        return location.getBlock().isPassable()
                && location.clone().add(0.0, 1.0, 0.0).getBlock().isPassable()
                && location.clone().add(0.0, -1.0, 0.0).getBlock().getType().isSolid();
    }

    private Location centered(Location spawn) {
        return new Location(spawn.getWorld(), spawn.getBlockX() + 0.5, spawn.getBlockY(), spawn.getBlockZ() + 0.5, spawn.getYaw(), spawn.getPitch());
    }
}
