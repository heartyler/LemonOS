package dev.lemonos;

final class BackendAdminChunksConfirmClickService {
    AdminChunksConfirmAction action(int clickedSlot, int startSlot, int cancelSlot) {
        if (clickedSlot == startSlot) {
            return AdminChunksConfirmAction.START;
        }
        if (clickedSlot == cancelSlot) {
            return AdminChunksConfirmAction.CANCEL;
        }
        return AdminChunksConfirmAction.NONE;
    }

    enum AdminChunksConfirmAction {
        NONE,
        START,
        CANCEL;
    }
}
