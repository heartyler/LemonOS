/*
 * Backend-side BungeeCord travel connect sender.
 */
package dev.lemonos;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

final class BackendTravelConnectService {
    private static final String CONNECT_COMMAND = "Connect";

    private final Plugin plugin;
    private final String channel;

    BackendTravelConnectService(Plugin plugin, String channel) {
        this.plugin = plugin;
        this.channel = channel;
    }

    void connect(Player player, String serverProxyName) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(CONNECT_COMMAND);
        byteArrayDataOutput.writeUTF(serverProxyName);
        player.sendPluginMessage(this.plugin, this.channel, byteArrayDataOutput.toByteArray());
    }
}
