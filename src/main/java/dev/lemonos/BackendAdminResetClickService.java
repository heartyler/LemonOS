package dev.lemonos;

final class BackendAdminResetClickService {
    AdminResetAction action(int clickedSlot, int allowSlot, int denySlot) {
        if (clickedSlot == allowSlot) {
            return AdminResetAction.ALLOW;
        }
        if (clickedSlot == denySlot) {
            return AdminResetAction.DENY;
        }
        return AdminResetAction.NONE;
    }

    enum AdminResetAction {
        NONE,
        ALLOW,
        DENY;
    }
}
