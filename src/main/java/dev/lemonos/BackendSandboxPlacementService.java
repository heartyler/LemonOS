package dev.lemonos;

import net.kyori.adventure.text.format.NamedTextColor;

final class BackendSandboxPlacementService {
    PlacementPreview preview(Object preview) {
        return new PlacementPreview(preview, preview == null);
    }

    PlacementStatus missingPreview() {
        return new PlacementStatus("nothing changed.", NamedTextColor.DARK_GRAY);
    }

    PlacementStatus applyFailed() {
        return new PlacementStatus("try again.", NamedTextColor.DARK_GRAY);
    }

    PlacementStatus applied(boolean emptyChange) {
        return emptyChange ? new PlacementStatus("nothing changed.", NamedTextColor.DARK_GRAY) : new PlacementStatus("done.", NamedTextColor.GRAY);
    }

    PlacementResult result(boolean applied, boolean emptyChange) {
        return applied ? new PlacementResult(true, this.applied(emptyChange)) : new PlacementResult(false, this.applyFailed());
    }

    MissingPlacementLifecycle missingLifecycle() {
        return new MissingPlacementLifecycle(true, true);
    }

    PlacementCompletionLifecycle completionLifecycle(PlacementResult result) {
        return new PlacementCompletionLifecycle(result.applied(), true, true);
    }

    record PlacementPreview(Object preview, boolean missing) {
    }

    record PlacementResult(boolean applied, PlacementStatus status) {
    }

    record PlacementStatus(String message, NamedTextColor color) {
    }

    record MissingPlacementLifecycle(boolean closeInventory, boolean sendStatus) {
    }

    record PlacementCompletionLifecycle(boolean recordChange, boolean closeInventory, boolean sendStatus) {
    }
}
