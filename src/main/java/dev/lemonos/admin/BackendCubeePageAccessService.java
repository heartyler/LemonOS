package dev.lemonos.admin;

/** Centralizes access policy for Cubee inventory page routing. */
public final class BackendCubeePageAccessService {
    public AccessPolicy policy(String pageName) {
        if (pageName == null || pageName.isBlank()) {
            throw new IllegalArgumentException("Cubee page name is required.");
        }
        if ("ADMIN_CHUNKS_DIMENSION".equals(pageName) || "ADMIN_CHUNKS_SIZE".equals(pageName)) {
            return AccessPolicy.ADMIN_CHUNK_EDIT;
        }
        if (pageName.startsWith("ADMIN")) {
            return AccessPolicy.ADMIN;
        }
        return AccessPolicy.PUBLIC;
    }

    public enum AccessPolicy {
        PUBLIC(false, false),
        ADMIN(true, false),
        ADMIN_CHUNK_EDIT(true, true);

        private final boolean adminRequired;
        private final boolean chunkEditRequired;

        AccessPolicy(boolean adminRequired, boolean chunkEditRequired) {
            this.adminRequired = adminRequired;
            this.chunkEditRequired = chunkEditRequired;
        }

        public boolean adminRequired() {
            return this.adminRequired;
        }

        public boolean chunkEditRequired() {
            return this.chunkEditRequired;
        }
    }
}
