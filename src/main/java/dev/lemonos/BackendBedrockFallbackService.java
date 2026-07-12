package dev.lemonos;

import java.util.ArrayList;
import java.util.List;

final class BackendBedrockFallbackService {
    List<FallbackButton> homeButtons(
            boolean currentServerLobby,
            boolean peopleShortcutPublic,
            boolean sandboxAvailable,
            boolean admin) {
        List<FallbackButton> buttons = new ArrayList<FallbackButton>();
        buttons.add(FallbackButton.LOOK);
        if (!currentServerLobby && peopleShortcutPublic) {
            buttons.add(FallbackButton.PEOPLE);
        }
        buttons.add(FallbackButton.PLACES);
        if (sandboxAvailable) {
            buttons.add(FallbackButton.SANDBOX);
        }
        if (admin) {
            buttons.add(FallbackButton.CARE);
        }
        return buttons;
    }

    enum FallbackButton {
        LOOK,
        PEOPLE,
        PLACES,
        SANDBOX,
        CARE;
    }
}
