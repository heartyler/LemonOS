package dev.lemonos;

import net.kyori.adventure.text.format.NamedTextColor;

final class BackendSandboxCenterDrawingService {
    int sphereAffectedBlocks(int radius) {
        int affectedBlocks = 0;
        int radiusSquared = radius * radius;
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    if (x * x + y * y + z * z > radiusSquared) {
                        continue;
                    }
                    ++affectedBlocks;
                }
            }
        }
        return affectedBlocks;
    }

    void forEachSphereBlock(int centerX, int centerY, int centerZ, int radius, SphereBlockConsumer consumer) {
        int radiusSquared = radius * radius;
        for (int x = centerX - radius; x <= centerX + radius; ++x) {
            for (int y = centerY - radius; y <= centerY + radius; ++y) {
                for (int z = centerZ - radius; z <= centerZ + radius; ++z) {
                    int offsetX = x - centerX;
                    int offsetY = y - centerY;
                    int offsetZ = z - centerZ;
                    if (offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ > radiusSquared) {
                        continue;
                    }
                    consumer.accept(x, y, z);
                }
            }
        }
    }

    boolean withinVerticalRange(int centerY, int radius, int minHeight, int maxHeight) {
        return centerY - radius >= minHeight && centerY + radius < maxHeight;
    }

    boolean sphereTooLarge(int radius, int maxBlocks) {
        int affectedBlocks = this.sphereAffectedBlocks(radius);
        return affectedBlocks <= 0 || affectedBlocks > maxBlocks;
    }

    RepeatStatus repeatStatus(boolean changed, boolean repeatDoneShown) {
        if (!changed) {
            return new RepeatStatus(false, true, "nothing changed.", NamedTextColor.DARK_GRAY);
        }
        if (!repeatDoneShown) {
            return new RepeatStatus(true, true, "done.", NamedTextColor.GRAY);
        }
        return new RepeatStatus(true, false, "", NamedTextColor.GRAY);
    }

    record RepeatStatus(boolean repeatDoneShown, boolean sendMainStatus, String message, NamedTextColor color) {
    }

    interface SphereBlockConsumer {
        void accept(int x, int y, int z);
    }

}
