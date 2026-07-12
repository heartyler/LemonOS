package dev.lemonos;

import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

/** Owns the single repeating scheduler for all LemonOS display boards. */
final class BackendBoardLifecycleService extends BackendRepeatingLifecycleService {
    BackendBoardLifecycleService(Plugin plugin, Consumer<RuntimeException> errorHandler) {
        super(plugin, errorHandler);
    }
}
