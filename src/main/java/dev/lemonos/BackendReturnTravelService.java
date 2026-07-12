/*
 * Backend-side LemonOS local return travel orchestration.
 */
package dev.lemonos;

import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

final class BackendReturnTravelService {
    private static final long RETURN_TRAVEL_DELAY_TICKS = 40L;
    private final Plugin plugin;
    private final BackendTravelStateService<?> travelStateService;
    private final Predicate<UUID> busy;
    private final BiPredicate<Location, Location> sameBlockLocation;

    BackendReturnTravelService(Plugin plugin, BackendTravelStateService<?> travelStateService, Predicate<UUID> busy, BiPredicate<Location, Location> sameBlockLocation) {
        this.plugin = plugin;
        this.travelStateService = travelStateService;
        this.busy = busy;
        this.sameBlockLocation = sameBlockLocation;
    }

    void returnHome(Player player, Supplier<Location> locationSupplier) {
        if (this.busy.test(player.getUniqueId()) || locationSupplier.get() == null) {
            return;
        }
        this.start(player, locationSupplier);
    }

    void returnSpawn(Player player, Supplier<Location> locationSupplier) {
        if (this.busy.test(player.getUniqueId())) {
            return;
        }
        this.start(player, locationSupplier);
    }

    private void start(Player player, Supplier<Location> locationSupplier) {
        player.closeInventory();
        this.travelStateService.begin(player.getUniqueId(), null, false, "on the way", token ->
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.finish(player, locationSupplier, token), RETURN_TRAVEL_DELAY_TICKS));
    }

    private void finish(Player player, Supplier<Location> locationSupplier, BackendOperationToken token) {
        if (!this.travelStateService.isCurrent(player.getUniqueId(), token)) return;
        if (!player.isOnline()) {
            this.travelStateService.endIfCurrent(player.getUniqueId(), token);
            return;
        }
        Location location = locationSupplier.get();
        if (location == null) {
            if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) != null) this.sendNothingChanged(player);
            return;
        }
        if (!player.teleport(location)) {
            if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) != null) this.sendNothingChanged(player);
            return;
        }
        BukkitTask verificationTask = Bukkit.getScheduler().runTask(this.plugin, () -> {
            if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) == null) return;
            if (!player.isOnline()) {
                return;
            }
            if (!this.sameBlockLocation.test(player.getLocation(), location)) {
                this.sendNothingChanged(player);
                return;
            }
            player.sendMessage((Component)Component.text((String)"you're here.", (TextColor)NamedTextColor.GRAY));
        });
        this.travelStateService.replaceTaskIfCurrent(player.getUniqueId(), token, verificationTask);
    }

    private void sendNothingChanged(Player player) {
        player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
    }
}
