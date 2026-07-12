package dev.lemonos;

import java.util.List;

final class BackendIdentityCompletionService {
    CompletionPlan loginCompletion(boolean notify) {
        return new CompletionPlan(commonCleanup(), SurfaceAction.LOGIN_SURFACE, notify);
    }

    CompletionPlan trustedCompletion(boolean delaySurface) {
        return new CompletionPlan(commonCleanup(), delaySurface ? SurfaceAction.TRUSTED_SURFACE_DELAYED : SurfaceAction.TRUSTED_SURFACE_NOW, false);
    }

    private List<CleanupAction> commonCleanup() {
        return List.of(
                CleanupAction.CLEAR_RESET_REQUEST,
                CleanupAction.CLEAR_PENDING_INPUT,
                CleanupAction.CLEAR_PENDING_PASSCODE,
                CleanupAction.CLEAR_GUIDANCE,
                CleanupAction.CLEAR_PASSCODE_FEEDBACK,
                CleanupAction.UNLOCK_AUTH,
                CleanupAction.MARK_AUTHENTICATED,
                CleanupAction.SYNC_ACCESS,
                CleanupAction.CLEAR_RESET_INPUT_COUNT,
                CleanupAction.SAVE_SESSION);
    }

    record CompletionPlan(List<CleanupAction> cleanupActions, SurfaceAction surfaceAction, boolean notifyPlayer) {
    }

    enum CleanupAction {
        CLEAR_RESET_REQUEST,
        CLEAR_PENDING_INPUT,
        CLEAR_PENDING_PASSCODE,
        CLEAR_GUIDANCE,
        CLEAR_PASSCODE_FEEDBACK,
        UNLOCK_AUTH,
        MARK_AUTHENTICATED,
        SYNC_ACCESS,
        CLEAR_RESET_INPUT_COUNT,
        SAVE_SESSION;
    }

    enum SurfaceAction {
        LOGIN_SURFACE,
        TRUSTED_SURFACE_NOW,
        TRUSTED_SURFACE_DELAYED;
    }
}
