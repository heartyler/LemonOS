/*
 * Decompiled-compatible LemonOS proxy skin actions.
 */
package dev.lemonos.proxy;

import dev.lemonos.common.AdminProtocol;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;

final class SkinService {
    private final ProxyServer server;
    private final Logger logger;
    private final ChannelIdentifier adminChannel;
    private final PlaceWakeService.Scheduler scheduler;

    SkinService(ProxyServer server, Logger logger, ChannelIdentifier adminChannel, PlaceWakeService.Scheduler scheduler) {
        this.server = server;
        this.logger = logger;
        this.adminChannel = adminChannel;
        this.scheduler = scheduler;
    }

    void applySkin(ServerConnection serverConnection, UUID uuid, String skinName) {
        Optional<Player> player = this.server.getPlayer(uuid);
        if (player.isEmpty() || !this.playerOnSourceServer(serverConnection, uuid) || !validSkinName(skinName)) {
            this.sendSkinResult(serverConnection, uuid, skinName, AdminProtocol.RESULT_TRY_AGAIN);
            return;
        }
        CompletableFuture.supplyAsync(() -> this.applySkinViaSkinsRestorer(player.get(), skinName)).thenAccept(result -> this.scheduler.schedule(() -> this.sendSkinResult(serverConnection, uuid, skinName, result)));
    }

    private String applySkinViaSkinsRestorer(Player player, String skinName) {
        try {
            Object skinsRestorer = Class.forName("net.skinsrestorer.api.SkinsRestorerProvider").getMethod("get", new Class[0]).invoke(null, new Object[0]);
            Object skinStorage = skinsRestorer.getClass().getMethod("getSkinStorage", new Class[0]).invoke(skinsRestorer, new Object[0]);
            Object skinData = skinStorage.getClass().getMethod("findOrCreateSkinData", String.class).invoke(skinStorage, skinName);
            if (!(skinData instanceof Optional)) {
                return AdminProtocol.STATUS_UNAVAILABLE;
            }
            Optional optional = (Optional)skinData;
            if (optional.isEmpty()) {
                return AdminProtocol.RESULT_TRY_AGAIN;
            }
            Object property = optional.get().getClass().getMethod("getProperty", new Class[0]).invoke(optional.get(), new Object[0]);
            Object skinApplier = skinsRestorer.getClass().getMethod("getSkinApplier", Class.class).invoke(skinsRestorer, Player.class);
            this.invokeSkinApplier(skinApplier, player, property);
            return AdminProtocol.RESULT_SAVED;
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            this.logger.debug("Unable to apply LemonOS proxy skin.", (Throwable)exception);
            return AdminProtocol.STATUS_UNAVAILABLE;
        }
    }

    private void invokeSkinApplier(Object skinApplier, Player player, Object property) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName("net.skinsrestorer.api.property.SkinProperty");
        try {
            skinApplier.getClass().getMethod("applySkin", Object.class, clazz).invoke(skinApplier, player, property);
        }
        catch (NoSuchMethodException exception) {
            skinApplier.getClass().getMethod("applySkin", Player.class, clazz).invoke(skinApplier, player, property);
        }
    }

    private void sendSkinResult(ServerConnection serverConnection, UUID uuid, String skinName, String result) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
                dataOutputStream.writeUTF(AdminProtocol.SKIN_RESULT);
                dataOutputStream.writeUTF(uuid.toString());
                dataOutputStream.writeUTF(skinName == null ? "" : skinName);
                dataOutputStream.writeUTF(result == null ? AdminProtocol.STATUS_UNAVAILABLE : result);
            }
            serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, byteArrayOutputStream.toByteArray());
        }
        catch (IOException exception) {
            this.logger.debug("Unable to send LemonOS skin result.", (Throwable)exception);
        }
    }

    private boolean playerOnSourceServer(ServerConnection serverConnection, UUID uuid) {
        return this.server.getPlayer(uuid).flatMap(Player::getCurrentServer).map(currentServer -> currentServer.getServer().getServerInfo().getName().equalsIgnoreCase(serverConnection.getServerInfo().getName())).orElse(false);
    }

    private static boolean validSkinName(String string) {
        return string != null && string.length() >= 3 && string.length() <= 16 && string.matches("[A-Za-z0-9_]+");
    }
}
