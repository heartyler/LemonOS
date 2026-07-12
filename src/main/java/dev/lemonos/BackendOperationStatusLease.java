package dev.lemonos;

import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.text.Component;

/** Clear-once ownership lease for an operation's persistent Action Bar status. */
final class BackendOperationStatusLease implements AutoCloseable {
    private final BackendActionBarCoordinator coordinator;
    private final UUID participant;
    private final BackendActionBarCoordinator.Owner owner;
    private boolean open = true;

    BackendOperationStatusLease(BackendActionBarCoordinator coordinator, UUID participant, BackendActionBarCoordinator.Owner owner) {
        this.coordinator = Objects.requireNonNull(coordinator, "coordinator");
        this.participant = Objects.requireNonNull(participant, "participant");
        this.owner = Objects.requireNonNull(owner, "owner");
    }

    synchronized boolean publish(Component component) {
        if (!this.open || component == null) return false;
        this.coordinator.publish(this.participant, this.owner, component);
        return true;
    }

    synchronized boolean open() {
        return this.open;
    }

    @Override
    public synchronized void close() {
        if (!this.open) return;
        this.open = false;
        this.coordinator.clear(this.participant, this.owner);
    }
}
