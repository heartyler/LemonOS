package dev.lemonos;

import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

final class BackendAdminPlayerControlService {
    private static final int CONTROL_PAGE_MARKER_BASE = -1000;
    private final Predicate<ItemStack> cubeeItem;
    private final Predicate<ItemStack> loginItem;

    BackendAdminPlayerControlService(Predicate<ItemStack> cubeeItem, Predicate<ItemStack> loginItem) {
        this.cubeeItem = cubeeItem;
        this.loginItem = loginItem;
    }

    int controlPageMarker(int pageIndex) {
        return CONTROL_PAGE_MARKER_BASE - Math.max(0, pageIndex);
    }

    boolean isControlPageMarker(int pageIndex) {
        return pageIndex <= CONTROL_PAGE_MARKER_BASE;
    }

    int controlPageIndex(int marker) {
        return Math.max(0, CONTROL_PAGE_MARKER_BASE - marker);
    }

    boolean isOnline(Player player) {
        return player != null && player.isOnline();
    }

    boolean isSelf(Player actor, Player target) {
        return actor != null && target != null && target.getUniqueId().equals(actor.getUniqueId());
    }

    boolean canOpenGamemode(Player target) {
        return this.isOnline(target);
    }

    boolean gamemodeApplied(Player target, GameMode gameMode) {
        return this.isOnline(target) && target.getGameMode() == gameMode;
    }

    boolean verifyClearedInventory(Player player) {
        if (!this.isOnline(player)) {
            return false;
        }
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); ++i) {
            if (!this.isAllowedAfterClear(inventory.getItem(i))) {
                return false;
            }
        }
        return this.isAllowedAfterClear(inventory.getItemInOffHand());
    }

    UUID targetId(Player player) {
        return player == null ? null : player.getUniqueId();
    }

    private boolean isAllowedAfterClear(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().isAir() || this.cubeeItem.test(itemStack) || this.loginItem.test(itemStack);
    }
}
