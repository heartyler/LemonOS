package dev.lemonos;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;

final class BackendActionBarCoordinator {
    private static final long KEEPALIVE_NANOS = 1_000_000_000L;
    enum Owner {
        MUSIC(100),
        ATMOSPHERE(200),
        PENDING(300),
        CHAIN(400),
        CARE_WORLD(500),
        SANDBOX(600),
        SANDBOX_NOTIFICATION(650),
        ADMIN_SEND(680),
        TRAVEL(700);

        private final int priority;

        Owner(int priority) {
            this.priority = priority;
        }
    }

    private final Map<UUID, EnumMap<Owner, Entry>> entries = new HashMap<UUID, EnumMap<Owner, Entry>>();
    private final Map<UUID, Component> lastFrames = new HashMap<UUID, Component>();
    private final Map<UUID, Owner> lastOwners = new HashMap<UUID, Owner>();
    private final Map<UUID, Long> lastSentNanos = new HashMap<UUID, Long>();

    synchronized void publish(UUID uuid, Owner owner, Component component) {
        if (uuid == null || owner == null || component == null) return;
        this.ownerEntries(uuid).put(owner, new Entry(component, Long.MAX_VALUE));
    }

    synchronized void notify(UUID uuid, Owner owner, Component component, long nowNanos, long durationMillis) {
        if (uuid == null || owner == null || component == null) return;
        long safeMillis = Math.max(1L, Math.min(durationMillis, Long.MAX_VALUE / 1_000_000L));
        long durationNanos = safeMillis * 1_000_000L;
        long untilNanos = nowNanos > Long.MAX_VALUE - durationNanos ? Long.MAX_VALUE : nowNanos + durationNanos;
        this.ownerEntries(uuid).put(owner, new Entry(component, untilNanos));
    }

    synchronized void clear(UUID uuid, Owner owner) {
        if (uuid == null || owner == null) return;
        EnumMap<Owner, Entry> ownerEntries = this.entries.get(uuid);
        if (ownerEntries == null) return;
        ownerEntries.remove(owner);
        if (ownerEntries.isEmpty()) this.entries.remove(uuid);
    }

    synchronized Frame frame(UUID uuid, long nowNanos) {
        if (uuid == null) return null;
        EnumMap<Owner, Entry> ownerEntries = this.entries.get(uuid);
        if (ownerEntries != null) {
            ownerEntries.entrySet().removeIf(entry -> entry.getValue().untilNanos <= nowNanos);
            if (ownerEntries.isEmpty()) this.entries.remove(uuid);
        }
        Entry winner = null;
        Owner winnerOwner = null;
        if (ownerEntries != null) {
            for (Map.Entry<Owner, Entry> entry : ownerEntries.entrySet()) {
                if (winnerOwner == null || entry.getKey().priority > winnerOwner.priority) {
                    winnerOwner = entry.getKey();
                    winner = entry.getValue();
                }
            }
        }
        if (winner != null) {
            Component lastFrame = this.lastFrames.get(uuid);
            Owner lastOwner = this.lastOwners.get(uuid);
            long lastSent = this.lastSentNanos.getOrDefault(uuid, 0L);
            boolean changed = winnerOwner != lastOwner || !winner.component.equals(lastFrame);
            if (!changed && lastSent != 0L && nowNanos - lastSent < KEEPALIVE_NANOS) return null;
            this.lastFrames.put(uuid, winner.component);
            this.lastOwners.put(uuid, winnerOwner);
            this.lastSentNanos.put(uuid, nowNanos);
            return new Frame(winnerOwner, winner.component, false);
        }
        if (this.lastFrames.remove(uuid) != null) {
            this.lastOwners.remove(uuid);
            this.lastSentNanos.remove(uuid);
            return new Frame(null, Component.empty(), true);
        }
        return null;
    }

    synchronized boolean owns(UUID uuid, Owner owner) {
        EnumMap<Owner, Entry> ownerEntries = uuid == null ? null : this.entries.get(uuid);
        return ownerEntries != null && ownerEntries.containsKey(owner);
    }

    synchronized Set<UUID> ids() {
        java.util.HashSet<UUID> ids = new java.util.HashSet<UUID>(this.entries.keySet());
        ids.addAll(this.lastFrames.keySet());
        return Set.copyOf(ids);
    }

    synchronized void remove(UUID uuid) {
        if (uuid == null) return;
        this.entries.remove(uuid);
        this.lastFrames.remove(uuid);
        this.lastOwners.remove(uuid);
        this.lastSentNanos.remove(uuid);
    }

    synchronized void clear() {
        this.entries.clear();
        this.lastFrames.clear();
        this.lastOwners.clear();
        this.lastSentNanos.clear();
    }

    private EnumMap<Owner, Entry> ownerEntries(UUID uuid) {
        return this.entries.computeIfAbsent(uuid, ignored -> new EnumMap<Owner, Entry>(Owner.class));
    }

    record Frame(Owner owner, Component component, boolean clear) {
    }

    private record Entry(Component component, long untilNanos) {
    }
}
