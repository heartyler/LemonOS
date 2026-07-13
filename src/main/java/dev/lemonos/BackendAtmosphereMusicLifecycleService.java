package dev.lemonos;

import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

/** Owns the Java Lobby music scheduler independently of Survival Atmosphere. */
final class BackendAtmosphereMusicLifecycleService extends BackendRepeatingLifecycleService {
    BackendAtmosphereMusicLifecycleService(Plugin plugin, Consumer<RuntimeException> errorHandler) {
        super(plugin, errorHandler);
    }
}
