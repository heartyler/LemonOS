/*
 * Backend-side LemonOS display model primitives.
 */
package dev.lemonos;

import java.util.List;

final class BackendDisplayModel {
    private final boolean bedrockEnabled;
    private final List<Entry> entries;

    BackendDisplayModel(boolean bedrockEnabled, List<Entry> entries) {
        this.bedrockEnabled = bedrockEnabled;
        this.entries = entries;
    }

    boolean bedrockEnabled() {
        return this.bedrockEnabled;
    }

    List<Entry> entries() {
        return this.entries;
    }

    static final class Entry {
        private final String role;
        private final double offsetX;
        private final double offsetY;
        private final double offsetZ;
        private final String text;
        private final ColorRole colorRole;
        private final Alignment alignment;

        Entry(String role, double offsetX, double offsetY, double offsetZ, String text, ColorRole colorRole, Alignment alignment) {
            this.role = role;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.text = text;
            this.colorRole = colorRole;
            this.alignment = alignment;
        }

        String role() {
            return this.role;
        }

        double offsetX() {
            return this.offsetX;
        }

        double offsetY() {
            return this.offsetY;
        }

        double offsetZ() {
            return this.offsetZ;
        }

        String text() {
            return this.text;
        }

        ColorRole colorRole() {
            return this.colorRole;
        }

        Alignment alignment() {
            return this.alignment;
        }
    }

    enum ColorRole {
        WHITE,
        GRAY
    }

    enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }
}
