/*
 * Backend-side LemonOS travel finish orchestration.
 */
package dev.lemonos;

import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.entity.Player;

final class BackendTravelFinishService<T> {
    private final BackendTravelStateService<T> travelStateService;
    private final BackendTravelConnectService travelConnectService;
    private final BiConsumer<Player, T> identityTransferSaver;
    private final Function<T, String> proxyName;

    BackendTravelFinishService(BackendTravelStateService<T> travelStateService, BackendTravelConnectService travelConnectService, BiConsumer<Player, T> identityTransferSaver, Function<T, String> proxyName) {
        this.travelStateService = travelStateService;
        this.travelConnectService = travelConnectService;
        this.identityTransferSaver = identityTransferSaver;
        this.proxyName = proxyName;
    }

    void finish(Player player, T target, BackendOperationToken token) {
        if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) == null) return;
        if (!player.isOnline()) {
            return;
        }
        this.identityTransferSaver.accept(player, target);
        this.travelConnectService.connect(player, this.proxyName.apply(target));
    }
}
