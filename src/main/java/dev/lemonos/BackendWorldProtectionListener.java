package dev.lemonos;

import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

final class BackendWorldProtectionListener implements Listener {
    private final BackendWorldPolicy policy;

    BackendWorldProtectionListener(BackendWorldPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onCreatureSpawn(CreatureSpawnEvent event) {
        if (this.blocksCreatureSpawns()) event.setCancelled(true);
    }

    @EventHandler
    void onBlockBurn(BlockBurnEvent event) {
        if (this.protectsEnvironment()) event.setCancelled(true);
    }

    @EventHandler
    void onBlockSpread(BlockSpreadEvent event) {
        if (this.blocksFireSpread(event.getSource().getType())) event.setCancelled(true);
    }

    @EventHandler
    void onBlockFromTo(BlockFromToEvent event) {
        if (this.blocksLavaFlow(event.getBlock().getType())) event.setCancelled(true);
    }

    @EventHandler
    void onBlockExplode(BlockExplodeEvent event) {
        if (!this.protectsEnvironment()) return;
        event.blockList().clear();
        event.setCancelled(true);
    }

    @EventHandler
    void onEntityExplode(EntityExplodeEvent event) {
        if (!this.protectsEnvironment()) return;
        event.blockList().clear();
        event.setCancelled(true);
    }

    boolean blocksCreatureSpawns() {
        return this.policy.blockCreatureSpawns();
    }

    boolean protectsEnvironment() {
        return this.policy.protectEnvironment();
    }

    boolean blocksFireSpread(Material source) {
        return this.protectsEnvironment() && source == Material.FIRE;
    }

    boolean blocksLavaFlow(Material source) {
        return this.protectsEnvironment() && source == Material.LAVA;
    }
}
