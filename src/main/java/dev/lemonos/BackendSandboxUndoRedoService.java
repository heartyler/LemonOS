package dev.lemonos;

import net.kyori.adventure.text.format.NamedTextColor;

final class BackendSandboxUndoRedoService {
    UndoRedoStatus missingChange() {
        return new UndoRedoStatus("nothing changed.", NamedTextColor.DARK_GRAY);
    }

    UndoRedoStatus applyFailed() {
        return new UndoRedoStatus("try again.", NamedTextColor.DARK_GRAY);
    }

    UndoRedoStatus applied() {
        return new UndoRedoStatus("done.", NamedTextColor.GRAY);
    }

    boolean undoUsesOldData() {
        return true;
    }

    boolean redoUsesOldData() {
        return false;
    }

    IdleRoute idleRoute(boolean busy, boolean undo) {
        if (busy) {
            return IdleRoute.BLOCKED;
        }
        return undo ? IdleRoute.UNDO : IdleRoute.REDO;
    }

    enum IdleRoute {
        BLOCKED,
        UNDO,
        REDO
    }

    record UndoRedoStatus(String message, NamedTextColor color) {
    }
}
