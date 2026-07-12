package dev.lemonos;

final class BackendAdminUpkeepClickService {
    AdminUpkeepAction action(int clickedSlot, int backSlot, int backupSlot, int chunksSlot) {
        if (clickedSlot == backSlot) {
            return AdminUpkeepAction.BACK;
        }
        if (clickedSlot == backupSlot) {
            return AdminUpkeepAction.BACKUP;
        }
        if (clickedSlot == chunksSlot) {
            return AdminUpkeepAction.CHUNKS;
        }
        return AdminUpkeepAction.NONE;
    }

    enum AdminUpkeepAction {
        NONE,
        BACK,
        BACKUP,
        CHUNKS;
    }
}
