package dev.lemonos;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;

public final class BackendEnvironmentDamageHarness {
    public static void main(String[] args) {
        BackendEnvironmentDamageService service = new BackendEnvironmentDamageService();
        require(service.isProtectedCause(EntityDamageEvent.DamageCause.CONTACT, Material.MAGMA_BLOCK),
                "magma contact is no longer protected");
        require(!service.isProtectedCause(EntityDamageEvent.DamageCause.CONTACT, Material.CACTUS),
                "CONTACT migration broadened protection to cactus");
        require(!service.isProtectedCause(EntityDamageEvent.DamageCause.CONTACT, null),
                "CONTACT without a direct block was protected");
        for (EntityDamageEvent.DamageCause cause : new EntityDamageEvent.DamageCause[] {
                EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
                EntityDamageEvent.DamageCause.FIRE,
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.LAVA }) {
            require(service.isProtectedCause(cause, null), "environment cause changed: " + cause);
        }
        require(!service.isProtectedCause(EntityDamageEvent.DamageCause.FALL, null), "fall damage was reclassified");
        System.out.println("Backend Environment damage harness OK");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
