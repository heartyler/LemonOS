package dev.lemonos;

import java.util.Map;

final class BackendAdminRequestsClickService {
    AdminRequestsClickResult action(int clickedSlot, int backSlot, int nextPageSlot, Map<Integer, String> slotKeys) {
        if (clickedSlot == backSlot) {
            return AdminRequestsClickResult.back();
        }
        if (clickedSlot == nextPageSlot) {
            return AdminRequestsClickResult.nextPage();
        }
        String selectedToken = slotKeys == null ? null : slotKeys.get(clickedSlot);
        if (selectedToken != null) {
            return AdminRequestsClickResult.select(selectedToken);
        }
        return AdminRequestsClickResult.none();
    }

    static final class AdminRequestsClickResult {
        final AdminRequestsAction action;
        final String selectedToken;

        private AdminRequestsClickResult(AdminRequestsAction action, String selectedToken) {
            this.action = action;
            this.selectedToken = selectedToken;
        }

        static AdminRequestsClickResult none() {
            return new AdminRequestsClickResult(AdminRequestsAction.NONE, null);
        }

        static AdminRequestsClickResult back() {
            return new AdminRequestsClickResult(AdminRequestsAction.BACK, null);
        }

        static AdminRequestsClickResult nextPage() {
            return new AdminRequestsClickResult(AdminRequestsAction.NEXT_PAGE, null);
        }

        static AdminRequestsClickResult select(String selectedToken) {
            return new AdminRequestsClickResult(AdminRequestsAction.SELECT, selectedToken);
        }
    }

    enum AdminRequestsAction {
        NONE,
        BACK,
        NEXT_PAGE,
        SELECT;
    }
}
