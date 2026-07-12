package dev.lemonos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class BackendActivityTelemetryHarness {
    public static void main(String[] args) {
        Set<UUID> activeChains = new HashSet<>();
        UUID playerId = UUID.randomUUID();
        Capture capture = new Capture();
        BackendActivityTelemetryListener listener = new BackendActivityTelemetryListener(
                activeChains::contains, () -> 4242L, capture::record);

        require(listener.shouldRecordBlockBreak(playerId), "ordinary block break was suppressed");
        activeChains.add(playerId);
        require(!listener.shouldRecordBlockBreak(playerId), "chain-generated block break was recorded");
        activeChains.clear();

        listener.record(null, BackendActivityTelemetryListener.PICKUP_ITEMS, 0);
        require(capture.activity.equals("pickup-items"), "activity key changed");
        require(capture.amount == 1, "zero amount was not normalized");
        require(capture.timestamp == 4242L, "monotonic timestamp source changed");
        listener.record(null, BackendActivityTelemetryListener.CRAFT_ITEMS, 7);
        require(capture.amount == 7, "positive amount changed");
        System.out.println("Backend Activity telemetry harness OK");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    private static final class Capture {
        String activity = "";
        int amount;
        long timestamp;

        void record(org.bukkit.entity.Player player, String activity, int amount, long timestamp) {
            this.activity = activity;
            this.amount = amount;
            this.timestamp = timestamp;
        }
    }
}
