/*
 * Decompiled-compatible LemonOS proxy access protocol messages.
 */
package dev.lemonos.proxy;

import dev.lemonos.common.AdminProtocol;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;

final class AccessMessageService {
    private static final int LEGACY_ACCESS_WRITE = 42;
    private static final int LEGACY_ACCESS_ACK = 43;
    private final ProxyServer server;
    private final Logger logger;
    private final ChannelIdentifier adminChannel;
    private final AccessRepository accessRepository;

    AccessMessageService(ProxyServer server, Logger logger, ChannelIdentifier adminChannel, AccessRepository accessRepository) {
        this.server = server;
        this.logger = logger;
        this.adminChannel = adminChannel;
        this.accessRepository = accessRepository;
    }

    boolean handleLegacyAccessWrite(ServerConnection serverConnection, byte[] data) {
        if (!this.isLegacyAccessWrite(data)) {
            return false;
        }
        long requestId = 0L;
        boolean saved = false;
        String name = "";
        boolean admin = false;
        try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));){
            dataInputStream.readUnsignedByte();
            requestId = dataInputStream.readLong();
            admin = dataInputStream.readUnsignedByte() == 1;
            name = AccessRepository.normalizeAccessName(dataInputStream.readUTF());
            if (!name.isBlank() && this.sourceHasAdmin(serverConnection)) {
                this.loadAccess();
                this.accessRepository.updateNameAccess(name, admin);
                saved = true;
            }
        }
        catch (IOException exception) {
            this.logger.debug("Ignoring malformed LemonOS legacy access message.", (Throwable)exception);
        }
        this.sendLegacyAccessAck(serverConnection, requestId, saved);
        if (saved) {
            this.logger.info("LemonOS access updated for {} as {}.", (Object)name, (Object)(admin ? "admin" : "default"));
        }
        return true;
    }

    boolean handleAccessMessage(ServerConnection serverConnection, String command, DataInputStream dataInputStream) throws IOException {
        if (AdminProtocol.REQUEST_ACCESS.equals(command)) {
            UUID uuid = UUID.fromString(dataInputStream.readUTF());
            if (!this.playerOnSourceServer(serverConnection, uuid)) {
                return true;
            }
            this.sendAccessState(serverConnection, uuid);
            return true;
        }
        if (AdminProtocol.REQUEST_KEYS.equals(command)) {
            if (!this.sourceHasAdmin(serverConnection)) {
                return true;
            }
            this.sendKeysState(serverConnection);
            return true;
        }
        if (AdminProtocol.SET_ACCESS.equals(command)) {
            UUID uuid = UUID.fromString(dataInputStream.readUTF());
            UUID targetUuid = UUID.fromString(dataInputStream.readUTF());
            String targetName = dataInputStream.readUTF();
            String role = dataInputStream.readUTF();
            if (!this.playerOnSourceServer(serverConnection, uuid) || !this.hasAdminAccess(uuid)) {
                this.sendAccessSaved(serverConnection, uuid, targetUuid, AdminProtocol.ROLE_DEFAULT);
                return true;
            }
            this.writeAccess(uuid, targetUuid, targetName, role);
            this.sendAccessSaved(serverConnection, uuid, targetUuid, role);
            return true;
        }
        return false;
    }

    private void sendAccessState(ServerConnection serverConnection, UUID uuid) throws IOException {
        this.loadAccess();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
            dataOutputStream.writeUTF(AdminProtocol.ACCESS_STATE);
            dataOutputStream.writeUTF(uuid.toString());
            dataOutputStream.writeUTF(this.hasAdminAccess(uuid) ? AdminProtocol.ROLE_ADMIN : AdminProtocol.ROLE_DEFAULT);
        }
        serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, byteArrayOutputStream.toByteArray());
    }

    private void sendKeysState(ServerConnection serverConnection) throws IOException {
        this.loadAccess();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
            dataOutputStream.writeUTF(AdminProtocol.KEYS_STATE);
            dataOutputStream.writeInt(this.server.getAllPlayers().size());
            for (Player player : this.server.getAllPlayers()) {
                dataOutputStream.writeUTF(player.getUniqueId().toString());
                dataOutputStream.writeUTF(player.getUsername());
                dataOutputStream.writeUTF(this.hasAdminAccess(player) ? AdminProtocol.ROLE_ADMIN : AdminProtocol.ROLE_DEFAULT);
            }
            Set<UUID> adminUuids = this.accessRepository.adminUuids();
            dataOutputStream.writeInt(adminUuids.size());
            for (UUID uuid : adminUuids) {
                dataOutputStream.writeUTF(uuid.toString());
                String name = this.accessRepository.adminName(uuid);
                dataOutputStream.writeUTF(name == null || name.isBlank() ? uuid.toString() : name);
            }
        }
        serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, byteArrayOutputStream.toByteArray());
    }

    private boolean hasAdminAccess(UUID uuid) {
        if (this.accessRepository.hasAdminAccess(uuid)) {
            return true;
        }
        return this.server.getPlayer(uuid).map(this::hasAdminAccess).orElse(false);
    }

    private boolean hasAdminAccess(Player player) {
        return this.accessRepository.hasAdminAccess(player.getUniqueId(), player.getUsername());
    }

    private boolean playerOnSourceServer(ServerConnection serverConnection, UUID uuid) {
        return this.server.getPlayer(uuid).flatMap(Player::getCurrentServer).map(currentServer -> currentServer.getServer().getServerInfo().getName().equalsIgnoreCase(serverConnection.getServerInfo().getName())).orElse(false);
    }

    private boolean sourceHasAdmin(ServerConnection serverConnection) {
        this.loadAccess();
        String serverName = serverConnection.getServerInfo().getName();
        return this.server.getAllPlayers().stream().filter(player -> player.getCurrentServer().map(currentServer -> currentServer.getServer().getServerInfo().getName().equalsIgnoreCase(serverName)).orElse(false)).anyMatch(this::hasAdminAccess);
    }

    private void writeAccess(UUID uuid, UUID targetUuid, String targetName, String role) throws IOException {
        this.loadAccess();
        this.accessRepository.updateAccess(targetUuid, targetName, role);
        this.logger.info("LemonOS access updated by {} for {} as {}.", new Object[]{uuid, targetUuid, role});
    }

    private boolean isLegacyAccessWrite(byte[] data) {
        return data != null && data.length > 0 && (data[0] & 0xFF) == LEGACY_ACCESS_WRITE;
    }

    private void sendLegacyAccessAck(ServerConnection serverConnection, long requestId, boolean saved) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
                dataOutputStream.writeByte(LEGACY_ACCESS_ACK);
                dataOutputStream.writeLong(requestId);
                dataOutputStream.writeByte(saved ? 1 : 0);
            }
            serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, byteArrayOutputStream.toByteArray());
        }
        catch (IOException exception) {
            this.logger.debug("Unable to send LemonOS legacy access ack.", (Throwable)exception);
        }
    }

    private void sendAccessSaved(ServerConnection serverConnection, UUID uuid, UUID targetUuid, String role) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
            dataOutputStream.writeUTF(AdminProtocol.ACCESS_SAVED);
            dataOutputStream.writeUTF(uuid.toString());
            dataOutputStream.writeUTF(targetUuid.toString());
            dataOutputStream.writeUTF(role);
        }
        serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, byteArrayOutputStream.toByteArray());
    }

    private void loadAccess() {
        this.accessRepository.load();
    }
}
