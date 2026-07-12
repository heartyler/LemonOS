package dev.lemonos;

final class BackendSandboxActiveDrawingLifecycleService {
    DrawingLifecycle cancel(boolean hadDrawing) {
        return new DrawingLifecycle(hadDrawing, hadDrawing, hadDrawing);
    }

    DrawingLifecycle finish(boolean hadDrawing) {
        return new DrawingLifecycle(hadDrawing, true, true);
    }

    DrawingLifecycle clearSession(boolean hadDrawing) {
        return new DrawingLifecycle(hadDrawing, true, true);
    }

    IdleExpiry expireIdle(boolean hadDrawing, boolean hadPreviews, boolean playerOnline) {
        boolean hadState = hadDrawing || hadPreviews;
        return new IdleExpiry(hadState, hadState && playerOnline, hadState && playerOnline);
    }

    record DrawingLifecycle(boolean hadDrawing, boolean cancelIdleTimeout, boolean clearStatus) {
    }

    record IdleExpiry(boolean hadState, boolean clearStatus, boolean sendNothingChanged) {
    }
}
