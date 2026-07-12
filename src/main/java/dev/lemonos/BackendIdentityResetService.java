/*
 * Backend-side LemonOS session-bound identity reset request/grant persistence.
 */
package dev.lemonos;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendIdentityResetService {
    private static final String REFERENCE_SEPARATOR = "~";

    CreateResult createRequest(FileConfiguration identities, String playerName, String identityKey, boolean bedrock,
            UUID sessionId, String ownerBackend, UUID ownerInstance, IdentitySaver identitySaver) {
        String token = this.identityPathToken(identityKey);
        String path = this.resetRequestPath(token);
        ConfigurationSection existing = identities.getConfigurationSection(path);
        String existingId = existing == null ? "" : existing.getString("request_id", "");
        if (!existingId.isBlank()) return new CreateResult(this.reference(token, existingId), false);

        String requestId = UUID.randomUUID().toString();
        identities.set(path + ".request_id", requestId);
        identities.set(path + ".session_id", sessionId.toString());
        identities.set(path + ".owner_backend", ownerBackend);
        identities.set(path + ".owner_instance", ownerInstance.toString());
        identities.set(path + ".key", identityKey);
        identities.set(path + ".name", playerName);
        identities.set(path + ".platform", bedrock ? "bedrock" : "java");
        identities.set(path + ".created", System.currentTimeMillis());
        if (!identitySaver.save()) {
            identities.set(path, null);
            return new CreateResult("", false);
        }
        return new CreateResult(this.reference(token, requestId), true);
    }

    boolean clearRequestForIdentity(FileConfiguration identities, String identityKey, IdentitySaver identitySaver) {
        String reference = this.currentReference(identities, identityKey);
        return !reference.isBlank() && this.clearRequestReference(identities, reference, identitySaver);
    }

    boolean clearRequestForSession(FileConfiguration identities, String identityKey, UUID sessionId,
            String ownerBackend, UUID ownerInstance, IdentitySaver identitySaver) {
        String reference = this.currentReference(identities, identityKey);
        RequestParts parts = this.parts(reference);
        if (parts == null) return false;
        String path = this.resetRequestPath(parts.token());
        if (!sessionId.toString().equals(identities.getString(path + ".session_id", ""))
                || !ownerBackend.equals(identities.getString(path + ".owner_backend", ""))
                || !ownerInstance.toString().equals(identities.getString(path + ".owner_instance", ""))) return false;
        return this.clearRequestReference(identities, reference, identitySaver);
    }

    boolean clearRequestReference(FileConfiguration identities, String reference, IdentitySaver identitySaver) {
        RequestParts parts = this.parts(reference);
        if (parts == null || !this.requestExistsByReference(identities, reference)) return false;
        String path = this.resetRequestPath(parts.token());
        ConfigurationSection snapshot = identities.getConfigurationSection(path);
        java.util.Map<String, Object> oldValues = snapshot == null ? java.util.Map.of() : snapshot.getValues(true);
        identities.set(path, null);
        if (identitySaver.save()) return true;
        for (java.util.Map.Entry<String, Object> entry : oldValues.entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection)) identities.set(path + "." + entry.getKey(), entry.getValue());
        }
        return false;
    }

    boolean removeRequestReference(FileConfiguration identities, String reference) {
        RequestParts parts = this.parts(reference);
        if (parts == null || !this.requestExistsByReference(identities, reference)) return false;
        identities.set(this.resetRequestPath(parts.token()), null);
        return true;
    }

    int clearOwnedRequests(FileConfiguration identities, String ownerBackend, UUID ownerInstance, IdentitySaver saver) {
        java.util.Map<String, Object> snapshot = this.snapshotRequests(identities);
        List<String> references = this.requestReferences(identities);
        int cleared = 0;
        for (String reference : references) {
            RequestParts parts = this.parts(reference);
            if (parts == null) continue;
            String path = this.resetRequestPath(parts.token());
            if (!ownerBackend.equals(identities.getString(path + ".owner_backend", ""))) continue;
            if (!ownerInstance.toString().equals(identities.getString(path + ".owner_instance", ""))) continue;
            identities.set(path, null);
            cleared++;
        }
        if (cleared > 0 && !saver.save()) {
            this.restoreRequests(identities, snapshot);
            return 0;
        }
        return cleared;
    }

    int clearStaleRequests(FileConfiguration identities, String ownerBackend, UUID currentInstance, IdentitySaver saver) {
        ConfigurationSection root = identities == null ? null : identities.getConfigurationSection("reset_requests");
        if (root == null) return 0;
        java.util.Map<String, Object> snapshot = this.snapshotRequests(identities);
        int cleared = 0;
        for (String token : List.copyOf(root.getKeys(false))) {
            String path = this.resetRequestPath(token);
            String backend = identities.getString(path + ".owner_backend", "");
            String instance = identities.getString(path + ".owner_instance", "");
            boolean legacy = identities.getString(path + ".request_id", "").isBlank();
            if (legacy || (ownerBackend.equals(backend) && !currentInstance.toString().equals(instance))) {
                identities.set(path, null);
                cleared++;
            }
        }
        if (cleared > 0 && !saver.save()) {
            this.restoreRequests(identities, snapshot);
            return 0;
        }
        return cleared;
    }

    List<String> requestReferences(FileConfiguration identities) {
        if (identities == null || identities.getConfigurationSection("reset_requests") == null) return List.of();
        ArrayList<String> references = new ArrayList<>();
        for (String token : identities.getConfigurationSection("reset_requests").getKeys(false)) {
            String requestId = identities.getString(this.resetRequestPath(token) + ".request_id", "");
            if (!requestId.isBlank()) references.add(this.reference(token, requestId));
        }
        references.sort(Comparator.comparing(reference -> this.requestName(identities, reference), String.CASE_INSENSITIVE_ORDER));
        return references;
    }

    boolean requestExistsForIdentity(FileConfiguration identities, String identityKey) {
        return !this.currentReference(identities, identityKey).isBlank();
    }

    boolean requestExistsByReference(FileConfiguration identities, String reference) {
        RequestParts parts = this.parts(reference);
        if (parts == null || identities == null) return false;
        return parts.requestId().equals(identities.getString(this.resetRequestPath(parts.token()) + ".request_id", ""));
    }

    String requestName(FileConfiguration identities, String reference) {
        RequestParts parts = this.parts(reference);
        return parts == null || identities == null ? "Player" : identities.getString(this.resetRequestPath(parts.token()) + ".name", "Player");
    }

    String requestKey(FileConfiguration identities, String reference) {
        RequestParts parts = this.parts(reference);
        return parts == null || identities == null ? "" : identities.getString(this.resetRequestPath(parts.token()) + ".key", "");
    }

    String currentReference(FileConfiguration identities, String identityKey) {
        if (identities == null || identityKey == null || identityKey.isBlank()) return "";
        String token = this.identityPathToken(identityKey);
        String requestId = identities.getString(this.resetRequestPath(token) + ".request_id", "");
        return requestId.isBlank() ? "" : this.reference(token, requestId);
    }

    boolean grantExists(FileConfiguration identities, String identityKey) {
        return identities != null && identities.getConfigurationSection(this.resetGrantPath(identityKey)) != null;
    }

    void setGrant(FileConfiguration identities, String identityKey, String name) {
        String path = this.resetGrantPath(identityKey);
        identities.set(path + ".key", identityKey);
        identities.set(path + ".name", name == null ? "Player" : name);
        identities.set(path + ".created", System.currentTimeMillis());
    }

    void clearGrant(FileConfiguration identities, String identityKey) {
        identities.set(this.resetGrantPath(identityKey), null);
    }

    private String reference(String token, String requestId) { return token + REFERENCE_SEPARATOR + requestId; }

    private java.util.Map<String, Object> snapshotRequests(FileConfiguration identities) {
        ConfigurationSection root = identities == null ? null : identities.getConfigurationSection("reset_requests");
        return root == null ? java.util.Map.of() : new java.util.LinkedHashMap<>(root.getValues(true));
    }

    private void restoreRequests(FileConfiguration identities, java.util.Map<String, Object> snapshot) {
        identities.set("reset_requests", null);
        for (java.util.Map.Entry<String, Object> entry : snapshot.entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection)) identities.set("reset_requests." + entry.getKey(), entry.getValue());
        }
    }

    private RequestParts parts(String reference) {
        if (reference == null) return null;
        int separator = reference.lastIndexOf(REFERENCE_SEPARATOR);
        if (separator <= 0 || separator == reference.length() - 1) return null;
        return new RequestParts(reference.substring(0, separator), reference.substring(separator + 1));
    }

    private String resetRequestPath(String token) { return "reset_requests." + token; }
    private String resetGrantPath(String identityKey) { return "reset_grants." + this.identityPathToken(identityKey); }
    private String identityPathToken(String identityKey) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8));
    }

    record CreateResult(String reference, boolean created) { }
    private record RequestParts(String token, String requestId) { }
    interface IdentitySaver { boolean save(); }
}
