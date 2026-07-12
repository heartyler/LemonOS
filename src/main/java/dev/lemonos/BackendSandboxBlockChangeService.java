package dev.lemonos;

import org.bukkit.block.data.BlockData;

final class BackendSandboxBlockChangeService {
    boolean sameBlockData(BlockData first, BlockData second) {
        return first != null && second != null && first.matches(second);
    }

    boolean sameExactBlockData(BlockData first, BlockData second) {
        return first != null && second != null && first.getAsString().equals(second.getAsString());
    }

    boolean shouldAddChange(BlockData oldData, BlockData newData) {
        return !this.sameExactBlockData(oldData, newData);
    }

    BlockData targetData(BlockData oldData, BlockData newData, boolean reverse) {
        return reverse ? oldData : newData;
    }
}
