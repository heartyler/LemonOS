/*
 * Backend-side LemonOS identity transfer persistence.
 */
package dev.lemonos;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

final class BackendIdentityTransferService {
    private static final long TRANSFER_TTL_MILLIS = 45000L;

    void saveTransfer(FileConfiguration identities, Player player, String identityKey, String targetProxyName, IdentitySaver identitySaver) {
        String path = this.identityTransferPath(identityKey);
        identities.set(path + ".until", (Object)(System.currentTimeMillis() + TRANSFER_TTL_MILLIS));
        identities.set(path + ".target", (Object)targetProxyName);
        identities.set(path + ".address", (Object)this.playerAddress(player));
        identitySaver.save();
    }

    boolean acceptTransfer(FileConfiguration identities, Player player, String identityKey, String currentProxyName, IdentitySaver identitySaver) {
        String path = this.identityTransferPath(identityKey);
        long until = identities.getLong(path + ".until", 0L);
        String target = identities.getString(path + ".target", "");
        String address = identities.getString(path + ".address", "");
        String currentAddress = this.playerAddress(player);
        boolean accepted = until > System.currentTimeMillis() && currentProxyName.equals(target) && !address.isBlank() && address.equals(currentAddress);
        if (accepted || until <= System.currentTimeMillis()) {
            identities.set(path, null);
            identitySaver.save();
        }
        return accepted;
    }

    void clearTransfer(FileConfiguration identities, String identityKey) {
        identities.set(this.identityTransferPath(identityKey), null);
    }

    private String identityTransferPath(String identityKey) {
        return "transfers." + this.identityPathToken(identityKey);
    }

    private String identityPathToken(String identityKey) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8));
    }

    private String playerAddress(Player player) {
        return player.getAddress() == null ? "" : player.getAddress().getAddress().getHostAddress();
    }

    interface IdentitySaver {
        boolean save();
    }
}
