package dev.lemonos;

import java.util.Objects;
import java.util.UUID;

/** Immutable correlation identity for one transient LemonOS operation. */
record BackendOperationToken(UUID operationId, long generation, long createdAtNanos) {
    BackendOperationToken {
        operationId = Objects.requireNonNull(operationId, "operationId");
        if (generation < 1L) throw new IllegalArgumentException("generation must be positive");
    }

    static BackendOperationToken create(long generation) {
        return new BackendOperationToken(UUID.randomUUID(), generation, System.nanoTime());
    }

    boolean sameOperation(BackendOperationToken other) {
        return other != null && this.operationId.equals(other.operationId) && this.generation == other.generation;
    }
}
