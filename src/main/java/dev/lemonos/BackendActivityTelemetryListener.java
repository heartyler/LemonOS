package dev.lemonos;

import java.util.Objects;
import java.util.UUID;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

final class BackendActivityTelemetryListener implements Listener {
    static final String BREAK_BLOCKS = "break-blocks";
    static final String PLACE_BLOCKS = "place-blocks";
    static final String PICKUP_ITEMS = "pickup-items";
    static final String CRAFT_ITEMS = "craft-items";

    private final Predicate<UUID> chainBreakActive;
    private final LongSupplier clock;
    private final ActivityRecorder recorder;

    BackendActivityTelemetryListener(
            Predicate<UUID> chainBreakActive,
            LongSupplier clock,
            ActivityRecorder recorder) {
        this.chainBreakActive = Objects.requireNonNull(chainBreakActive, "chainBreakActive");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.recorder = Objects.requireNonNull(recorder, "recorder");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (this.shouldRecordBlockBreak(player.getUniqueId())) this.record(player, BREAK_BLOCKS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onBlockPlace(BlockPlaceEvent event) {
        this.record(event.getPlayer(), PLACE_BLOCKS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            this.record(player, PICKUP_ITEMS, event.getItem().getItemStack().getAmount());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onCraft(CraftItemEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (entity instanceof Player player) {
            ItemStack result = event.getRecipe() == null ? null : event.getRecipe().getResult();
            this.record(player, CRAFT_ITEMS, result == null ? 1 : result.getAmount());
        }
    }

    boolean shouldRecordBlockBreak(UUID playerId) {
        return !this.chainBreakActive.test(playerId);
    }

    void record(Player player, String activity, int amount) {
        this.recorder.record(player, activity, Math.max(1, amount), this.clock.getAsLong());
    }

    @FunctionalInterface
    interface ActivityRecorder {
        void record(Player player, String activity, int amount, long timestamp);
    }
}
