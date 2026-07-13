package dev.lemonos;

import java.nio.file.Files;
import java.nio.file.Path;

public final class BackendServerPortResolverHarness {
    public static void main(String[] args) throws Exception {
        BackendServerPortResolver resolver = new BackendServerPortResolver();
        Path production = Files.createTempDirectory("lemonos-port-production");
        BackendServerPortResolver.Resolution fallback = resolver.resolve(production.toFile(), 30067);
        require("survival".equals(fallback.currentServer()), "Production fallback must identify Survival.");
        require(fallback.ports().get("creative") == 30068, "Production fallback port changed.");

        Path development = Files.createTempDirectory("lemonos-port-development");
        Files.writeString(development.resolve("honey.yml"), """
                servers:
                  velocity:
                    port: 25575
                  lobby:
                    port: 30036
                  survival:
                    port: 30037
                  creative:
                    port: 30038
                order:
                  start:
                    - velocity
                """);
        BackendServerPortResolver.Resolution lobby = resolver.resolve(development.toFile(), 30036);
        BackendServerPortResolver.Resolution survival = resolver.resolve(development.toFile(), 30037);
        BackendServerPortResolver.Resolution creative = resolver.resolve(development.toFile(), 30038);
        require("lobby".equals(lobby.currentServer()), "Dev Lobby port was not resolved.");
        require("survival".equals(survival.currentServer()), "Dev Survival port was not resolved.");
        require("creative".equals(creative.currentServer()), "Dev Creative port was not resolved.");
        require(creative.ports().get("lobby") == 30036, "Dev port map was not retained for probes.");

        BackendServerPortResolver.Resolution unknown = resolver.resolve(development.toFile(), 39999);
        require(unknown.currentServer() == null, "Unknown port must not fall back to Lobby.");
        require(!unknown.diagnostics().isEmpty(), "Unknown port must produce a diagnostic.");

        Files.writeString(development.resolve("honey.yml"), """
                servers:
                  lobby:
                    port: 30036
                  survival:
                    port: 30036
                  creative:
                    port: 30038
                """);
        BackendServerPortResolver.Resolution duplicate = resolver.resolve(development.toFile(), 30036);
        require(duplicate.currentServer() == null, "Duplicate current port must be rejected.");

        System.out.println("Backend server port resolver harness passed.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
