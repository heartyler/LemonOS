/*
 * Backend-side LemonOS player-to-player local travel orchestration.
 */
package dev.lemonos;

import java.util.function.BiPredicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

final class BackendLocalTravelService {
    private static final long LOCAL_TRAVEL_DELAY_TICKS = 40L;
    private final Plugin plugin;
    private final BackendTravelStateService<?> travelStateService;
    private final BiPredicate<Location, Location> sameBlockLocation;

    BackendLocalTravelService(Plugin plugin, BackendTravelStateService<?> travelStateService, BiPredicate<Location, Location> sameBlockLocation) {
        this.plugin = plugin;
        this.travelStateService = travelStateService;
        this.sameBlockLocation = sameBlockLocation;
    }

    void start(Player player, Player target) {
        if (this.travelStateService.contains(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        this.travelStateService.begin(player.getUniqueId(), null, false, "on the way", token ->
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.finish(player, target, token), LOCAL_TRAVEL_DELAY_TICKS));
    }

    private void finish(Player player, Player target, BackendOperationToken token) {
        if (!this.travelStateService.isCurrent(player.getUniqueId(), token)) return;
        if (!player.isOnline() || !target.isOnline()) {
            if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) != null && player.isOnline()) {
                this.sendNothingChanged(player);
            }
            return;
        }
        Location location = target.getLocation();
        if (!player.teleport(location)) {
            if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) != null) this.sendTryAgain(player);
            return;
        }
        BukkitTask verificationTask = Bukkit.getScheduler().runTask(this.plugin, () -> {
            if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) == null) return;
            if (!player.isOnline() || !this.sameBlockLocation.test(player.getLocation(), location)) {
                if (player.isOnline()) {
                    this.sendTryAgain(player);
                }
                return;
            }
            player.sendMessage((Component)Component.text((String)"you're here.", (TextColor)NamedTextColor.GRAY));
            if (target.isOnline()) {
                target.sendMessage(((TextComponent)Component.text((String)player.getName(), (TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.space())).append((Component)Component.text((String)"is here.", (TextColor)NamedTextColor.GRAY)));
            }
        });
        this.travelStateService.replaceTaskIfCurrent(player.getUniqueId(), token, verificationTask);
    }

    private void sendNothingChanged(Player player) {
        player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
    }

    private void sendTryAgain(Player player) {
        player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
    }
}
