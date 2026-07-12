/*
 * Backend-side LemonOS meet-up request lifecycle.
 */
package dev.lemonos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

final class BackendMeetRequestService<K> {
    private static final long REQUEST_TIMEOUT_TICKS = 400L;
    private static final long LATE_REQUEST_TICKS = 200L;
    private final Plugin plugin;
    private final Predicate<UUID> busy;
    private final Predicate<Player> sociallyBusy;
    private final Predicate<K> visitKind;
    private final BiConsumer<Player, Player> visitTravel;
    private final BiConsumer<Player, Player> inviteTravel;
    private final Consumer<Player> notificationSound;
    private final Consumer<Player> pendingStatus;
    private final Map<UUID, RequestState<K>> incomingRequests = new HashMap<UUID, RequestState<K>>();
    private final Map<UUID, RequestState<K>> outgoingRequests = new HashMap<UUID, RequestState<K>>();
    private final Map<UUID, BukkitTask> lateRequestTasks = new HashMap<UUID, BukkitTask>();

    BackendMeetRequestService(Plugin plugin, Predicate<UUID> busy, Predicate<Player> sociallyBusy, Predicate<K> visitKind, BiConsumer<Player, Player> visitTravel, BiConsumer<Player, Player> inviteTravel, Consumer<Player> notificationSound, Consumer<Player> pendingStatus) {
        this.plugin = plugin;
        this.busy = busy;
        this.sociallyBusy = sociallyBusy;
        this.visitKind = visitKind;
        this.visitTravel = visitTravel;
        this.inviteTravel = inviteTravel;
        this.notificationSound = notificationSound;
        this.pendingStatus = pendingStatus;
    }

    RequestState<K> incoming(UUID uuid) {
        return this.incomingRequests.get(uuid);
    }

    boolean hasActive(UUID uuid) {
        return this.incomingRequests.containsKey(uuid) || this.outgoingRequests.containsKey(uuid);
    }

    void create(Player sender, Player receiver, K kind, String receiverMessage) {
        if (this.busy.test(sender.getUniqueId()) || this.sociallyBusy.test(receiver)) {
            return;
        }
        this.clearLate(sender.getUniqueId());
        this.clearLate(receiver.getUniqueId());
        RequestState<K> requestState = new RequestState<K>(sender.getUniqueId(), receiver.getUniqueId(), kind);
        requestState.timeoutTask = Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.expire(requestState), REQUEST_TIMEOUT_TICKS);
        this.register(requestState);
        sender.closeInventory();
        this.pendingStatus.accept(sender);
        receiver.sendMessage(((TextComponent)Component.text((String)sender.getName(), (TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.space())).append((Component)Component.text((String)receiverMessage, (TextColor)NamedTextColor.GRAY)));
        this.notificationSound.accept(receiver);
        receiver.sendMessage((Component)Component.text((String)"open cubee.", (TextColor)NamedTextColor.GRAY));
    }

    void accept(Player receiver, RequestState<K> requestState) {
        Player sender = Bukkit.getPlayer((UUID)requestState.sender);
        if (sender == null || !sender.isOnline()) {
            receiver.closeInventory();
            receiver.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            this.clear(requestState);
            return;
        }
        this.clear(requestState);
        receiver.closeInventory();
        if (this.visitKind.test(requestState.kind)) {
            this.visitTravel.accept(sender, receiver);
        } else {
            this.inviteTravel.accept(receiver, sender);
        }
    }

    void decline(RequestState<K> requestState) {
        this.clear(requestState);
    }

    void expire(RequestState<K> requestState) {
        requestState.expired = true;
        this.unlink(requestState);
        this.markLate(requestState.receiver);
    }

    void clearFor(UUID uuid) {
        RequestState<K> incoming = this.incomingRequests.get(uuid);
        if (incoming != null) {
            this.clear(incoming);
        }
        RequestState<K> outgoing = this.outgoingRequests.get(uuid);
        if (outgoing != null) {
            this.clear(outgoing);
        }
        this.clearLate(uuid);
    }

    void clear(RequestState<K> requestState) {
        this.cancelTimeout(requestState);
        this.unlink(requestState);
        this.clearLate(requestState.receiver);
        this.clearLate(requestState.sender);
    }

    boolean consumeLate(UUID uuid) {
        BukkitTask task = this.lateRequestTasks.remove(uuid);
        if (task == null) {
            return false;
        }
        task.cancel();
        return true;
    }

    boolean matches(UUID sender, K kind, RequestState<K> requestState) {
        return sender != null && kind != null && sender.equals(requestState.sender) && kind == requestState.kind;
    }

    void cancelAllAndClear() {
        for (RequestState<K> requestState : this.incomingRequests.values()) {
            this.cancelTimeout(requestState);
        }
        for (BukkitTask task : this.lateRequestTasks.values()) {
            task.cancel();
        }
        this.incomingRequests.clear();
        this.outgoingRequests.clear();
        this.lateRequestTasks.clear();
    }

    private void register(RequestState<K> requestState) {
        this.incomingRequests.put(requestState.receiver, requestState);
        this.outgoingRequests.put(requestState.sender, requestState);
    }

    private void unlink(RequestState<K> requestState) {
        this.incomingRequests.remove(requestState.receiver);
        this.outgoingRequests.remove(requestState.sender);
    }

    private void cancelTimeout(RequestState<K> requestState) {
        if (requestState.timeoutTask != null) {
            requestState.timeoutTask.cancel();
        }
    }

    private void markLate(UUID uuid) {
        this.clearLate(uuid);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.lateRequestTasks.remove(uuid), LATE_REQUEST_TICKS);
        this.lateRequestTasks.put(uuid, task);
    }

    private void clearLate(UUID uuid) {
        BukkitTask task = this.lateRequestTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    static final class RequestState<K> {
        final UUID sender;
        final UUID receiver;
        final K kind;
        BukkitTask timeoutTask;
        boolean expired;

        private RequestState(UUID sender, UUID receiver, K kind) {
            this.sender = sender;
            this.receiver = receiver;
            this.kind = kind;
        }
    }
}
