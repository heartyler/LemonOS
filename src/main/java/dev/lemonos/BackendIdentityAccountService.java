/*
 * Backend-side LemonOS identity account/passcode persistence.
 */
package dev.lemonos;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendIdentityAccountService {
    boolean registered(FileConfiguration identities, String identityKey) {
        if (identities == null) {
            return false;
        }
        String path = this.identityDataPath(identities, identityKey);
        return !identities.getString(path + ".salt", "").isBlank() && !identities.getString(path + ".hash", "").isBlank() && identities.getBoolean(path + ".passcode", false);
    }

    void savePasscode(FileConfiguration identities, String identityKey, String name, String passcode) {
        String path = this.identityPath(identityKey);
        identities.set(path + ".name", (Object)name);
        identities.set(path + ".platform", (Object)(identityKey.startsWith("bedrock:") ? "bedrock" : "java"));
        identities.set(path + ".salt", (Object)this.randomToken());
        identities.set(path + ".hash", (Object)this.hashPasscode(passcode, identities.getString(path + ".salt", "")));
        identities.set(path + ".created", (Object)System.currentTimeMillis());
        identities.set(path + ".key", (Object)identityKey);
        identities.set(path + ".passcode", (Object)true);
    }

    boolean verifyPasscode(FileConfiguration identities, String identityKey, String passcode) {
        String path = this.identityDataPath(identities, identityKey);
        String salt = identities.getString(path + ".salt", "");
        String hash = identities.getString(path + ".hash", "");
        return identities.getBoolean(path + ".passcode", false) && !salt.isBlank() && !hash.isBlank() && hash.equals(this.hashPasscode(passcode, salt));
    }

    void removePasscode(FileConfiguration identities, String identityKey) {
        if (identityKey == null || identityKey.isBlank() || identities == null) {
            return;
        }
        identities.set(this.identityPath(identityKey), null);
        String legacyPath = this.legacyIdentityPath(identityKey);
        if (!legacyPath.isBlank()) {
            identities.set(legacyPath, null);
        }
    }

    private String identityPath(String identityKey) {
        return "identities." + this.identityPathToken(identityKey);
    }

    private String identityDataPath(FileConfiguration identities, String identityKey) {
        String path = this.identityPath(identityKey);
        if (this.identityPathReady(identities, path)) {
            return path;
        }
        String legacyPath = this.legacyIdentityPath(identityKey);
        if (!legacyPath.isBlank() && this.identityPathReady(identities, legacyPath)) {
            return legacyPath;
        }
        return path;
    }

    private boolean identityPathReady(FileConfiguration identities, String path) {
        return identities != null && !identities.getString(path + ".salt", "").isBlank() && !identities.getString(path + ".hash", "").isBlank();
    }

    private String legacyIdentityPath(String identityKey) {
        if (!identityKey.startsWith("java:")) {
            return "";
        }
        String legacyName = identityKey.substring("java:".length());
        return legacyName.isBlank() ? "" : "identities." + legacyName;
    }

    private String identityPathToken(String identityKey) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8));
    }

    private String hashPasscode(String passcode, String salt) {
        try {
            PBEKeySpec pBEKeySpec = new PBEKeySpec(passcode.toCharArray(), this.decodeSalt(salt), 65536, 256);
            byte[] bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(pBEKeySpec).getEncoded();
            return Base64.getEncoder().encodeToString(bytes);
        }
        catch (IllegalArgumentException | GeneralSecurityException exception) {
            return "";
        }
    }

    private byte[] decodeSalt(String salt) {
        return Base64.getUrlDecoder().decode(salt);
    }

    private String randomToken() {
        byte[] bytes = new byte[16];
        ThreadLocalRandom.current().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
