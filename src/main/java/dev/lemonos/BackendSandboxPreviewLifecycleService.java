package dev.lemonos;

import java.util.List;

final class BackendSandboxPreviewLifecycleService {
    List<PreviewKind> clearOrder() {
        return List.of(PreviewKind.CLONE, PreviewKind.CLEAR, PreviewKind.ROTATE, PreviewKind.FLIP);
    }

    List<PreviewKind> recoveryOrder() {
        return this.clearOrder();
    }

    PreviewKind transformKind(boolean rotate) {
        return rotate ? PreviewKind.ROTATE : PreviewKind.FLIP;
    }

    ReadyPreviewLifecycle readyPreviewLifecycle() {
        return new ReadyPreviewLifecycle(true, true, true);
    }

    record ReadyPreviewLifecycle(boolean removeDrawingState, boolean showBox, boolean sendReadyStatus) {
    }

    enum PreviewKind {
        CLONE,
        CLEAR,
        ROTATE,
        FLIP
    }
}
