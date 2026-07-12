/*
 * Backend-side LemonOS legacy access protocol.
 */
package dev.lemonos;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

final class BackendAccessLegacyService {
    private static final int ACCESS_CONFIRM_TIMEOUT_TICKS = 100;
    private final Plugin plugin;
    private final Function<String, String> nameNormalizer;
    private final AccessStateUpdater accessStateUpdater;
    private final Map<Long, PendingAccessRequest> pendingAccessRequests = new ConcurrentHashMap<Long, PendingAccessRequest>();
    private final AtomicLong accessRequestIds = new AtomicLong();

    BackendAccessLegacyService(Plugin plugin, Function<String, String> nameNormalizer, AccessStateUpdater accessStateUpdater) {
        this.plugin = plugin;
        this.nameNormalizer = nameNormalizer;
        this.accessStateUpdater = accessStateUpdater;
    }

    boolean sendAccessRequest(Player player, String normalizedName, boolean admin) {
        long requestId = this.accessRequestIds.incrementAndGet();
        UUID actorId = player.getUniqueId();
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.timeoutAccessRequest(requestId), (long)ACCESS_CONFIRM_TIMEOUT_TICKS);
        this.pendingAccessRequests.put(requestId, new PendingAccessRequest(actorId, normalizedName, admin, timeoutTask));
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeByte(BackendAdminProtocol.ACCESS_REQUEST_MAGIC);
        byteArrayDataOutput.writeLong(requestId);
        byteArrayDataOutput.writeByte(admin ? 1 : 0);
        byteArrayDataOutput.writeUTF(normalizedName);
        player.sendPluginMessage(this.plugin, BackendAdminProtocol.ADMIN_CHANNEL, byteArrayDataOutput.toByteArray());
        return true;
    }

    boolean hasPendingActor(UUID actorId) {
        return actorId != null && this.pendingAccessRequests.values().stream().anyMatch(request -> actorId.equals(request.actorId));
    }

    boolean handleAccessMessage(byte[] data) {
        if (data.length == 10 && data[0] == BackendAdminProtocol.ACCESS_ACK_MAGIC) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            byteBuffer.get();
            this.handleAccessAck(byteBuffer.getLong(), byteBuffer.get() == 1);
            return true;
        }
        if (data.length != 17) {
            return false;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        UUID uuid = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        boolean admin = byteBuffer.get() == 1;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return true;
        }
        this.accessStateUpdater.update(player, admin);
        return true;
    }

    void cancelPendingRequests() {
        for (PendingAccessRequest pendingAccessRequest : this.pendingAccessRequests.values()) {
            pendingAccessRequest.timeoutTask.cancel();
        }
    }

    void clear() {
        this.pendingAccessRequests.clear();
    }

    private void handleAccessAck(long requestId, boolean saved) {
        PendingAccessRequest pendingAccessRequest = this.pendingAccessRequests.remove(requestId);
        if (pendingAccessRequest == null) {
            return;
        }
        pendingAccessRequest.timeoutTask.cancel();
        Player player = Bukkit.getPlayer(pendingAccessRequest.actorId);
        if (!saved) {
            if (player != null && player.isOnline()) {
                player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            return;
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!this.nameNormalizer.apply(onlinePlayer.getName()).equals(pendingAccessRequest.name)) {
                continue;
            }
            this.accessStateUpdater.update(onlinePlayer, pendingAccessRequest.admin);
        }
        if (player != null && player.isOnline()) {
            player.sendMessage((Component)Component.text((String)"saved.", (TextColor)NamedTextColor.GRAY));
        }
    }

    private void timeoutAccessRequest(long requestId) {
        PendingAccessRequest pendingAccessRequest = this.pendingAccessRequests.remove(requestId);
        if (pendingAccessRequest == null) {
            return;
        }
        Player player = Bukkit.getPlayer(pendingAccessRequest.actorId);
        if (player != null && player.isOnline()) {
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
        }
    }

    interface AccessStateUpdater {
        void update(Player player, boolean admin);
    }

    private static final class PendingAccessRequest {
        private final UUID actorId;
        private final String name;
        private final boolean admin;
        private final BukkitTask timeoutTask;

        private PendingAccessRequest(UUID actorId, String name, boolean admin, BukkitTask timeoutTask) {
            this.actorId = actorId;
            this.name = name;
            this.admin = admin;
            this.timeoutTask = timeoutTask;
        }
    }
}
