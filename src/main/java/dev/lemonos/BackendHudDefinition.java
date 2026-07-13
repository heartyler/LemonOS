package dev.lemonos;

import java.util.Set;

record BackendHudDefinition(
        String dataKey,
        String rolePrefix,
        String defaultTitle,
        String defaultSubtitle,
        String defaultBottomLine,
        double defaultX,
        double defaultY,
        double defaultZ,
        Set<String> servers,
        boolean trackBlocksChanged,
        RankingSource rankingSource) {

    BackendHudDefinition {
        servers = Set.copyOf(servers);
    }

    String configPath() {
        return "hud." + this.dataKey;
    }

    boolean enabledOn(String server) {
        return server != null && this.servers.contains(server);
    }

    boolean usesPlaytime() {
        return this.rankingSource == RankingSource.PLAYTIME;
    }

    enum RankingSource {
        PLAYTIME,
        HUD_STATISTICS
    }
}
