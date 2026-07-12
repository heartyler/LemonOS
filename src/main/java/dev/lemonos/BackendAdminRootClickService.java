package dev.lemonos;

final class BackendAdminRootClickService {
    AdminRootAction action(int clickedSlot, int homeSlot, int peopleSlot, int requestsSlot, int keysSlot,
            int worldSlot, int atmosphereSlot, int upkeepSlot, boolean worldExpanded) {
        if (clickedSlot == homeSlot) {
            return AdminRootAction.HOME;
        }
        if (clickedSlot == peopleSlot) {
            return AdminRootAction.PEOPLE;
        }
        if (clickedSlot == requestsSlot) {
            return AdminRootAction.REQUESTS;
        }
        if (clickedSlot == keysSlot) {
            return AdminRootAction.KEYS;
        }
        if (clickedSlot == worldSlot) {
            return AdminRootAction.TOGGLE_WORLD;
        }
        if (worldExpanded && clickedSlot == atmosphereSlot) {
            return AdminRootAction.ATMOSPHERE;
        }
        if (worldExpanded && clickedSlot == upkeepSlot) {
            return AdminRootAction.UPKEEP;
        }
        return AdminRootAction.NONE;
    }

    enum AdminRootAction {
        NONE,
        HOME,
        PEOPLE,
        REQUESTS,
        KEYS,
        TOGGLE_WORLD,
        ATMOSPHERE,
        UPKEEP;
    }
}
