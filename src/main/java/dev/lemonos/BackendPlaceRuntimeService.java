package dev.lemonos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.ToIntFunction;

/** Owns the resolved backend identity, ports, and live availability snapshot. */
final class BackendPlaceRuntimeService<S> {
    private final IntPredicate reachability;
    private final Map<S, Integer> ports = new ConcurrentHashMap<S, Integer>();
    private final Map<S, Boolean> availability = new ConcurrentHashMap<S, Boolean>();

    BackendPlaceRuntimeService(IntPredicate reachability) {
        this.reachability = Objects.requireNonNull(reachability, "reachability");
    }

    S configure(
            BackendServerPortResolver.Resolution resolution,
            Iterable<S> servers,
            Function<S, String> proxyName,
            ToIntFunction<S> defaultPort) {
        Objects.requireNonNull(resolution, "resolution");
        ArrayList<S> configuredServers = new ArrayList<S>();
        for (S server : servers) {
            configuredServers.add(server);
        }
        this.ports.clear();
        for (S server : configuredServers) {
            this.ports.put(server, resolution.ports().getOrDefault(proxyName.apply(server), defaultPort.applyAsInt(server)));
        }
        S currentServer = null;
        for (S server : configuredServers) {
            if (proxyName.apply(server).equals(resolution.currentServer())) {
                currentServer = server;
                break;
            }
        }
        if (currentServer == null) {
            throw new IllegalArgumentException("Resolved backend identity is not configured: " + resolution.currentServer());
        }
        this.availability.clear();
        for (S server : configuredServers) {
            this.availability.put(server, Objects.equals(server, currentServer));
        }
        return currentServer;
    }

    void refresh(Iterable<S> servers, S currentServer) {
        for (S server : servers) {
            this.availability.put(server, Objects.equals(server, currentServer) || this.canConnect(server));
        }
    }

    boolean available(S server, S currentServer) {
        return this.availability.getOrDefault(server, Objects.equals(server, currentServer));
    }

    boolean canConnect(S server) {
        Integer port = this.ports.get(server);
        return port != null && this.reachability.test(port);
    }

    int port(S server, int fallback) {
        return this.ports.getOrDefault(server, fallback);
    }

    Map<S, Integer> ports() {
        return Map.copyOf(this.ports);
    }

    Map<S, Boolean> availability() {
        return Map.copyOf(this.availability);
    }
}
