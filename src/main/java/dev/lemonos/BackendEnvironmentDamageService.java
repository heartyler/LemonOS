package dev.lemonos;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;

final class BackendEnvironmentDamageService {
    boolean isProtectedCause(EntityDamageEvent.DamageCause cause, Material directBlock) {
        if (cause == EntityDamageEvent.DamageCause.CONTACT) {
            return directBlock == Material.MAGMA_BLOCK;
        }
        return cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || cause == EntityDamageEvent.DamageCause.FIRE
                || cause == EntityDamageEvent.DamageCause.FIRE_TICK
                || cause == EntityDamageEvent.DamageCause.LAVA;
    }
}
