package dev.lemonos;

final class BackendAdminKeyHolderClickService {
    AdminKeyHolderAction action(int clickedSlot, int backSlot, int takeSlot, String accessName) {
        if (clickedSlot == backSlot) {
            return AdminKeyHolderAction.BACK;
        }
        if (clickedSlot == takeSlot && accessName != null) {
            return AdminKeyHolderAction.TAKE;
        }
        return AdminKeyHolderAction.NONE;
    }

    enum AdminKeyHolderAction {
        NONE,
        BACK,
        TAKE;
    }
}
