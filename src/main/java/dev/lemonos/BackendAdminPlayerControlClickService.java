package dev.lemonos;

final class BackendAdminPlayerControlClickService {
    AdminPlayerControlAction action(int clickedSlot, int backSlot, int gamemodeSlot, int clearSlot, int sendSlot) {
        if (clickedSlot == backSlot) {
            return AdminPlayerControlAction.BACK;
        }
        if (clickedSlot == gamemodeSlot) {
            return AdminPlayerControlAction.GAMEMODE;
        }
        if (clickedSlot == clearSlot) {
            return AdminPlayerControlAction.CLEAR;
        }
        if (clickedSlot == sendSlot) {
            return AdminPlayerControlAction.SEND;
        }
        return AdminPlayerControlAction.NONE;
    }

    enum AdminPlayerControlAction {
        NONE,
        BACK,
        GAMEMODE,
        CLEAR,
        SEND;
    }
}
