package dev.lemonos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

final class BackendAdminResetNavigationService {
    private final Supplier<List<String>> resetRequestTokens;
    private final Function<String, String> resetRequestName;
    private final int pageSize;

    BackendAdminResetNavigationService(Supplier<List<String>> resetRequestTokens, Function<String, String> resetRequestName, int pageSize) {
        this.resetRequestTokens = resetRequestTokens;
        this.resetRequestName = resetRequestName;
        this.pageSize = pageSize;
    }

    List<String> tokens() {
        return this.resetRequestTokens.get();
    }

    String displayName(String token) {
        return this.resetRequestName.apply(token);
    }

    String countLore() {
        int count = this.tokens().size();
        return count == 0 ? null : count + " waiting.";
    }

    String requestLore(String displayName) {
        return displayName + " wants to reset.";
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

    List<SlotToken> pageTokens(List<String> tokens, int pageIndex, int[] slots) {
        ArrayList<SlotToken> pageTokens = new ArrayList<SlotToken>();
        int start = pageIndex * this.pageSize;
        for (int i = 0; i < slots.length && start + i < tokens.size(); ++i) {
            pageTokens.add(new SlotToken(slots[i], tokens.get(start + i)));
        }
        return pageTokens;
    }

    private boolean hasNextPage(int pageIndex, int totalItems) {
        return (pageIndex + 1) * this.pageSize < totalItems;
    }

    static final class SlotToken {
        final int slot;
        final String token;

        SlotToken(int slot, String token) {
            this.slot = slot;
            this.token = token;
        }
    }
}
