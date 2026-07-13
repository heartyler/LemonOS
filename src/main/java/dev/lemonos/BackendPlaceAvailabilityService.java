/*
 * Backend-side LemonOS place availability orchestration.
 */
package dev.lemonos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendPlaceAvailabilityService<S> {
    private static final int CONNECT_TIMEOUT_MILLIS = 300;
    private final BackendPlaceRuntimeStatusService placeRuntimeStatusService;

    BackendPlaceAvailabilityService(BackendPlaceRuntimeStatusService placeRuntimeStatusService) {
        this.placeRuntimeStatusService = placeRuntimeStatusService;
    }

    void initialize(Map<S, Boolean> availability, Iterable<S> servers, S currentServer) {
        for (S server : servers) {
            availability.put(server, Objects.equals(server, currentServer));
        }
    }

    void refresh(Map<S, Boolean> availability, Iterable<S> servers, S currentServer, ToIntFunction<S> port) {
        for (S server : servers) {
            if (Objects.equals(server, currentServer)) {
                availability.put(server, true);
                continue;
            }
            availability.put(server, this.canConnect(port.applyAsInt(server)));
        }
    }

    boolean available(Map<S, Boolean> availability, S server, S currentServer) {
        return availability.getOrDefault((Object)server, Objects.equals(server, currentServer));
    }

    boolean ready(FileConfiguration places, S server, Function<S, String> proxyName) {
        return !this.closed(places, server, proxyName);
    }

    boolean closed(FileConfiguration places, S server, Function<S, String> proxyName) {
        return this.placeRuntimeStatusService.closed(places, proxyName.apply(server));
    }

    boolean wakeable(FileConfiguration places, S server, Function<S, String> proxyName) {
        return this.placeRuntimeStatusService.wakeable(places, proxyName.apply(server));
    }

    boolean canConnect(int port) {
        try (Socket socket = new Socket();){
            socket.connect(new InetSocketAddress("127.0.0.1", port), CONNECT_TIMEOUT_MILLIS);
            return true;
        }
        catch (IOException | RuntimeException exception) {
            return false;
        }
    }
}
