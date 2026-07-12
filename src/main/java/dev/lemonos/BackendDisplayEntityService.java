/*
 * Backend-side LemonOS display entity style model.
 */
package dev.lemonos;

final class BackendDisplayEntityService {
    EntityStyle stayedCloseStyle(BackendDisplayConfig config, String role, boolean bedrockRole, boolean bottomRole, boolean bedrockBottomRole) {
        float scale = (float)(bedrockBottomRole ? config.doubleValue("stayed-close.display.bedrock.bottom-scale", 0.42, 0.25, 2.00) : (bottomRole ? config.doubleValue("stayed-close.display.bottom-scale", 0.42, 0.25, 2.00) : (bedrockRole ? config.doubleValue("stayed-close.display.bedrock.scale", 0.53, 0.25, 2.00) : config.doubleValue("stayed-close.display.scale", 0.53, 0.25, 2.00))));
        int backgroundAlpha = bedrockRole ? config.intValue("stayed-close.display.bedrock.background-alpha", 0, 0, 255) : config.intValue("stayed-close.display.background-alpha", 0, 0, 255);
        int viewRange = config.intValue("stayed-close.display.view-range", 32, 1, 128);
        int lineWidth = bedrockBottomRole ? config.intValue("stayed-close.display.bedrock.bottom-line-width", 260, 60, 800) : (bottomRole ? config.intValue("stayed-close.display.bottom-line-width", 260, 60, 800) : (bedrockRole ? config.intValue("stayed-close.display.bedrock.line-width", 260, 60, 800) : config.intValue("stayed-close.display.line-width", 220, 60, 800)));
        return new EntityStyle(scale, backgroundAlpha, viewRange, lineWidth);
    }

    EntityStyle hudStyle(BackendDisplayConfig config, String configPath, String role) {
        boolean bedrockRole = role.contains("bedrock_");
        boolean bedrockBottomRole = role.endsWith("bedrock_bottom");
        boolean bottomRole = role.endsWith("bottom") && !bedrockBottomRole;
        float scale = (float)(bedrockBottomRole ? config.doubleValue(configPath + ".display.bedrock.bottom-scale", 0.42, 0.25, 2.00) : (bottomRole ? config.doubleValue(configPath + ".display.bottom-scale", 0.42, 0.25, 2.00) : (bedrockRole ? config.doubleValue(configPath + ".display.bedrock.scale", 0.53, 0.25, 2.00) : config.doubleValue(configPath + ".display.scale", 0.53, 0.25, 2.00))));
        int backgroundAlpha = bedrockRole ? config.intValue(configPath + ".display.bedrock.background-alpha", 0, 0, 255) : config.intValue(configPath + ".display.background-alpha", 0, 0, 255);
        int viewRange = config.intValue(configPath + ".display.view-range", 32, 1, 128);
        int lineWidth = bedrockBottomRole ? config.intValue(configPath + ".display.bedrock.bottom-line-width", 260, 60, 800) : (bottomRole ? config.intValue(configPath + ".display.bottom-line-width", 260, 60, 800) : (bedrockRole ? config.intValue(configPath + ".display.bedrock.line-width", 260, 60, 800) : config.intValue(configPath + ".display.line-width", 220, 60, 800)));
        return new EntityStyle(scale, backgroundAlpha, viewRange, lineWidth);
    }

    static final class EntityStyle {
        private final float scale;
        private final int backgroundAlpha;
        private final int viewRange;
        private final int lineWidth;

        private EntityStyle(float scale, int backgroundAlpha, int viewRange, int lineWidth) {
            this.scale = scale;
            this.backgroundAlpha = backgroundAlpha;
            this.viewRange = viewRange;
            this.lineWidth = lineWidth;
        }

        float scale() {
            return this.scale;
        }

        int backgroundAlpha() {
            return this.backgroundAlpha;
        }

        int viewRange() {
            return this.viewRange;
        }

        int lineWidth() {
            return this.lineWidth;
        }
    }
}
