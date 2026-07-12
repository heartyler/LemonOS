package dev.lemonos;

/** Locked world behavior for one Honey backend. */
record BackendWorldPolicy(boolean blockCreatureSpawns, boolean disableFireTick,
        boolean protectEnvironment, boolean protectFallDamage, boolean protectPlayerPvp) {
    static BackendWorldPolicy forBackend(String backend) {
        return switch (backend) {
            case "lobby" -> new BackendWorldPolicy(true, true, true, true, true);
            case "creative" -> new BackendWorldPolicy(false, true, true, true, true);
            case "survival" -> new BackendWorldPolicy(false, false, false, false, false);
            default -> throw new IllegalArgumentException("Unknown backend: " + backend);
        };
    }
}
