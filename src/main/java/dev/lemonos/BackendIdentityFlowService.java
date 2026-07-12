package dev.lemonos;

import java.util.function.BooleanSupplier;

final class BackendIdentityFlowService {
    BeginAction bedrockBeginAction(boolean bedrockTrusted, BooleanSupplier transferAccepted) {
        if (!bedrockTrusted) {
            return BeginAction.KICK_UNAVAILABLE;
        }
        return transferAccepted.getAsBoolean() ? BeginAction.TRUSTED_TRANSFERRED : BeginAction.TRUSTED;
    }

    BeginAction javaBeginAction(
            boolean javaLoginEnabled,
            BooleanSupplier resetGrantExists,
            BooleanSupplier transferAccepted,
            BooleanSupplier sessionAccepted,
            BooleanSupplier registered) {
        if (!javaLoginEnabled) {
            return BeginAction.TRUSTED;
        }
        if (resetGrantExists.getAsBoolean()) {
            return BeginAction.FORCE_PASSCODE_RESET;
        }
        if (transferAccepted.getAsBoolean()) {
            return BeginAction.TRUSTED_TRANSFERRED;
        }
        if (sessionAccepted.getAsBoolean()) {
            return BeginAction.TRUSTED;
        }
        return registered.getAsBoolean() ? BeginAction.PASSCODE_LOGIN : BeginAction.PASSCODE_CREATE;
    }

    enum BeginAction {
        KICK_UNAVAILABLE,
        TRUSTED,
        TRUSTED_TRANSFERRED,
        FORCE_PASSCODE_RESET,
        PASSCODE_LOGIN,
        PASSCODE_CREATE;
    }
}
