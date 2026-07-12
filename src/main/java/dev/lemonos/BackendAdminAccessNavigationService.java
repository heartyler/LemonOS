package dev.lemonos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

final class BackendAdminAccessNavigationService {
    private final Supplier<List<String>> accessHolderNames;
    private final Supplier<List<String>> networkOnlineNames;
    private final Function<String, String> normalizeAccessName;
    private final Predicate<String> safeAdminName;
    private final int pageSize;

    BackendAdminAccessNavigationService(Supplier<List<String>> accessHolderNames, Supplier<List<String>> networkOnlineNames, Function<String, String> normalizeAccessName, Predicate<String> safeAdminName, int pageSize) {
        this.accessHolderNames = accessHolderNames;
        this.networkOnlineNames = networkOnlineNames;
        this.normalizeAccessName = normalizeAccessName;
        this.safeAdminName = safeAdminName;
        this.pageSize = pageSize;
    }

    List<String> holderNames(Player viewer) {
        ArrayList<String> holders = new ArrayList<String>(this.accessHolderNames.get());
        String self = this.normalizeAccessName.apply(viewer.getName());
        if (holders.remove(self)) {
            holders.add(0, self);
        }
        return holders;
    }

    List<String> candidateNames() {
        HashSet<String> holders = new HashSet<String>(this.accessHolderNames.get());
        List<String> candidates = this.networkOnlineNames.get();
        if (candidates.isEmpty()) {
            candidates = new ArrayList<String>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = this.normalizeAccessName.apply(player.getName());
                if (!this.safeAdminName.test(name)) {
                    continue;
                }
                candidates.add(name);
            }
        }
        return candidates.stream().filter(name -> !holders.contains(this.normalizeAccessName.apply(name))).distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }

    int pageIndex(int requestedPage, int totalItems) {
        int maxPage = Math.max(0, (Math.max(0, totalItems) - 1) / this.pageSize);
        return Math.max(0, Math.min(requestedPage, maxPage));
    }

    boolean hasAnyNextPage(int totalItems) {
        return this.hasNextPage(0, totalItems);
    }

    int nextLoopPage(int currentPage, int totalItems) {
        if (!this.hasAnyNextPage(totalItems)) {
            return this.pageIndex(currentPage, totalItems);
        }
        int nextPage = currentPage + 1;
        return this.hasNextPage(currentPage, totalItems) ? nextPage : 0;
    }

    List<SlotKey> pageKeys(List<String> names, int pageIndex, int[] slots) {
        ArrayList<SlotKey> keys = new ArrayList<SlotKey>();
        int start = pageIndex * this.pageSize;
        for (int i = 0; i < slots.length && start + i < names.size(); ++i) {
            keys.add(new SlotKey(slots[i], names.get(start + i)));
        }
        return keys;
    }

    private boolean hasNextPage(int pageIndex, int totalItems) {
        return (pageIndex + 1) * this.pageSize < totalItems;
    }

    static final class SlotKey {
        final int slot;
        final String name;

        SlotKey(int slot, String name) {
            this.slot = slot;
            this.name = name;
        }
    }
}
