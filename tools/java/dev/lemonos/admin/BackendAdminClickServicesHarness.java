package dev.lemonos.admin;

import java.util.Map;

public final class BackendAdminClickServicesHarness {
    private BackendAdminClickServicesHarness() {
    }

    public static void main(String[] args) {
        BackendAdminConfirmClickService confirm = new BackendAdminConfirmClickService();
        assertConfirm(confirm.action(14, 14, 12), BackendAdminConfirmClickService.ConfirmAction.CONFIRM);
        assertConfirm(confirm.action(12, 14, 12), BackendAdminConfirmClickService.ConfirmAction.CANCEL);
        assertConfirm(confirm.action(14, 14, 12, false), BackendAdminConfirmClickService.ConfirmAction.NONE);
        assertConfirm(confirm.action(12, 14, 12, false), BackendAdminConfirmClickService.ConfirmAction.CANCEL);
        assertConfirm(confirm.action(7, 14, 12), BackendAdminConfirmClickService.ConfirmAction.NONE);

        BackendAdminPagedSelectionClickService paged = new BackendAdminPagedSelectionClickService();
        assertSelection(paged.action(26, 26, 4, Map.of(26, "ignored")),
                BackendAdminPagedSelectionClickService.SelectionAction.BACK, null);
        assertSelection(paged.action(4, 26, 4, Map.of(4, "ignored")),
                BackendAdminPagedSelectionClickService.SelectionAction.NEXT_PAGE, null);
        assertSelection(paged.action(11, 26, 4, Map.of(11, "alex")),
                BackendAdminPagedSelectionClickService.SelectionAction.SELECT, "alex");
        assertSelection(paged.action(11, 26, 4, null),
                BackendAdminPagedSelectionClickService.SelectionAction.NONE, null);
        System.out.println("Backend consolidated admin click harness OK");
    }

    private static void assertConfirm(
            BackendAdminConfirmClickService.ConfirmAction actual,
            BackendAdminConfirmClickService.ConfirmAction expected) {
        if (actual != expected) {
            throw new IllegalStateException("Confirm action expected " + expected + " but was " + actual);
        }
    }

    private static void assertSelection(
            BackendAdminPagedSelectionClickService.SelectionResult actual,
            BackendAdminPagedSelectionClickService.SelectionAction expectedAction,
            String expectedName) {
        if (actual.action() != expectedAction || !java.util.Objects.equals(actual.selectedName(), expectedName)) {
            throw new IllegalStateException("Selection expected " + expectedAction + "/" + expectedName
                    + " but was " + actual.action() + "/" + actual.selectedName());
        }
    }
}
