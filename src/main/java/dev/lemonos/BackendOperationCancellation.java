package dev.lemonos;

import java.io.IOException;

/** Cooperative cancellation with an atomic, cancellation-safe commit section. */
final class BackendOperationCancellation {
    private boolean cancelled;
    private boolean committed;

    synchronized boolean cancel() {
        if (this.committed) return false;
        this.cancelled = true;
        return true;
    }

    synchronized boolean cancelled() {
        return this.cancelled;
    }

    void check() throws IOException {
        if (this.cancelled()) throw new IOException("operation cancelled");
    }

    synchronized void commit(IoAction action) throws IOException {
        if (this.cancelled) throw new IOException("operation cancelled");
        action.run();
        this.committed = true;
    }

    interface IoAction {
        void run() throws IOException;
    }
}
