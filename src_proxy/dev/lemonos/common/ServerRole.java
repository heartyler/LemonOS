/*
 * Decompiled with CFR 0.152.
 */
package dev.lemonos.common;

import java.nio.file.Path;
import java.util.Locale;

public enum ServerRole {
    LOBBY,
    SURVIVAL,
    CREATIVE,
    UNKNOWN;


    public static ServerRole fromConfiguredValue(String string, Path path) {
        if (string != null && !string.isBlank() && !"auto".equalsIgnoreCase(string)) {
            return ServerRole.fromName(string);
        }
        if (path == null || path.getFileName() == null) {
            return UNKNOWN;
        }
        return ServerRole.fromName(path.getFileName().toString());
    }

    public static ServerRole fromName(String string) {
        if (string == null) {
            return UNKNOWN;
        }
        return switch (string.trim().toLowerCase(Locale.ROOT)) {
            case "lobby" -> LOBBY;
            case "survival" -> SURVIVAL;
            case "creative" -> CREATIVE;
            default -> UNKNOWN;
        };
    }

    public String configName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
