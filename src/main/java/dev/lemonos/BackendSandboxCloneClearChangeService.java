package dev.lemonos;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

final class BackendSandboxCloneClearChangeService {
    void buildCloneChanges(World world, Iterable<ClonePlacementBlock> blocks, ChangeSink changeSink) {
        for (ClonePlacementBlock cloneBlock : blocks) {
            int x = cloneBlock.baseX() + cloneBlock.offsetX();
            int y = cloneBlock.baseY() + cloneBlock.offsetY();
            int z = cloneBlock.baseZ() + cloneBlock.offsetZ();
            Block block = world.getBlockAt(x, y, z);
            changeSink.add(world, x, y, z, block.getBlockData(), cloneBlock.blockData());
        }
    }

    void buildClearChanges(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockData clearData, ChangeSink changeSink) {
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Block block = world.getBlockAt(x, y, z);
                    changeSink.add(world, x, y, z, block.getBlockData(), clearData);
                }
            }
        }
    }

    interface ChangeSink {
        void add(World world, int x, int y, int z, BlockData oldData, BlockData newData);
    }

    record ClonePlacementBlock(int baseX, int baseY, int baseZ, int offsetX, int offsetY, int offsetZ, BlockData blockData) {
    }
}
