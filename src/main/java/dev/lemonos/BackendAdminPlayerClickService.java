package dev.lemonos;

final class BackendAdminPlayerClickService {
    AdminPlayerAction action(int clickedSlot, int backSlot, int controlSlot, int visitSlot, int inviteSlot, int messageSlot) {
        if (clickedSlot == backSlot) {
            return AdminPlayerAction.BACK;
        }
        if (clickedSlot == controlSlot) {
            return AdminPlayerAction.CONTROL;
        }
        if (clickedSlot == visitSlot) {
            return AdminPlayerAction.VISIT;
        }
        if (clickedSlot == inviteSlot) {
            return AdminPlayerAction.INVITE;
        }
        if (clickedSlot == messageSlot) {
            return AdminPlayerAction.MESSAGE;
        }
        return AdminPlayerAction.NONE;
    }

    enum AdminPlayerAction {
        NONE,
        BACK,
        CONTROL,
        VISIT,
        INVITE,
        MESSAGE;
    }
}
