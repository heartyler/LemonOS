package dev.lemonos;

import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

final class BackendAdminPeopleActionService {
    private final Predicate<UUID> busy;

    BackendAdminPeopleActionService(Predicate<UUID> busy) {
        this.busy = busy;
    }

    Player currentTarget(Player actor, UUID targetId) {
        Player target = targetId == null ? null : Bukkit.getPlayer((UUID)targetId);
        return this.canTarget(actor, target) ? target : null;
    }

    boolean canTarget(Player actor, Player target) {
        return actor != null && target != null && target.isOnline() && !target.getUniqueId().equals(actor.getUniqueId());
    }

    boolean canStartMessage(Player target) {
        return target != null && !this.busy.test(target.getUniqueId());
    }

    String messageLore(Player target, String defaultLore) {
        return this.canStartMessage(target) ? defaultLore : "busy.";
    }
}
