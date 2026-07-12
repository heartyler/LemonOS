/*
 * Backend-side LemonOS open pad protocol receiver.
 */
package dev.lemonos;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

final class BackendOpenCubeeMessageService {
    private final Plugin plugin;
    private final OpenCubeeHandler openCubeeHandler;

    BackendOpenCubeeMessageService(Plugin plugin, OpenCubeeHandler openCubeeHandler) {
        this.plugin = plugin;
        this.openCubeeHandler = openCubeeHandler;
    }

    boolean handleOpenCubeeMessage(Player player, byte[] data) {
        try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));){
            String command = dataInputStream.readUTF();
            if (!BackendAdminProtocol.OPEN_CUBEE.equals(command)) {
                return false;
            }
            UUID uuid = UUID.fromString(dataInputStream.readUTF());
            boolean recovery = dataInputStream.available() > 0 && dataInputStream.readBoolean();
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                Player targetPlayer = Bukkit.getPlayer(uuid);
                if (targetPlayer == null || !targetPlayer.isOnline()) {
                    return;
                }
                this.openCubeeHandler.handle(targetPlayer, recovery);
            });
            return true;
        }
        catch (IOException | IllegalArgumentException exception) {
            return false;
        }
    }

    interface OpenCubeeHandler {
        void handle(Player player, boolean recovery);
    }
}
