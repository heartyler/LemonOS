package dev.lemonos.admin;

import java.util.Map;

public final class BackendAdminPagedSelectionClickService {
    public SelectionResult action(int clickedSlot, int backSlot, int nextPageSlot, Map<Integer, String> slotKeys) {
        if (clickedSlot == backSlot) return new SelectionResult(SelectionAction.BACK, null);
        if (clickedSlot == nextPageSlot) return new SelectionResult(SelectionAction.NEXT_PAGE, null);
        String selectedName = slotKeys == null ? null : slotKeys.get(clickedSlot);
        if (selectedName != null) return new SelectionResult(SelectionAction.SELECT, selectedName);
        return new SelectionResult(SelectionAction.NONE, null);
    }

    public record SelectionResult(SelectionAction action, String selectedName) { }
    public enum SelectionAction { NONE, BACK, NEXT_PAGE, SELECT }
}
