/*
 * Decompiled-compatible LemonOS place connect and result boundary.
 */
package dev.lemonos.proxy;

import dev.lemonos.common.AdminProtocol;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;

final class PlaceConnectService {
    private final ProxyServer server;
    private final Logger logger;
    private final ChannelIdentifier adminChannel;
    private final PlaceRuntimeProbe runtimeProbe;
    private final PlaceStatusRepository statusRepository;
    private final PlaceWakeService.Scheduler scheduler;
    private final WakeStarter wakeStarter;

    PlaceConnectService(ProxyServer server, Logger logger, ChannelIdentifier adminChannel, PlaceRuntimeProbe runtimeProbe, PlaceStatusRepository statusRepository, PlaceWakeService.Scheduler scheduler, WakeStarter wakeStarter) {
        this.server = server;
        this.logger = logger;
        this.adminChannel = adminChannel;
        this.runtimeProbe = runtimeProbe;
        this.statusRepository = statusRepository;
        this.scheduler = scheduler;
        this.wakeStarter = wakeStarter;
    }

    void connectPlace(ServerConnection serverConnection, UUID uuid, String requestedPlace) throws IOException {
        Optional<Player> player = this.server.getPlayer(uuid);
        String place = normalizePlaceName(requestedPlace);
        Optional<RegisteredServer> registeredServer = this.server.getServer(place);
        if (player.isEmpty() || registeredServer.isEmpty() || !this.playerOnSourceServer(serverConnection, uuid)) {
            this.sendPlaceResult(serverConnection, uuid, place, AdminProtocol.PLACE_UNAVAILABLE);
            return;
        }
        int port = this.runtimeProbe.port(place);
        if (canProxyWake(place) && port > 0 && !this.runtimeProbe.canConnect(place)) {
            String status = this.statusRepository.status(place);
            if (PlaceStatusRepository.isWakeStatus(status) || PlaceStatusRepository.isReadyStatus(status)) {
                this.wakeStarter.wakeAndConnect(serverConnection, player.get(), registeredServer.get(), uuid, place);
                return;
            }
        }
        this.connectPlayerToPlace(serverConnection, player.get(), registeredServer.get(), uuid, place);
    }

    void connectPlayerToPlace(ServerConnection serverConnection, Player player, RegisteredServer registeredServer, UUID uuid, String place) {
        player.createConnectionRequest(registeredServer).connect().thenAccept(result -> {
            this.scheduler.schedule(() -> {
                try {
                    this.sendPlaceResult(serverConnection, uuid, place, result.isSuccessful() ? AdminProtocol.PLACE_CONNECTED : AdminProtocol.PLACE_UNAVAILABLE);
                }
                catch (IOException exception) {
                    this.logger.debug("Unable to send LemonOS place result.", (Throwable)exception);
                }
            });
        });
    }

    void sendPlaceResult(ServerConnection serverConnection, UUID uuid, String place, String result) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
            dataOutputStream.writeUTF(result);
            dataOutputStream.writeUTF(uuid.toString());
            dataOutputStream.writeUTF(place);
        }
        serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, byteArrayOutputStream.toByteArray());
    }

    private boolean playerOnSourceServer(ServerConnection serverConnection, UUID uuid) {
        return this.server.getPlayer(uuid).flatMap(Player::getCurrentServer).map(currentServer -> currentServer.getServer().getServerInfo().getName().equalsIgnoreCase(serverConnection.getServerInfo().getName())).orElse(false);
    }

    private static String normalizePlaceName(String string) {
        return string == null ? "" : string.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean canProxyWake(String string) {
        return "survival".equals(string) || "creative".equals(string);
    }

    interface WakeStarter {
        void wakeAndConnect(ServerConnection serverConnection, Player player, RegisteredServer registeredServer, UUID uuid, String place);
    }
}
