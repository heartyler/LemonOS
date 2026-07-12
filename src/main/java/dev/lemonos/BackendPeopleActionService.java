package dev.lemonos;

import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.entity.Player;

final class BackendPeopleActionService {
    private final Predicate<UUID> busy;
    private final Predicate<Player> authLocked;
    private final Predicate<Player> sociallyBusy;

    BackendPeopleActionService(Predicate<UUID> busy, Predicate<Player> authLocked, Predicate<Player> sociallyBusy) {
        this.busy = busy;
        this.authLocked = authLocked;
        this.sociallyBusy = sociallyBusy;
    }

    boolean isCurrentOnline(Player target) {
        return target != null && target.isOnline();
    }

    boolean canCreateMeetRequest(Player actor, Player target) {
        return actor != null && this.isCurrentOnline(target) && !this.busy.test(actor.getUniqueId()) && !this.sociallyBusy.test(target);
    }

    boolean canStartPrivateNote(Player actor, Player target) {
        return actor != null && this.isCurrentOnline(target) && !this.authLocked.test(target) && !this.busy.test(actor.getUniqueId()) && !this.sociallyBusy.test(target);
    }

    boolean canDeliverPrivateNote(Player target) {
        return this.isCurrentOnline(target) && !this.authLocked.test(target) && !this.sociallyBusy.test(target);
    }
}
