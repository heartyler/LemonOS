/*
 * Backend-side LemonOS wake-place protocol sender.
 */
package dev.lemonos;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

final class BackendWakePlaceService {
    private final Plugin plugin;

    BackendWakePlaceService(Plugin plugin) {
        this.plugin = plugin;
    }

    void sendWakePlaceRequest(Player player, String serverProxyName) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(BackendAdminProtocol.WAKE_PLACE);
        byteArrayDataOutput.writeUTF(player.getUniqueId().toString());
        byteArrayDataOutput.writeUTF(serverProxyName);
        player.sendPluginMessage(this.plugin, BackendAdminProtocol.ADMIN_CHANNEL, byteArrayDataOutput.toByteArray());
    }
}
