package dev.lemonos;

import net.kyori.adventure.text.format.NamedTextColor;

final class BackendSandboxInputFailureService {
    FailureResult fail(int currentFailures, String message) {
        int failures = currentFailures + 1;
        if (failures >= 3) {
            return new FailureResult(failures, true, "nothing changed.", NamedTextColor.DARK_GRAY);
        }
        return new FailureResult(failures, false, message, NamedTextColor.DARK_GRAY);
    }

    record FailureResult(int failures, boolean closeSession, String message, NamedTextColor color) {
    }
}
