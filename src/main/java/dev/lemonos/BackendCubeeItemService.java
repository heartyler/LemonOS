package dev.lemonos;

import java.util.function.Predicate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

final class BackendCubeeItemService {
    private final Predicate<ItemStack> cubeeItem;

    BackendCubeeItemService(Predicate<ItemStack> cubeeItem) {
        this.cubeeItem = cubeeItem;
    }

    InventoryMode ensureMode(boolean selectSystemSlot) {
        return new InventoryMode(false, true, selectSystemSlot, false);
    }

    InventoryMode normalizeMode() {
        return new InventoryMode(true, false, false, true);
    }

    AccessAction accessAction(boolean authLocked, boolean enabled, boolean visible) {
        if (authLocked) {
            return AccessAction.HIDE_AUTH;
        }
        if (!enabled || !visible) {
            return AccessAction.PURGE;
        }
        return AccessAction.CONTINUE;
    }

    PlacementAction placementAction(boolean occupiedByOtherItem, boolean lobby, boolean moved) {
        if (!occupiedByOtherItem || lobby && moved) {
            return PlacementAction.PLACE;
        }
        return PlacementAction.HIDE;
    }

    boolean hasOutsideSystemSlot(PlayerInventory inventory, int systemSlot) {
        for (int slot = 0; slot < inventory.getSize(); ++slot) {
            if (slot == systemSlot || !this.cubeeItem.test(inventory.getItem(slot))) {
                continue;
            }
            return true;
        }
        return this.cubeeItem.test(inventory.getItemInOffHand());
    }

    void purge(PlayerInventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); ++slot) {
            if (!this.cubeeItem.test(inventory.getItem(slot))) {
                continue;
            }
            inventory.setItem(slot, null);
        }
        if (this.cubeeItem.test(inventory.getItemInOffHand())) {
            inventory.setItemInOffHand(null);
        }
    }

    void purgeOutsideSystemSlot(PlayerInventory inventory, int systemSlot) {
        for (int slot = 0; slot < inventory.getSize(); ++slot) {
            if (slot == systemSlot || !this.cubeeItem.test(inventory.getItem(slot))) {
                continue;
            }
            inventory.setItem(slot, null);
        }
        if (this.cubeeItem.test(inventory.getItemInOffHand())) {
            inventory.setItemInOffHand(null);
        }
    }

    boolean moveItemFromSystemSlot(PlayerInventory inventory, int systemSlot) {
        ItemStack itemStack = inventory.getItem(systemSlot);
        if (this.isEmpty(itemStack) || this.cubeeItem.test(itemStack)) {
            return true;
        }
        for (int slot = 0; slot < inventory.getSize(); ++slot) {
            if (slot == systemSlot || !this.isEmpty(inventory.getItem(slot))) {
                continue;
            }
            inventory.setItem(slot, itemStack);
            inventory.setItem(systemSlot, null);
            return true;
        }
        return false;
    }

    private boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().isAir();
    }

    enum AccessAction {
        HIDE_AUTH,
        PURGE,
        CONTINUE
    }

    enum PlacementAction {
        PLACE,
        HIDE
    }

    record InventoryMode(boolean purgeAll, boolean fastKeep, boolean selectSystemSlot, boolean updateInventory) {
    }
}
