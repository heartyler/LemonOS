package dev.lemonos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

final class BackendPeopleNavigationService {
    private final Map<UUID, Integer> pageIndexes;
    private final Predicate<Player> sociallyBusy;
    private final int pageSize;

    BackendPeopleNavigationService(Map<UUID, Integer> pageIndexes, Predicate<Player> sociallyBusy, int pageSize) {
        this.pageIndexes = pageIndexes;
        this.sociallyBusy = sociallyBusy;
        this.pageSize = pageSize;
    }

    int currentPageIndex(Player player) {
        return this.pageIndexes.getOrDefault(player.getUniqueId(), 0);
    }

    void rememberPageIndex(Player player, int pageIndex) {
        this.pageIndexes.put(player.getUniqueId(), Math.max(0, pageIndex));
    }

    List<Player> listPeople(Player viewer) {
        ArrayList<Player> people = new ArrayList<Player>();
        for (Player candidate : Bukkit.getOnlinePlayers()) {
            if (candidate.getUniqueId().equals(viewer.getUniqueId())) {
                continue;
            }
            people.add(candidate);
        }
        people.sort(Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        return people;
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

    List<SlotTarget> pageTargets(List<Player> people, int pageIndex, int[] slots) {
        ArrayList<SlotTarget> targets = new ArrayList<SlotTarget>();
        int start = pageIndex * this.pageSize;
        for (int i = 0; i < slots.length && start + i < people.size(); ++i) {
            targets.add(new SlotTarget(slots[i], people.get(start + i)));
        }
        return targets;
    }

    String itemStatus(Player player) {
        return this.sociallyBusy.test(player) ? "busy." : "meet up.";
    }

    boolean canOpenPlayer(Player player) {
        return player != null && player.isOnline() && !this.sociallyBusy.test(player);
    }

    private boolean hasNextPage(int pageIndex, int totalItems) {
        return (pageIndex + 1) * this.pageSize < totalItems;
    }

    static final class SlotTarget {
        final int slot;
        final Player player;

        SlotTarget(int slot, Player player) {
            this.slot = slot;
            this.player = player;
        }
    }
}
