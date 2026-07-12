/*
 * Backend-side LemonOS identity skin persistence.
 */
package dev.lemonos;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

final class BackendIdentitySkinService {
    void saveChoice(FileConfiguration skins, Player player, String identityKey, String skinName) {
        if (skins == null) {
            return;
        }
        String path = this.skinPath(identityKey);
        skins.set(path + ".name", (Object)player.getName());
        skins.set(path + ".skin", (Object)skinName);
        skins.set(path + ".property", null);
        skins.set(path + ".updated", (Object)System.currentTimeMillis());
    }

    String savedSkin(FileConfiguration skins, String identityKey) {
        if (skins == null) {
            return "";
        }
        return skins.getString(this.skinPath(identityKey) + ".skin", "");
    }

    private String skinPath(String identityKey) {
        return "skins." + this.identityPathToken(identityKey);
    }

    private String identityPathToken(String identityKey) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8));
    }
}
