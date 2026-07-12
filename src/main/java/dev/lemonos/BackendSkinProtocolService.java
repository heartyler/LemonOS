/*
 * Backend-side LemonOS skin protocol transport.
 */
package dev.lemonos;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

final class BackendSkinProtocolService {
    private final Plugin plugin;
    private final SkinResultHandler resultHandler;

    BackendSkinProtocolService(Plugin plugin, SkinResultHandler resultHandler) {
        this.plugin = plugin;
        this.resultHandler = resultHandler;
    }

    void sendSkinApplyRequest(Player player, String skinName) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(BackendAdminProtocol.SKIN_APPLY);
        byteArrayDataOutput.writeUTF(player.getUniqueId().toString());
        byteArrayDataOutput.writeUTF(skinName);
        player.sendPluginMessage(this.plugin, BackendAdminProtocol.ADMIN_CHANNEL, byteArrayDataOutput.toByteArray());
    }

    boolean handleSkinResultMessage(byte[] data) {
        try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));){
            String command = dataInputStream.readUTF();
            if (!BackendAdminProtocol.SKIN_RESULT.equals(command)) {
                return false;
            }
            UUID uuid = UUID.fromString(dataInputStream.readUTF());
            String skinName = dataInputStream.readUTF();
            String result = dataInputStream.readUTF();
            Bukkit.getScheduler().runTask(this.plugin, () -> this.resultHandler.handle(uuid, skinName, result));
            return true;
        }
        catch (IOException | IllegalArgumentException exception) {
            return false;
        }
    }

    interface SkinResultHandler {
        void handle(UUID uuid, String skinName, String result);
    }
}
