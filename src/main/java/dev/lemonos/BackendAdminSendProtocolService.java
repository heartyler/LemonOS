package dev.lemonos;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

final class BackendAdminSendProtocolService {
    private final Plugin plugin;
    private final ResultHandler resultHandler;

    BackendAdminSendProtocolService(Plugin plugin, ResultHandler resultHandler) {
        this.plugin = plugin;
        this.resultHandler = resultHandler;
    }

    void send(Player actor, UUID targetId, String place, UUID operationId) {
        if (actor == null || !actor.isOnline() || targetId == null || place == null || operationId == null) return;
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(BackendAdminProtocol.SEND_PLAYER_PLACE);
        output.writeUTF(actor.getUniqueId().toString());
        output.writeUTF(targetId.toString());
        output.writeUTF(place);
        output.writeUTF(operationId.toString());
        actor.sendPluginMessage(this.plugin, BackendAdminProtocol.ADMIN_CHANNEL, output.toByteArray());
    }

    boolean handleResult(byte[] data) {
        try {
            ByteArrayDataInput input = ByteStreams.newDataInput(data);
            if (!BackendAdminProtocol.SEND_PLAYER_RESULT.equals(input.readUTF())) return false;
            UUID actorId = UUID.fromString(input.readUTF());
            UUID targetId = UUID.fromString(input.readUTF());
            String place = input.readUTF();
            UUID operationId = UUID.fromString(input.readUTF());
            String result = input.readUTF();
            this.resultHandler.handle(actorId, targetId, place, operationId, result);
            return true;
        }
        catch (RuntimeException ignored) {
            return false;
        }
    }

    interface ResultHandler {
        void handle(UUID actorId, UUID targetId, String place, UUID operationId, String result);
    }
}
