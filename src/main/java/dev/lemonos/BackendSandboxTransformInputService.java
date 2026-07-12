package dev.lemonos;

import java.util.Locale;

final class BackendSandboxTransformInputService {
    Integer rotation(String input) {
        int value = this.parseInt(input, -1);
        return value == 90 || value == 180 || value == 270 ? value : null;
    }

    Character flipAxis(String input) {
        String value = input.trim().toLowerCase(Locale.ROOT);
        return value.equals("x") || value.equals("z") ? value.charAt(0) : null;
    }

    String failureMessage() {
        return "try again.";
    }

    private int parseInt(String input, int fallback) {
        try {
            return Integer.parseInt(input.trim());
        }
        catch (NumberFormatException numberFormatException) {
            return fallback;
        }
    }
}
