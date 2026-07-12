package dev.lemonos;

import java.util.List;

final class BackendPasscodeDisplayService {
    private final BackendPasscodeLayout layout;

    BackendPasscodeDisplayService(BackendPasscodeLayout layout) {
        this.layout = layout;
    }

    PasscodeDisplay display(boolean creating, String passcode, String status, BackendPasscodeInputService.EnterState enterState) {
        String value = passcode == null ? "" : passcode;
        return new PasscodeDisplay(
                title(creating, value, status),
                digitButtons(),
                !value.isEmpty(),
                !creating,
                enterState);
    }

    TitlePlan title(boolean creating, String passcode, String status) {
        String value = passcode == null ? "" : passcode;
        if (status != null) {
            return new TitlePlan(creating ? TitleKind.CREATE_STATUS : TitleKind.LOGIN_STATUS, status, 0);
        }
        if (value.isEmpty()) {
            return creating ? new TitlePlan(TitleKind.CREATE_EMPTY, null, 0) : new TitlePlan(TitleKind.LOGIN_EMPTY, null, 0);
        }
        return new TitlePlan(creating ? TitleKind.CREATE_MASKED : TitleKind.LOGIN_MASKED, null, Math.min(value.length(), 8));
    }

    List<DigitButton> digitButtons() {
        return this.layout.digitButtons().stream().map(button -> new DigitButton(button.slot(), button.label())).toList();
    }

    record PasscodeDisplay(
            TitlePlan title,
            List<DigitButton> digits,
            boolean showClear,
            boolean showReset,
            BackendPasscodeInputService.EnterState enterState) {
    }

    record TitlePlan(TitleKind kind, String status, int maskLength) {
    }

    record DigitButton(int slot, String label) {
    }

    enum TitleKind {
        CREATE_EMPTY,
        CREATE_STATUS,
        CREATE_MASKED,
        LOGIN_EMPTY,
        LOGIN_STATUS,
        LOGIN_MASKED;
    }
}
