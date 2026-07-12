package dev.lemonos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;

final class BackendSandboxClonePreviewPlanService {
    ClonePreviewPlan build(World world, BackendSandboxGeometryService.SelectionBounds bounds, Location location, Predicate<Entity> excludedEntity) {
        int targetMinX = location.getBlockX();
        int targetMinY = location.getBlockY();
        int targetMinZ = location.getBlockZ();
        int targetMaxX = targetMinX + bounds.sizeX() - 1;
        int targetMaxY = targetMinY + bounds.sizeY() - 1;
        int targetMaxZ = targetMinZ + bounds.sizeZ() - 1;
        ArrayList<SourceBlock> blocks = new ArrayList<SourceBlock>();
        for (int x = bounds.minX(); x <= bounds.maxX(); ++x) {
            for (int y = bounds.minY(); y <= bounds.maxY(); ++y) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); ++z) {
                    blocks.add(new SourceBlock(x - bounds.minX(), y - bounds.minY(), z - bounds.minZ(), world.getBlockAt(x, y, z).getBlockData()));
                }
            }
        }
        ArrayList<SourceEntity> entities = new ArrayList<SourceEntity>();
        for (Entity entity : world.getEntities()) {
            if (excludedEntity.test(entity) || !this.inside(entity.getLocation(), bounds)) {
                continue;
            }
            EntitySnapshot snapshot = entity.createSnapshot();
            if (snapshot == null) {
                throw new IllegalStateException("Entity snapshot unavailable for " + entity.getType());
            }
            Entity vehicle = entity.getVehicle();
            UUID vehicleId = vehicle == null || excludedEntity.test(vehicle) || !this.inside(vehicle.getLocation(), bounds)
                    ? null
                    : vehicle.getUniqueId();
            Location source = entity.getLocation();
            entities.add(new SourceEntity(
                    entity.getUniqueId(),
                    vehicleId,
                    source.getX() - bounds.minX(),
                    source.getY() - bounds.minY(),
                    source.getZ() - bounds.minZ(),
                    source.getYaw(),
                    source.getPitch(),
                    snapshot));
        }
        return new ClonePreviewPlan(targetMinX, targetMinY, targetMinZ, targetMaxX, targetMaxY, targetMaxZ, blocks, entities);
    }

    private boolean inside(Location location, BackendSandboxGeometryService.SelectionBounds bounds) {
        return location.getX() >= bounds.minX() && location.getX() < bounds.maxX() + 1.0
                && location.getY() >= bounds.minY() && location.getY() < bounds.maxY() + 1.0
                && location.getZ() >= bounds.minZ() && location.getZ() < bounds.maxZ() + 1.0;
    }

    boolean outsideVerticalRange(ClonePreviewPlan plan, int minHeight, int maxHeight) {
        return plan.minY() < minHeight || plan.maxY() >= maxHeight;
    }

    record ClonePreviewPlan(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, List<SourceBlock> blocks, List<SourceEntity> entities) {
    }

    record SourceBlock(int offsetX, int offsetY, int offsetZ, BlockData blockData) {
    }

    record SourceEntity(UUID sourceId, UUID vehicleSourceId, double offsetX, double offsetY, double offsetZ, float yaw, float pitch, EntitySnapshot snapshot) {
    }
}
