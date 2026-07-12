package dev.lemonos;

import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.Location;

final class BackendSandboxGeometryService {
    boolean validSelection(Location first, Location second, int maxBlocks) {
        if (first == null || second == null || first.getWorld() == null || !first.getWorld().equals(second.getWorld())) {
            return false;
        }
        int volume = this.volume(first, second);
        return volume > 0 && volume <= maxBlocks;
    }

    int volume(Location first, Location second) {
        SelectionBounds bounds = this.bounds(first, second);
        return bounds == null ? 0 : bounds.volume();
    }

    SelectionBounds bounds(Location first, Location second) {
        if (first == null || second == null) {
            return null;
        }
        return new SelectionBounds(
                Math.min(first.getBlockX(), second.getBlockX()),
                Math.max(first.getBlockX(), second.getBlockX()),
                Math.min(first.getBlockY(), second.getBlockY()),
                Math.max(first.getBlockY(), second.getBlockY()),
                Math.min(first.getBlockZ(), second.getBlockZ()),
                Math.max(first.getBlockZ(), second.getBlockZ()));
    }

    BlockPoint rotatedPosition(int x, int y, int z, double centerX, double centerZ, int rotation) {
        double offsetX = (double)x - centerX;
        double offsetZ = (double)z - centerZ;
        double rotatedX;
        double rotatedZ;
        if (rotation == 180) {
            rotatedX = -offsetX;
            rotatedZ = -offsetZ;
        } else if (rotation == 270) {
            rotatedX = offsetZ;
            rotatedZ = -offsetX;
        } else {
            rotatedX = -offsetZ;
            rotatedZ = offsetX;
        }
        return new BlockPoint((int)Math.round(centerX + rotatedX), y, (int)Math.round(centerZ + rotatedZ));
    }

    BlockPoint flippedPosition(int x, int y, int z, double centerX, double centerZ, char axis) {
        if (axis == 'x') {
            double offsetX = (double)x - centerX;
            return new BlockPoint((int)Math.round(centerX - offsetX), y, z);
        }
        double offsetZ = (double)z - centerZ;
        return new BlockPoint(x, y, (int)Math.round(centerZ - offsetZ));
    }

    BlockData rotateBlockData(BlockData blockData, int rotation) {
        BlockData rotated = blockData.clone();
        int quarterTurns = rotation / 90;
        for (int i = 0; i < quarterTurns; ++i) {
            rotated = this.rotateBlockDataOnce(rotated);
        }
        return rotated;
    }

    BlockData flipBlockData(BlockData blockData, char axis) {
        BlockData flipped = blockData.clone();
        if (flipped instanceof Directional) {
            Directional directional = (Directional)flipped;
            BlockFace mirroredFace = this.mirrorFace(directional.getFacing(), axis);
            if (directional.getFaces().contains(mirroredFace)) {
                directional.setFacing(mirroredFace);
            }
        }
        if (flipped instanceof Rotatable) {
            Rotatable rotatable = (Rotatable)flipped;
            rotatable.setRotation(this.mirrorFace(rotatable.getRotation(), axis));
        }
        return flipped;
    }

    private BlockData rotateBlockDataOnce(BlockData blockData) {
        if (blockData instanceof Directional) {
            Directional directional = (Directional)blockData;
            BlockFace rotatedFace = this.rotateFace(directional.getFacing());
            if (directional.getFaces().contains(rotatedFace)) {
                directional.setFacing(rotatedFace);
            }
        }
        if (blockData instanceof Orientable) {
            Orientable orientable = (Orientable)blockData;
            Axis axis = orientable.getAxis();
            if (axis == Axis.X && orientable.getAxes().contains(Axis.Z)) {
                orientable.setAxis(Axis.Z);
            } else if (axis == Axis.Z && orientable.getAxes().contains(Axis.X)) {
                orientable.setAxis(Axis.X);
            }
        }
        if (blockData instanceof Rotatable) {
            Rotatable rotatable = (Rotatable)blockData;
            rotatable.setRotation(this.rotateFace(rotatable.getRotation()));
        }
        return blockData;
    }

    private BlockFace mirrorFace(BlockFace blockFace, char axis) {
        if (axis == 'x') {
            return switch (blockFace) {
                case EAST -> BlockFace.WEST;
                case WEST -> BlockFace.EAST;
                case NORTH_EAST -> BlockFace.NORTH_WEST;
                case NORTH_WEST -> BlockFace.NORTH_EAST;
                case SOUTH_EAST -> BlockFace.SOUTH_WEST;
                case SOUTH_WEST -> BlockFace.SOUTH_EAST;
                default -> blockFace;
            };
        }
        return switch (blockFace) {
            case NORTH -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.NORTH;
            case NORTH_EAST -> BlockFace.SOUTH_EAST;
            case SOUTH_EAST -> BlockFace.NORTH_EAST;
            case NORTH_WEST -> BlockFace.SOUTH_WEST;
            case SOUTH_WEST -> BlockFace.NORTH_WEST;
            default -> blockFace;
        };
    }

    private BlockFace rotateFace(BlockFace blockFace) {
        return switch (blockFace) {
            case NORTH -> BlockFace.EAST;
            case EAST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.WEST;
            case WEST -> BlockFace.NORTH;
            case NORTH_EAST -> BlockFace.SOUTH_EAST;
            case SOUTH_EAST -> BlockFace.SOUTH_WEST;
            case SOUTH_WEST -> BlockFace.NORTH_WEST;
            case NORTH_WEST -> BlockFace.NORTH_EAST;
            default -> blockFace;
        };
    }

    record SelectionBounds(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        int sizeX() {
            return this.maxX - this.minX + 1;
        }

        int sizeY() {
            return this.maxY - this.minY + 1;
        }

        int sizeZ() {
            return this.maxZ - this.minZ + 1;
        }

        int volume() {
            return this.sizeX() * this.sizeY() * this.sizeZ();
        }
    }

    record BlockPoint(int x, int y, int z) {
    }
}
