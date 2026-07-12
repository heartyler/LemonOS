package dev.lemonos;

import org.bukkit.Location;
import org.bukkit.World;

/** Keeps the invisible Lobby movement boundary centered on the canonical world spawn. */
final class BackendLobbyBoundsService {
    private final double size;

    BackendLobbyBoundsService(double size) {
        if (!Double.isFinite(size) || size <= 0.0) throw new IllegalArgumentException("size must be positive");
        this.size = size;
    }

    boolean appliesTo(Location location, World lobbyWorld) {
        return location != null && lobbyWorld != null && location.getWorld() != null
                && location.getWorld().getUID().equals(lobbyWorld.getUID());
    }

    boolean outside(Location location, Location spawn) {
        Bounds bounds = this.bounds(spawn);
        return location.getX() < bounds.minX() || location.getX() > bounds.maxX()
                || location.getZ() < bounds.minZ() || location.getZ() > bounds.maxZ();
    }

    Location clamp(Location location, Location spawn) {
        Bounds bounds = this.bounds(spawn);
        Location clamped = location.clone();
        clamped.setX(Math.max(bounds.minX(), Math.min(bounds.maxX(), clamped.getX())));
        clamped.setZ(Math.max(bounds.minZ(), Math.min(bounds.maxZ(), clamped.getZ())));
        return clamped;
    }

    Bounds bounds(Location spawn) {
        if (spawn == null || spawn.getWorld() == null) throw new IllegalArgumentException("spawn must have a world");
        double half = this.size / 2.0;
        return new Bounds(spawn.getX() - half, spawn.getX() + half, spawn.getZ() - half, spawn.getZ() + half);
    }

    record Bounds(double minX, double maxX, double minZ, double maxZ) {}
}
