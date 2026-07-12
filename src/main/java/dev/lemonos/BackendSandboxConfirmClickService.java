package dev.lemonos;

final class BackendSandboxConfirmClickService {
    ConfirmAction action(int clickedSlot, int confirmSlot, int cancelSlot) {
        if (clickedSlot == confirmSlot) {
            return ConfirmAction.CONFIRM;
        }
        if (clickedSlot == cancelSlot) {
            return ConfirmAction.CANCEL;
        }
        return ConfirmAction.NONE;
    }

    enum ConfirmAction {
        NONE,
        CONFIRM,
        CANCEL;
    }
}
