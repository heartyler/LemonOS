package dev.lemonos;

import java.util.List;
import java.util.Map;

public final class BackendPlaceRuntimeHarness {
    private enum Server {
        LOBBY("lobby", 30066),
        SURVIVAL("survival", 30067),
        CREATIVE("creative", 30068);

        private final String name;
        private final int defaultPort;

        Server(String name, int defaultPort) {
            this.name = name;
            this.defaultPort = defaultPort;
        }
    }

    public static void main(String[] args) {
        BackendServerPortResolver.Resolution resolution = new BackendServerPortResolver.Resolution(
                Map.of("lobby", 30036, "survival", 30037, "creative", 30038),
                "lobby",
                List.of());
        BackendPlaceRuntimeService<Server> service = new BackendPlaceRuntimeService<Server>(port -> port != 30037);
        Server current = service.configure(resolution, List.of(Server.values()), server -> server.name, server -> server.defaultPort);

        require(current == Server.LOBBY, "Configured identity must select Lobby.");
        require(service.port(Server.LOBBY, -1) == 30036, "Lobby Dev port was not retained.");
        require(service.port(Server.SURVIVAL, -1) == 30037, "Survival port must be configured after current Lobby.");
        require(service.port(Server.CREATIVE, -1) == 30038, "Creative port must be configured after current Lobby.");
        require(service.available(Server.LOBBY, current), "Current server must initialize available.");
        require(!service.available(Server.CREATIVE, current), "Remote server must initialize unavailable before probing.");

        service.refresh(List.of(Server.values()), current);
        require(service.available(Server.LOBBY, current), "Current server must remain available.");
        require(!service.available(Server.SURVIVAL, current), "Unreachable destination must remain unavailable.");
        require(service.available(Server.CREATIVE, current), "Reachable destination must become available.");
        require(service.canConnect(Server.CREATIVE), "Wake travel must use the same resolved port map.");

        boolean rejected = false;
        try {
            service.configure(
                    new BackendServerPortResolver.Resolution(resolution.ports(), "unknown", List.of()),
                    List.of(Server.values()),
                    server -> server.name,
                    server -> server.defaultPort);
        }
        catch (IllegalArgumentException exception) {
            rejected = true;
        }
        require(rejected, "Unknown resolved identity must be rejected.");
        System.out.println("Backend place runtime harness passed.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
