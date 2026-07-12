package dev.lemonos;

import net.kyori.adventure.text.format.NamedTextColor;

final class BackendSandboxCloneClearPreviewStatusService {
    PreviewStatus cloneStatus(boolean invalidSelection, boolean differentWorld, boolean outsideVerticalRange) {
        if (invalidSelection || differentWorld || outsideVerticalRange) {
            return new PreviewStatus(false, true, "too large.", NamedTextColor.DARK_GRAY);
        }
        return new PreviewStatus(true, false, "", NamedTextColor.GRAY);
    }

    PreviewStatus clearStatus() {
        return new PreviewStatus(true, false, "", NamedTextColor.GRAY);
    }

    record PreviewStatus(boolean ready, boolean sendStatus, String message, NamedTextColor color) {
    }
}
