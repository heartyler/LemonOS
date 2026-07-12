package dev.lemonos;

final class BackendAdminBackupConfirmClickService {
    AdminBackupConfirmAction action(int clickedSlot, int backupSlot, int cancelSlot) {
        if (clickedSlot == backupSlot) {
            return AdminBackupConfirmAction.BACKUP;
        }
        if (clickedSlot == cancelSlot) {
            return AdminBackupConfirmAction.CANCEL;
        }
        return AdminBackupConfirmAction.NONE;
    }

    enum AdminBackupConfirmAction {
        NONE,
        BACKUP,
        CANCEL;
    }
}
