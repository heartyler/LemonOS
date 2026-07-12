package dev.lemonos;

final class BackendSandboxInteractionService {
    SandboxClickAction action(int clickedSlot) {
        return switch (clickedSlot) {
            case 0 -> SandboxClickAction.BACK;
            case 1 -> SandboxClickAction.UNDO;
            case 2 -> SandboxClickAction.REDO;
            case 3 -> SandboxClickAction.SET;
            case 4 -> SandboxClickAction.WALL;
            case 5 -> SandboxClickAction.FLOOR;
            case 12 -> SandboxClickAction.REPLACE;
            case 13 -> SandboxClickAction.CLONE;
            case 14 -> SandboxClickAction.CLEAR;
            case 21 -> SandboxClickAction.CIRCLE;
            case 22 -> SandboxClickAction.FLIP;
            case 23 -> SandboxClickAction.ROTATE;
            default -> SandboxClickAction.NONE;
        };
    }

    ConfirmAction confirmAction(int clickedSlot, int confirmSlot, int cancelSlot) {
        if (clickedSlot == confirmSlot) return ConfirmAction.CONFIRM;
        if (clickedSlot == cancelSlot) return ConfirmAction.CANCEL;
        return ConfirmAction.NONE;
    }

    enum SandboxClickAction {
        NONE,
        BACK,
        UNDO,
        REDO,
        SET,
        WALL,
        FLOOR,
        CLONE,
        CLEAR,
        CIRCLE,
        REPLACE,
        FLIP,
        ROTATE
    }

    enum ConfirmAction {
        NONE,
        CONFIRM,
        CANCEL
    }
}
