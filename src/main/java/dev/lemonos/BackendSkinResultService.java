/*
 * Backend-side LemonOS skin result behavior.
 */
package dev.lemonos;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

final class BackendSkinResultService {
    private final SkinChoiceSaver skinChoiceSaver;

    BackendSkinResultService(SkinChoiceSaver skinChoiceSaver) {
        this.skinChoiceSaver = skinChoiceSaver;
    }

    void handleSkinResult(UUID uuid, String skinName, String result, boolean notify) {
        Player player = Bukkit.getPlayer((UUID)uuid);
        if (player == null || !player.isOnline()) {
            return;
        }
        if (BackendAdminProtocol.RESULT_SAVED.equals(result)) {
            if (notify) {
                this.skinChoiceSaver.save(player, skinName);
                player.sendMessage((Component)Component.text((String)"saved.", (TextColor)NamedTextColor.GRAY));
            }
            return;
        }
        if (!notify) {
            return;
        }
        if (BackendAdminProtocol.RESULT_TRY_AGAIN.equals(result)) {
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
    }

    interface SkinChoiceSaver {
        void save(Player player, String skinName);
    }
}
