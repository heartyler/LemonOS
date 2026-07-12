package dev.lemonos;

final class BackendCubeeClickService {
    HomeAction homeAction(
            int clickedSlot,
            int lookSlot,
            int careSlot,
            boolean lookEnabled,
            boolean admin,
            boolean currentServerLobby,
            boolean currentServerSurvival,
            boolean peopleShortcutPublic,
            boolean sandboxAvailable) {
        if (clickedSlot == lookSlot && lookEnabled) {
            return HomeAction.LOOK;
        }
        if (clickedSlot == careSlot && admin) {
            return HomeAction.CARE;
        }
        if (clickedSlot == 12 && !currentServerLobby && peopleShortcutPublic) {
            return HomeAction.PEOPLE;
        }
        if (clickedSlot == 13) {
            return HomeAction.PLACES;
        }
        if (clickedSlot == 14 && currentServerSurvival) {
            return HomeAction.SURVIVAL_HOME;
        }
        if (clickedSlot == 14 && sandboxAvailable) {
            return HomeAction.SANDBOX;
        }
        return HomeAction.NONE;
    }

    boolean isNavBack(int clickedSlot, int backSlot) {
        return clickedSlot == backSlot;
    }

    enum HomeAction {
        NONE,
        LOOK,
        CARE,
        PEOPLE,
        PLACES,
        SURVIVAL_HOME,
        SANDBOX;
    }
}
