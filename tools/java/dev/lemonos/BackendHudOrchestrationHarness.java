package dev.lemonos;

import java.util.List;
import java.util.Set;
import org.bukkit.configuration.file.YamlConfiguration;

public final class BackendHudOrchestrationHarness {
    private BackendHudOrchestrationHarness() {
    }

    public static void main(String[] args) {
        BackendHudDefinition stayedClose = new BackendHudDefinition(
                "stayed-close", "stayed_close_", "Stayclose", "near", "time", 0.0, 0.0, 0.0,
                Set.of("lobby"), false, BackendHudDefinition.RankingSource.PLAYTIME);
        BackendHudDefinition grewHere = new BackendHudDefinition(
                "grew-here", "hud_grew_here_", "Grow", "grows", "crops", 0.0, 0.0, 0.0,
                Set.of("lobby", "survival"), false, BackendHudDefinition.RankingSource.HUD_STATISTICS);
        List<BackendHudDefinition> definitions = List.of(stayedClose, grewHere);
        YamlConfiguration source = new YamlConfiguration();
        source.set("hud.stayed-close.enabled", true);
        source.set("hud.stayed-close.refresh-minutes", 5);
        source.set("hud.grew-here.enabled", true);
        source.set("hud.grew-here.refresh-minutes", 2);
        BackendHudConfig config = new BackendHudConfig(source);
        BackendHudOrchestrationService service = new BackendHudOrchestrationService();

        BackendHudOrchestrationService.Plan lobby = service.plan("lobby", config, definitions);
        require(lobby.active(), "Lobby HUD plan must be active.");
        require(lobby.periodTicks() == 2400L, "HUD plan must use the shortest enabled refresh interval.");
        require(lobby.disabledRolePrefixes().isEmpty(), "Enabled HUD definitions must not be cleared.");
        require(stayedClose.usesPlaytime(), "Stayed Close must use the playtime ranking provider.");
        require(!grewHere.usesPlaytime(), "Metric HUDs must retain HUD statistics ownership.");
        require("hud.stayed-close".equals(stayedClose.configPath()), "Stayed Close must use the canonical HUD path.");

        source.set("hud.grew-here.enabled", false);
        BackendHudOrchestrationService.Plan survival = service.plan("survival", config, definitions);
        require(!survival.active(), "An inapplicable Stayed Close HUD must not activate Survival scheduling.");
        require(survival.disabledRolePrefixes().equals(List.of("hud_grew_here_")), "Disabled applicable HUD must be cleared.");
        System.out.println("Backend HUD orchestration harness passed.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
