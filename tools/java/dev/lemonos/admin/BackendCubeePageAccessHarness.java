package dev.lemonos.admin;

public final class BackendCubeePageAccessHarness {
    private BackendCubeePageAccessHarness() {
    }

    public static void main(String[] args) {
        BackendCubeePageAccessService service = new BackendCubeePageAccessService();
        assertPolicy(service, "HOME", BackendCubeePageAccessService.AccessPolicy.PUBLIC);
        assertPolicy(service, "ADMIN", BackendCubeePageAccessService.AccessPolicy.ADMIN);
        assertPolicy(service, "ADMIN_FUTURE_PAGE", BackendCubeePageAccessService.AccessPolicy.ADMIN);
        assertPolicy(service, "ADMIN_CHUNKS_DIMENSION", BackendCubeePageAccessService.AccessPolicy.ADMIN_CHUNK_EDIT);
        assertPolicy(service, "ADMIN_CHUNKS_SIZE", BackendCubeePageAccessService.AccessPolicy.ADMIN_CHUNK_EDIT);
        try {
            service.policy(null);
            throw new IllegalStateException("Null page name was accepted.");
        }
        catch (IllegalArgumentException expected) {
        }
        System.out.println("Backend Cubee page access harness OK");
    }

    private static void assertPolicy(
            BackendCubeePageAccessService service,
            String page,
            BackendCubeePageAccessService.AccessPolicy expected) {
        BackendCubeePageAccessService.AccessPolicy actual = service.policy(page);
        if (actual != expected) {
            throw new IllegalStateException(page + " expected " + expected + " but was " + actual);
        }
    }
}
