package dev.lemonos;

final class BackendSandboxDrawingShapeService {
    boolean shouldDraw(ShapePolicy policy, int x, int y, int z, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        return switch (policy) {
            case VOLUME -> true;
            case FLOOR -> y == minY;
            case WALL -> x == minX || x == maxX || z == minZ || z == maxZ;
            case NONE -> false;
        };
    }

    enum ShapePolicy {
        VOLUME,
        WALL,
        FLOOR,
        NONE
    }
}
