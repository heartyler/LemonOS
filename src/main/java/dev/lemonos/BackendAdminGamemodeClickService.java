package dev.lemonos;

import org.bukkit.GameMode;

final class BackendAdminGamemodeClickService {
    AdminGamemodeClick action(int clickedSlot, int backSlot, GameMode currentGameMode) {
        if (clickedSlot == backSlot) {
            return new AdminGamemodeClick(AdminGamemodeAction.BACK, null);
        }
        GameMode gameMode = this.gameModeForSlot(clickedSlot);
        if (gameMode != null) {
            if (gameMode == currentGameMode) {
                return new AdminGamemodeClick(AdminGamemodeAction.CURRENT, gameMode);
            }
            return new AdminGamemodeClick(AdminGamemodeAction.SELECT, gameMode);
        }
        return new AdminGamemodeClick(AdminGamemodeAction.NONE, null);
    }

    private GameMode gameModeForSlot(int slot) {
        return switch (slot) {
            case 11 -> GameMode.ADVENTURE;
            case 12 -> GameMode.SURVIVAL;
            case 13 -> GameMode.CREATIVE;
            case 14 -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    record AdminGamemodeClick(AdminGamemodeAction action, GameMode gameMode) {
    }

    enum AdminGamemodeAction {
        NONE,
        BACK,
        CURRENT,
        SELECT;
    }
}
