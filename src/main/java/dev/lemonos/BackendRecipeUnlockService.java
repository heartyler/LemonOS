package dev.lemonos;

import java.util.Iterator;
import java.util.LinkedHashSet;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

/** Collects unique keyed recipes and applies one idempotent player update. */
final class BackendRecipeUnlockService {
    int unlockAll(Player player, Iterator<Recipe> recipes) {
        if (player == null || recipes == null) return 0;
        LinkedHashSet<NamespacedKey> keys = new LinkedHashSet<NamespacedKey>();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            if (recipe instanceof Keyed keyed) {
                NamespacedKey key = keyed.getKey();
                if (key != null) keys.add(key);
            }
        }
        return keys.isEmpty() ? 0 : player.discoverRecipes(keys);
    }
}
