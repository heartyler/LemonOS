/*
 * Backend-side LemonOS normal travel start orchestration.
 */
package dev.lemonos;

import java.util.Objects;
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

final class BackendTravelStartService<T> {
    private static final long NORMAL_TRAVEL_DELAY_TICKS = 40L;
    private final Plugin plugin;
    private final BackendTravelStateService<T> travelStateService;
    private final Predicate<UUID> busy;
    private final Predicate<T> available;
    private final Predicate<T> wakeable;
    private final BiConsumer<Player, T> wakeTravel;
    private final TravelFinisher<T> finishTravel;

    BackendTravelStartService(Plugin plugin, BackendTravelStateService<T> travelStateService, Predicate<UUID> busy, Predicate<T> available, Predicate<T> wakeable, BiConsumer<Player, T> wakeTravel, TravelFinisher<T> finishTravel) {
        this.plugin = plugin;
        this.travelStateService = travelStateService;
        this.busy = busy;
        this.available = available;
        this.wakeable = wakeable;
        this.wakeTravel = wakeTravel;
        this.finishTravel = finishTravel;
    }

    void start(Player player, T target, T currentTarget) {
        BackendTravelStateService.TravelState<T> travelState = this.travelStateService.get(player.getUniqueId());
        if (travelState != null && travelState.wake() && Objects.equals(travelState.target(), target)) {
            this.travelStateService.showIfCurrent(player.getUniqueId(), travelState.token(), "waiting");
            return;
        }
        if (Objects.equals(target, currentTarget) || this.busy.test(player.getUniqueId())) {
            return;
        }
        if (!this.available.test(target)) {
            if (this.wakeable.test(target)) {
                this.wakeTravel.accept(player, target);
            }
            return;
        }
        player.closeInventory();
        this.travelStateService.begin(player.getUniqueId(), target, false, "on the way", token ->
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.finishTravel.finish(player, target, token), NORMAL_TRAVEL_DELAY_TICKS));
    }

    interface TravelFinisher<T> {
        void finish(Player player, T target, BackendOperationToken token);
    }
}
