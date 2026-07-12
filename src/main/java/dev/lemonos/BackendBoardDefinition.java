package dev.lemonos;

import java.util.Set;

record BackendBoardDefinition(
        String dataKey,
        String rolePrefix,
        String defaultTitle,
        String defaultSubtitle,
        String defaultBottomLine,
        double defaultX,
        double defaultY,
        double defaultZ,
        Set<String> servers,
        boolean trackBlocksChanged) {

    BackendBoardDefinition {
        servers = Set.copyOf(servers);
    }

    String configPath() {
        return "boards." + this.dataKey;
    }

    boolean enabledOn(String server) {
        return server != null && this.servers.contains(server);
    }
}
