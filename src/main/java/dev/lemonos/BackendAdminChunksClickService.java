package dev.lemonos;

final class BackendAdminChunksClickService {
    AdminChunksAction action(int clickedSlot, int backSlot, int centerSlot, int sizeSlot, int dimensionSlot, int cancelSlot, int startSlot) {
        if (clickedSlot == backSlot) {
            return AdminChunksAction.BACK;
        }
        if (clickedSlot == centerSlot) {
            return AdminChunksAction.CENTER;
        }
        if (clickedSlot == sizeSlot) {
            return AdminChunksAction.SIZE;
        }
        if (clickedSlot == dimensionSlot) {
            return AdminChunksAction.DIMENSION;
        }
        if (clickedSlot == cancelSlot) {
            return AdminChunksAction.CANCEL;
        }
        if (clickedSlot == startSlot) {
            return AdminChunksAction.START;
        }
        return AdminChunksAction.NONE;
    }

    enum AdminChunksAction {
        NONE,
        BACK,
        CENTER,
        SIZE,
        DIMENSION,
        CANCEL,
        START;
    }
}
