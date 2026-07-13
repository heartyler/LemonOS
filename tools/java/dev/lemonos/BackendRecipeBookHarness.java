package dev.lemonos;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

public final class BackendRecipeBookHarness {
    private BackendRecipeBookHarness() {
    }

    public static void main(String[] args) {
        verifyServerPolicy();
        verifyUniqueBatchDiscovery();
        System.out.println("Backend Recipe Book behavior harness passed.");
    }

    private static void verifyServerPolicy() {
        BackendRecipeBookConfig defaults = new BackendRecipeBookConfig(null);
        require(defaults.unlockAll("survival"), "Survival must default to enabled.");
        require(defaults.unlockAll("creative"), "Creative must default to enabled.");
        require(!defaults.unlockAll("lobby"), "Lobby must remain disabled.");
        require(!defaults.unlockAll(null), "A missing server identity must fail closed.");

        YamlConfiguration source = new YamlConfiguration();
        source.set("recipe-book.unlock-all.survival", false);
        source.set("recipe-book.unlock-all.creative", "invalid");
        BackendRecipeBookConfig configured = new BackendRecipeBookConfig(source);
        require(!configured.unlockAll("survival"), "Explicit false must disable Survival discovery.");
        require(!configured.unlockAll("creative"), "A non-boolean Creative policy must fail closed.");
    }

    @SuppressWarnings("unchecked")
    private static void verifyUniqueBatchDiscovery() {
        NamespacedKey first = NamespacedKey.fromString("minecraft:stone");
        NamespacedKey second = NamespacedKey.fromString("minecraft:crafting_table");
        Recipe firstRecipe = keyedRecipe(first);
        Recipe duplicate = keyedRecipe(first);
        Recipe secondRecipe = keyedRecipe(second);
        Recipe unkeyed = (Recipe)Proxy.newProxyInstance(
                BackendRecipeBookHarness.class.getClassLoader(), new Class<?>[]{Recipe.class},
                (proxy, method, arguments) -> defaultValue(method.getReturnType()));

        AtomicInteger calls = new AtomicInteger();
        AtomicReference<Collection<NamespacedKey>> discovered = new AtomicReference<>();
        Player player = (Player)Proxy.newProxyInstance(
                BackendRecipeBookHarness.class.getClassLoader(), new Class<?>[]{Player.class},
                (proxy, method, arguments) -> {
                    if ("discoverRecipes".equals(method.getName())) {
                        calls.incrementAndGet();
                        Collection<NamespacedKey> keys = (Collection<NamespacedKey>)arguments[0];
                        discovered.set(List.copyOf(keys));
                        return keys.size();
                    }
                    return defaultValue(method.getReturnType());
                });

        BackendRecipeUnlockService service = new BackendRecipeUnlockService();
        int unlocked = service.unlockAll(player, Arrays.asList(firstRecipe, duplicate, null, unkeyed, secondRecipe).iterator());
        require(unlocked == 2, "Discovery result must report unique keyed recipes.");
        require(calls.get() == 1, "Recipe discovery must use one player update.");
        require(discovered.get().equals(List.of(first, second)), "Recipe keys must be unique and stable.");
        require(service.unlockAll(null, List.of(firstRecipe).iterator()) == 0, "A missing player must be ignored.");
        require(service.unlockAll(player, null) == 0, "A missing iterator must be ignored.");
    }

    private static Recipe keyedRecipe(NamespacedKey key) {
        return (Recipe)Proxy.newProxyInstance(
                BackendRecipeBookHarness.class.getClassLoader(), new Class<?>[]{Recipe.class, Keyed.class},
                (proxy, method, arguments) -> "getKey".equals(method.getName()) ? key : defaultValue(method.getReturnType()));
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte)0;
        if (type == short.class) return (short)0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0F;
        if (type == double.class) return 0.0D;
        return null;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
