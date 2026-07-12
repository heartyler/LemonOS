/*
 * Backend-side LemonOS wake-travel orchestration.
 */
package dev.lemonos;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

final class BackendWakeTravelService<T> {
    private static final long WAKE_TIMEOUT_MILLIS = 120000L;
    private static final long INITIAL_DELAY_TICKS = 1L;
    private static final long POLL_PERIOD_TICKS = 20L;
    private final Plugin plugin;
    private final BackendTravelStateService<T> travelStateService;
    private final BiConsumer<Player, T> wakeRequest;
    private final BiConsumer<T, String> runtimeStatus;
    private final Predicate<T> canConnect;
    private final TravelFinisher<T> finishTravel;

    BackendWakeTravelService(Plugin plugin, BackendTravelStateService<T> travelStateService, BiConsumer<Player, T> wakeRequest, BiConsumer<T, String> runtimeStatus, Predicate<T> canConnect, TravelFinisher<T> finishTravel) {
        this.plugin = plugin;
        this.travelStateService = travelStateService;
        this.wakeRequest = wakeRequest;
        this.runtimeStatus = runtimeStatus;
        this.canConnect = canConnect;
        this.finishTravel = finishTravel;
    }

    void start(Player player, T target, String wakingStatus) {
        player.closeInventory();
        this.wakeRequest.accept(player, target);
        this.runtimeStatus.accept(target, wakingStatus);
        long timeoutAt = System.nanoTime() / 1_000_000L + WAKE_TIMEOUT_MILLIS;
        this.travelStateService.begin(player.getUniqueId(), target, true, "waiting", token ->
                Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (!player.isOnline()) {
                this.travelStateService.endIfCurrent(player.getUniqueId(), token);
                return;
            }
            if (this.canConnect.test(target)) {
                this.finishTravel.finish(player, target, token);
                return;
            }
            if (System.nanoTime() / 1_000_000L > timeoutAt) {
                if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) != null) {
                    player.sendMessage((Component)Component.text((String)"no signal.", (TextColor)NamedTextColor.DARK_GRAY));
                }
                return;
            }
            this.travelStateService.showIfCurrent(player.getUniqueId(), token, "waiting");
        }, INITIAL_DELAY_TICKS, POLL_PERIOD_TICKS));
    }

    interface TravelFinisher<T> {
        void finish(Player player, T target, BackendOperationToken token);
    }
}
