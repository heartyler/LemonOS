/*
 * Decompiled-compatible LemonOS people state and movement actions.
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
import java.util.Optional;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;

final class PeopleService {
    private final ProxyServer server;
    private final Logger logger;
    private final ChannelIdentifier adminChannel;
    private final PlaceWakeService.Scheduler scheduler;

    PeopleService(ProxyServer server, Logger logger, ChannelIdentifier adminChannel, PlaceWakeService.Scheduler scheduler) {
        this.server = server;
        this.logger = logger;
        this.adminChannel = adminChannel;
        this.scheduler = scheduler;
    }

    void sendPeopleState(ServerConnection serverConnection) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
            dataOutputStream.writeUTF(AdminProtocol.PEOPLE_STATE);
            dataOutputStream.writeInt(this.server.getAllPlayers().size());
            for (Player player : this.server.getAllPlayers()) {
                dataOutputStream.writeUTF(player.getUniqueId().toString());
                dataOutputStream.writeUTF(player.getUsername());
                dataOutputStream.writeUTF(player.getCurrentServer().map(currentServer -> currentServer.getServerInfo().getName()).orElse(""));
            }
        }
        serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, byteArrayOutputStream.toByteArray());
    }

    void meetPlayer(ServerConnection serverConnection, UUID uuid, UUID targetUuid) throws IOException {
        Optional<Player> player = this.server.getPlayer(uuid);
        Optional<Player> targetPlayer = this.server.getPlayer(targetUuid);
        if (player.isEmpty() || targetPlayer.isEmpty() || !this.playerOnSourceServer(serverConnection, uuid) || targetPlayer.get().getCurrentServer().isEmpty()) {
            this.sendPeopleResult(serverConnection, uuid, AdminProtocol.PEOPLE_ACTION_UNAVAILABLE);
            return;
        }
        RegisteredServer registeredServer = targetPlayer.get().getCurrentServer().get().getServer();
        player.get().createConnectionRequest(registeredServer).connect().thenAccept(result -> this.scheduler.schedule(() -> {
            try {
                this.sendPeopleResult(serverConnection, uuid, result.isSuccessful() ? AdminProtocol.PEOPLE_ACTION_DONE : AdminProtocol.PEOPLE_ACTION_UNAVAILABLE);
            }
            catch (IOException exception) {
                this.logger.debug("Unable to send LemonOS people result.", (Throwable)exception);
            }
        }));
    }

    void bringPlayer(ServerConnection serverConnection, UUID uuid, UUID targetUuid) throws IOException {
        Optional<Player> player = this.server.getPlayer(uuid);
        Optional<Player> targetPlayer = this.server.getPlayer(targetUuid);
        if (player.isEmpty() || targetPlayer.isEmpty() || !this.playerOnSourceServer(serverConnection, uuid) || player.get().getCurrentServer().isEmpty()) {
            this.sendPeopleResult(serverConnection, uuid, AdminProtocol.PEOPLE_ACTION_UNAVAILABLE);
            return;
        }
        RegisteredServer registeredServer = player.get().getCurrentServer().get().getServer();
        targetPlayer.get().createConnectionRequest(registeredServer).connect().thenAccept(result -> this.scheduler.schedule(() -> {
            try {
                this.sendPeopleResult(serverConnection, uuid, result.isSuccessful() ? AdminProtocol.PEOPLE_ACTION_DONE : AdminProtocol.PEOPLE_ACTION_UNAVAILABLE);
            }
            catch (IOException exception) {
                this.logger.debug("Unable to send LemonOS people result.", (Throwable)exception);
            }
        }));
    }

    void sendPlayerPlace(ServerConnection serverConnection, UUID actorId, UUID targetId, String place, UUID operationId) throws IOException {
        String normalizedPlace = place == null ? "" : place.trim().toLowerCase(Locale.ROOT);
        Optional<Player> actor = this.server.getPlayer(actorId);
        Optional<Player> target = this.server.getPlayer(targetId);
        Optional<RegisteredServer> destination = this.server.getServer(normalizedPlace);
        if (!Set.of("lobby", "survival", "creative").contains(normalizedPlace)
                || actor.isEmpty() || target.isEmpty() || destination.isEmpty()
                || !this.playerOnSourceServer(serverConnection, actorId)
                || !this.playerOnSourceServer(serverConnection, targetId)) {
            this.sendPlayerPlaceResult(serverConnection, actorId, targetId, normalizedPlace, operationId, AdminProtocol.SEND_RESULT_TRY_AGAIN);
            return;
        }
        target.get().createConnectionRequest(destination.get()).connect().thenAccept(result -> this.scheduler.schedule(() -> {
            try {
                this.sendPlayerPlaceResult(serverConnection, actorId, targetId, normalizedPlace, operationId,
                        result.isSuccessful() ? AdminProtocol.SEND_RESULT_SENT : AdminProtocol.SEND_RESULT_TRY_AGAIN);
            }
            catch (IOException exception) {
                this.logger.debug("Unable to send LemonOS admin Send result.", (Throwable)exception);
            }
        }));
    }

    private void sendPlayerPlaceResult(ServerConnection serverConnection, UUID actorId, UUID targetId, String place, UUID operationId, String result) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (DataOutputStream data = new DataOutputStream(output)) {
            data.writeUTF(AdminProtocol.SEND_PLAYER_RESULT);
            data.writeUTF(actorId.toString());
            data.writeUTF(targetId.toString());
            data.writeUTF(place);
            data.writeUTF(operationId.toString());
            data.writeUTF(result);
        }
        serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, output.toByteArray());
    }

    private void sendPeopleResult(ServerConnection serverConnection, UUID uuid, String result) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
            dataOutputStream.writeUTF(result);
            dataOutputStream.writeUTF(uuid.toString());
        }
        serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, byteArrayOutputStream.toByteArray());
    }

    private boolean playerOnSourceServer(ServerConnection serverConnection, UUID uuid) {
        return this.server.getPlayer(uuid).flatMap(Player::getCurrentServer).map(currentServer -> currentServer.getServer().getServerInfo().getName().equalsIgnoreCase(serverConnection.getServerInfo().getName())).orElse(false);
    }
}
