package dev.lemonos;

import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

/** Owns the single repeating scheduler for all LemonOS HUD displays. */
final class BackendHudLifecycleService extends BackendRepeatingLifecycleService {
    BackendHudLifecycleService(Plugin plugin, Consumer<RuntimeException> errorHandler) {
        super(plugin, errorHandler);
    }
}
