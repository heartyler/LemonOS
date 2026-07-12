package dev.lemonos;

import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;

public final class BackendResetSessionHarness {
    public static void main(String[] args) {
        BackendIdentityResetService service = new BackendIdentityResetService();
        YamlConfiguration identities = new YamlConfiguration();
        UUID session = UUID.randomUUID();
        UUID instance = UUID.randomUUID();

        BackendIdentityResetService.CreateResult first = service.createRequest(
                identities, "Player", "java:player", false, session, "lobby", instance, () -> true);
        require(first.created(), "first request must be created");
        require(service.requestExistsByReference(identities, first.reference()), "created reference missing");
        BackendIdentityResetService.CreateResult duplicate = service.createRequest(
                identities, "Player", "java:player", false, session, "lobby", instance, () -> true);
        require(!duplicate.created() && duplicate.reference().equals(first.reference()), "duplicate request must retain reference");
        require(!service.clearRequestForSession(identities, "java:player", UUID.randomUUID(), "lobby", instance, () -> true), "wrong session cleared request");

        require(!service.clearRequestReference(identities, first.reference(), () -> false), "failed save reported successful clear");
        require(service.requestExistsByReference(identities, first.reference()), "failed save did not restore request");
        require(service.clearRequestForSession(identities, "java:player", session, "lobby", instance, () -> true), "matching session did not clear");

        BackendIdentityResetService.CreateResult second = service.createRequest(
                identities, "Player", "java:player", false, session, "lobby", instance, () -> true);
        require(second.created() && !second.reference().equals(first.reference()), "new request must use new request_id");
        require(!service.requestExistsByReference(identities, first.reference()), "stale reference became current");
        require(!service.removeRequestReference(identities, first.reference()), "stale reference removed current request");

        int cleared = service.clearStaleRequests(identities, "lobby", UUID.randomUUID(), () -> true);
        require(cleared == 1 && !service.requestExistsByReference(identities, second.reference()), "stale boot cleanup failed");
        System.out.println("Backend Reset session harness OK");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
