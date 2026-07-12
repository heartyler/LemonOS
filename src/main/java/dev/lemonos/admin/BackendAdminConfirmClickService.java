package dev.lemonos.admin;

public final class BackendAdminConfirmClickService {
    public ConfirmAction action(int clickedSlot, int confirmSlot, int cancelSlot) {
        return this.action(clickedSlot, confirmSlot, cancelSlot, true);
    }

    public ConfirmAction action(int clickedSlot, int confirmSlot, int cancelSlot, boolean confirmationAvailable) {
        if (clickedSlot == confirmSlot && confirmationAvailable) return ConfirmAction.CONFIRM;
        if (clickedSlot == cancelSlot) return ConfirmAction.CANCEL;
        return ConfirmAction.NONE;
    }

    public enum ConfirmAction { NONE, CONFIRM, CANCEL }
}
