package dev.lemonos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;

final class BackendSandboxStatusService {
    private final Predicate<UUID> statePredicate;
    private final Map<UUID, Component> statuses = new HashMap<UUID, Component>();

    BackendSandboxStatusService(Predicate<UUID> statePredicate) {
        this.statePredicate = statePredicate;
    }

    boolean hasState(UUID uuid) {
        return uuid != null && this.statePredicate.test(uuid);
    }

    boolean contains(UUID uuid) {
        return uuid != null && this.statuses.containsKey(uuid);
    }

    void set(UUID uuid, Component component) {
        if (uuid != null && component != null) this.statuses.put(uuid, component);
    }

    void remove(UUID uuid) {
        if (uuid != null) this.statuses.remove(uuid);
    }

    void clear() {
        this.statuses.clear();
    }

    List<UUID> statusIds() {
        return List.copyOf(this.statuses.keySet());
    }

    List<StatusEntry> statusEntries() {
        ArrayList<StatusEntry> entries = new ArrayList<StatusEntry>();
        for (UUID uuid : List.copyOf(this.statuses.keySet())) {
            Component status = this.statuses.get(uuid);
            if (status != null && this.hasState(uuid)) {
                entries.add(new StatusEntry(uuid, status));
                continue;
            }
            this.statuses.remove(uuid);
            if (status != null) entries.add(new StatusEntry(uuid, Component.empty()));
        }
        return entries;
    }

    record StatusEntry(UUID uuid, Component component) {
    }
}
