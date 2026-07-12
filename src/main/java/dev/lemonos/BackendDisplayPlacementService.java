/*
 * Backend-side LemonOS display placement model.
 */
package dev.lemonos;

final class BackendDisplayPlacementService {
    Placement stayedClosePlacement(BackendDisplayConfig config, String fallbackWorld) {
        return this.placement(config, "stayed-close", fallbackWorld);
    }

    Placement hudPlacement(BackendDisplayConfig config, String configPath, String fallbackWorld) {
        return this.placement(config, configPath, fallbackWorld);
    }

    private Placement placement(BackendDisplayConfig config, String configPath, String fallbackWorld) {
        String worldName = config.stringValue(configPath + ".display.world", fallbackWorld);
        double x = config.doubleValue(configPath + ".display.x", 5.42, -30000000.0, 30000000.0);
        double y = config.doubleValue(configPath + ".display.y", -60.86, -2048.0, 2048.0);
        double z = config.doubleValue(configPath + ".display.z", 0.5, -30000000.0, 30000000.0);
        float yaw = (float)config.doubleValue(configPath + ".display.yaw", 90.0, -180.0, 180.0);
        float pitch = (float)config.doubleValue(configPath + ".display.pitch", 0.0, -90.0, 90.0);
        return new Placement(worldName, x, y, z, yaw, pitch);
    }

    static final class Placement {
        private final String worldName;
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;

        private Placement(String worldName, double x, double y, double z, float yaw, float pitch) {
            this.worldName = worldName;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        String worldName() {
            return this.worldName;
        }

        double x() {
            return this.x;
        }

        double y() {
            return this.y;
        }

        double z() {
            return this.z;
        }

        float yaw() {
            return this.yaw;
        }

        float pitch() {
            return this.pitch;
        }
    }
}
