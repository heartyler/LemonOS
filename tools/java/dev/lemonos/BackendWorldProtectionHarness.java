package dev.lemonos;

import org.bukkit.Material;

public final class BackendWorldProtectionHarness {
    public static void main(String[] args) {
        BackendWorldProtectionListener lobby = listener("lobby");
        require(lobby.blocksCreatureSpawns(), "Lobby must block creature spawns");
        require(lobby.protectsEnvironment(), "Lobby must protect the environment");
        require(lobby.blocksFireSpread(Material.FIRE), "Lobby must block fire spread");
        require(!lobby.blocksFireSpread(Material.LAVA), "Lobby fire rule matched non-fire material");
        require(lobby.blocksLavaFlow(Material.LAVA), "Lobby must block lava flow");
        require(!lobby.blocksLavaFlow(Material.WATER), "Lobby lava rule matched non-lava material");

        BackendWorldProtectionListener creative = listener("creative");
        require(!creative.blocksCreatureSpawns(), "Creative creature-spawn behavior changed");
        require(creative.protectsEnvironment(), "Creative must protect the environment");

        BackendWorldProtectionListener survival = listener("survival");
        require(!survival.blocksCreatureSpawns(), "Survival creature-spawn behavior changed");
        require(!survival.protectsEnvironment(), "Survival environment behavior changed");
        require(!survival.blocksFireSpread(Material.FIRE), "Survival fire behavior changed");
        require(!survival.blocksLavaFlow(Material.LAVA), "Survival lava behavior changed");
        System.out.println("Backend World protection harness OK");
    }

    private static BackendWorldProtectionListener listener(String backend) {
        return new BackendWorldProtectionListener(BackendWorldPolicy.forBackend(backend));
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
