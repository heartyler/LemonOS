package dev.lemonos;

import java.util.Map;
import java.util.UUID;

final class BackendAdminPeopleClickService {
    AdminPeopleClick action(int clickedSlot, int backSlot, int nextPageSlot, Map<Integer, UUID> slotTargets) {
        if (clickedSlot == backSlot) {
            return new AdminPeopleClick(AdminPeopleAction.BACK, null);
        }
        if (clickedSlot == nextPageSlot) {
            return new AdminPeopleClick(AdminPeopleAction.NEXT_PAGE, null);
        }
        UUID target = slotTargets == null ? null : slotTargets.get(clickedSlot);
        if (target != null) {
            return new AdminPeopleClick(AdminPeopleAction.SELECT, target);
        }
        return new AdminPeopleClick(AdminPeopleAction.NONE, null);
    }

    record AdminPeopleClick(AdminPeopleAction action, UUID target) {
    }

    enum AdminPeopleAction {
        NONE,
        BACK,
        NEXT_PAGE,
        SELECT;
    }
}
