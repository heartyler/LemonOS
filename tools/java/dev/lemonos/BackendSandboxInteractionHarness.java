package dev.lemonos;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BackendSandboxInteractionHarness {
    public static void main(String[] args) {
        BackendSandboxInteractionService service = new BackendSandboxInteractionService();
        Map<Integer, BackendSandboxInteractionService.SandboxClickAction> routes = new LinkedHashMap<>();
        routes.put(0, BackendSandboxInteractionService.SandboxClickAction.BACK);
        routes.put(1, BackendSandboxInteractionService.SandboxClickAction.UNDO);
        routes.put(2, BackendSandboxInteractionService.SandboxClickAction.REDO);
        routes.put(3, BackendSandboxInteractionService.SandboxClickAction.SET);
        routes.put(4, BackendSandboxInteractionService.SandboxClickAction.WALL);
        routes.put(5, BackendSandboxInteractionService.SandboxClickAction.FLOOR);
        routes.put(12, BackendSandboxInteractionService.SandboxClickAction.REPLACE);
        routes.put(13, BackendSandboxInteractionService.SandboxClickAction.CLONE);
        routes.put(14, BackendSandboxInteractionService.SandboxClickAction.CLEAR);
        routes.put(21, BackendSandboxInteractionService.SandboxClickAction.CIRCLE);
        routes.put(22, BackendSandboxInteractionService.SandboxClickAction.FLIP);
        routes.put(23, BackendSandboxInteractionService.SandboxClickAction.ROTATE);
        routes.forEach((slot, action) -> require(service.action(slot) == action, "wrong route for slot " + slot));
        require(service.action(-1) == BackendSandboxInteractionService.SandboxClickAction.NONE, "negative slot routed");
        require(service.action(24) == BackendSandboxInteractionService.SandboxClickAction.NONE, "unknown slot routed");
        require(service.confirmAction(14, 14, 12) == BackendSandboxInteractionService.ConfirmAction.CONFIRM, "confirm not routed");
        require(service.confirmAction(12, 14, 12) == BackendSandboxInteractionService.ConfirmAction.CANCEL, "cancel not routed");
        require(service.confirmAction(13, 14, 12) == BackendSandboxInteractionService.ConfirmAction.NONE, "unknown confirm slot routed");
        System.out.println("Backend Sandbox interaction harness OK");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
