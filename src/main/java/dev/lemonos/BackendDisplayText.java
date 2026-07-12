/*
 * Backend-side LemonOS display text helpers.
 */
package dev.lemonos;

final class BackendDisplayText {
    private BackendDisplayText() {
    }

    static String fitName(String string, int limit) {
        String value = string == null || string.isBlank() ? "" : string.trim();
        if (value.length() <= limit) {
            return value;
        }
        if (limit <= 1) {
            return value.substring(0, limit);
        }
        return value.substring(0, limit - 1) + ".";
    }
}
