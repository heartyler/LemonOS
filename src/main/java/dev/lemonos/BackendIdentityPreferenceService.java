/*
 * Backend-side LemonOS identity preference persistence.
 */
package dev.lemonos;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendIdentityPreferenceService {
    boolean cubeeVisible(FileConfiguration identities, String identityKey, String serverProxyName, boolean lobby) {
        if (lobby) {
            return true;
        }
        if (identities == null) {
            return true;
        }
        String path = this.cubeeServerPreferencePath(identityKey, serverProxyName);
        if (identities.isSet(path)) {
            return identities.getBoolean(path, true);
        }
        return identities.getBoolean(this.cubeePreferencePath(identityKey), true);
    }

    void setCubeeVisible(FileConfiguration identities, String identityKey, String serverProxyName, boolean visible) {
        identities.set(this.cubeeServerPreferencePath(identityKey, serverProxyName), (Object)visible);
    }

    private String cubeePreferencePath(String identityKey) {
        return "preferences." + this.identityPathToken(identityKey) + ".cubee.visible";
    }

    private String cubeeServerPreferencePath(String identityKey, String serverProxyName) {
        return "preferences." + this.identityPathToken(identityKey) + ".cubee.visible-by-server." + serverProxyName;
    }

    private String identityPathToken(String identityKey) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8));
    }
}
