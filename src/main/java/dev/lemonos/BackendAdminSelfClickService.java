package dev.lemonos;

final class BackendAdminSelfClickService {
    AdminSelfAction action(int clickedSlot, int backSlot, int gamemodeSlot, int clearSlot) {
        if (clickedSlot == backSlot) {
            return AdminSelfAction.BACK;
        }
        if (clickedSlot == gamemodeSlot) {
            return AdminSelfAction.GAMEMODE;
        }
        if (clickedSlot == clearSlot) {
            return AdminSelfAction.CLEAR;
        }
        return AdminSelfAction.NONE;
    }

    enum AdminSelfAction {
        NONE,
        BACK,
        GAMEMODE,
        CLEAR;
    }
}
