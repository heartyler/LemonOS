package dev.lemonos;

import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

/** Owns the Survival-only ambient Atmosphere scheduler. */
final class BackendAtmosphereLifecycleService extends BackendRepeatingLifecycleService {
    BackendAtmosphereLifecycleService(Plugin plugin, Consumer<RuntimeException> errorHandler) {
        super(plugin, errorHandler);
    }
}
