package dev.lemonos;

final class BackendAdminKeysClickService {
    AdminKeysAction action(int clickedSlot, int backSlot, int giveSlot, int holdersSlot) {
        if (clickedSlot == backSlot) {
            return AdminKeysAction.BACK;
        }
        if (clickedSlot == giveSlot) {
            return AdminKeysAction.GIVE;
        }
        if (clickedSlot == holdersSlot) {
            return AdminKeysAction.HOLDERS;
        }
        return AdminKeysAction.NONE;
    }

    enum AdminKeysAction {
        NONE,
        BACK,
        GIVE,
        HOLDERS;
    }
}
