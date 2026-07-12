/*
 * Backend-side LemonOS identity session persistence.
 */
package dev.lemonos;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

final class BackendIdentitySessionService {
    void saveSession(FileConfiguration identities, Player player, String identityKey, long sessionMillis, IdentitySaver identitySaver) {
        String path = this.identitySessionPath(identityKey);
        identities.set(path + ".until", (Object)(System.currentTimeMillis() + sessionMillis));
        identities.set(path + ".address", (Object)this.playerAddress(player));
        identitySaver.save();
    }

    boolean acceptSession(FileConfiguration identities, Player player, String identityKey) {
        String path = this.identitySessionPath(identityKey);
        long until = identities.getLong(path + ".until", 0L);
        String address = identities.getString(path + ".address", "");
        return until > System.currentTimeMillis() && !address.isBlank() && address.equals(this.playerAddress(player));
    }

    void clearSession(FileConfiguration identities, String identityKey) {
        identities.set(this.identitySessionPath(identityKey), null);
    }

    private String identitySessionPath(String identityKey) {
        return "sessions." + this.identityPathToken(identityKey);
    }

    private String identityPathToken(String identityKey) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8));
    }

    private String playerAddress(Player player) {
        return player.getAddress() == null || player.getAddress().getAddress() == null ? "" : player.getAddress().getAddress().getHostAddress();
    }

    interface IdentitySaver {
        boolean save();
    }
}
