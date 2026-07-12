package dev.lemonos;

final class BackendPasscodeInputService {
    private final BackendPasscodeLayout layout;

    BackendPasscodeInputService(BackendPasscodeLayout layout) {
        this.layout = layout;
    }

    Integer digit(int slot) {
        return this.layout.digit(slot);
    }

    EnterState enterState(String passcode, boolean overflow) {
        String value = passcode == null ? "" : passcode;
        if (overflow) {
            return EnterState.TOO_LONG;
        }
        if (value.isEmpty()) {
            return EnterState.HIDDEN;
        }
        if (value.length() < 4) {
            return EnterState.TOO_SHORT;
        }
        return EnterState.READY;
    }

    LoginMode resolveLoginMode(LoginMode current, boolean registeredWithoutResetGrant, boolean resetRequestExists, boolean resetGrantExists) {
        LoginMode mode = current;
        if (mode == null) {
            mode = registeredWithoutResetGrant ? LoginMode.LOGIN : LoginMode.CREATE_PASSCODE;
        }
        if (resetRequestExists && mode == LoginMode.LOGIN) {
            mode = LoginMode.RESET_WAITING;
        }
        if (resetGrantExists) {
            mode = LoginMode.CREATE_PASSCODE;
        }
        if (mode == LoginMode.RESET_WAITING && !resetRequestExists) {
            mode = LoginMode.LOGIN;
        }
        return mode;
    }

    enum EnterState {
        HIDDEN,
        TOO_SHORT,
        TOO_LONG,
        READY;
    }

    enum LoginMode {
        CREATE_PASSCODE,
        LOGIN,
        RESET_WAITING;
    }
}
