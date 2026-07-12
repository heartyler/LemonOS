package dev.lemonos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class BackendOperationRegistryHarness {
    public static void main(String[] args) {
        BackendOperationRegistry<String, String> registry = new BackendOperationRegistry<>();
        BackendOperationToken first = new BackendOperationToken(UUID.fromString("00000000-0000-0000-0000-000000000001"), 1L, 10L);
        BackendOperationToken second = new BackendOperationToken(UUID.fromString("00000000-0000-0000-0000-000000000002"), 2L, 20L);

        require(registry.beginIfAbsent("actor", first, "send-1"), "first begin must succeed");
        require(!registry.beginIfAbsent("actor", second, "send-2"), "duplicate begin must not replace");
        require(registry.isCurrent("actor", first), "first token must be current");
        require(!registry.isCurrent("actor", second), "second token must not be current yet");
        List<String> used = new ArrayList<>();
        require(registry.useIfCurrent("actor", first, used::add), "current token must use operation atomically");
        require(!registry.useIfCurrent("actor", second, used::add), "stale token must not use operation");
        require(used.equals(List.of("send-1")), "use-if-current must expose only the current operation");
        require(registry.replace("actor", second, "send-2").orElseThrow().operation().equals("send-1"), "replace must return prior operation");
        require(registry.removeIfCurrent("actor", first).isEmpty(), "stale token must not remove replacement");
        require(registry.removeIfCurrent("actor", second).orElseThrow().operation().equals("send-2"), "current token must remove");
        require(registry.size() == 0, "registry must be empty after current removal");

        registry.beginIfAbsent("one", BackendOperationToken.create(3L), "cleanup-1");
        registry.beginIfAbsent("two", BackendOperationToken.create(4L), "cleanup-2");
        List<String> cleaned = new ArrayList<>();
        registry.clear(cleaned::add);
        require(registry.size() == 0, "clear must remove snapshot entries");
        require(cleaned.size() == 2 && cleaned.containsAll(List.of("cleanup-1", "cleanup-2")), "clear must clean each operation exactly once");
        System.out.println("Backend operation registry harness OK");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
