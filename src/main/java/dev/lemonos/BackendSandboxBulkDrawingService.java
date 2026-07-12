package dev.lemonos;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import net.kyori.adventure.text.format.NamedTextColor;

final class BackendSandboxBulkDrawingService {
    private final BackendSandboxDrawingShapeService drawingShapeService;
    private final BackendSandboxBlockChangeService blockChangeService;

    BackendSandboxBulkDrawingService(BackendSandboxDrawingShapeService drawingShapeService, BackendSandboxBlockChangeService blockChangeService) {
        this.drawingShapeService = drawingShapeService;
        this.blockChangeService = blockChangeService;
    }

    void buildChanges(World world, Bounds bounds, BackendSandboxDrawingShapeService.ShapePolicy shapePolicy, boolean replace, BlockData sourceData, BlockData newData, ChangeConsumer consumer) {
        for (int x = bounds.minX(); x <= bounds.maxX(); ++x) {
            for (int y = bounds.minY(); y <= bounds.maxY(); ++y) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); ++z) {
                    if (!this.drawingShapeService.shouldDraw(shapePolicy, x, y, z, bounds.minX(), bounds.maxX(), bounds.minY(), bounds.maxY(), bounds.minZ(), bounds.maxZ())) {
                        continue;
                    }
                    Block block = world.getBlockAt(x, y, z);
                    BlockData oldData = block.getBlockData();
                    if (replace && !this.blockChangeService.sameBlockData(oldData, sourceData)) {
                        continue;
                    }
                    if (!this.blockChangeService.shouldAddChange(oldData, newData)) {
                        continue;
                    }
                    consumer.accept(world, x, y, z, oldData, newData);
                }
            }
        }
    }

    ApplyStatus applyStatus(boolean verified, boolean changed) {
        if (!verified) {
            return new ApplyStatus(false, "try again.", NamedTextColor.DARK_GRAY);
        }
        if (changed) {
            return new ApplyStatus(true, "done.", NamedTextColor.GRAY);
        }
        return new ApplyStatus(true, "nothing changed.", NamedTextColor.DARK_GRAY);
    }

    record Bounds(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
    }

    record ApplyStatus(boolean verified, String message, NamedTextColor color) {
    }

    interface ChangeConsumer {
        void accept(World world, int x, int y, int z, BlockData oldData, BlockData newData);
    }
}
