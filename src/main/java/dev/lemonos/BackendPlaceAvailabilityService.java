/*
 * Backend-side LemonOS place availability orchestration.
 */
package dev.lemonos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Function;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendPlaceAvailabilityService<S> {
    private static final int CONNECT_TIMEOUT_MILLIS = 300;
    private final BackendPlaceRuntimeStatusService placeRuntimeStatusService;

    BackendPlaceAvailabilityService(BackendPlaceRuntimeStatusService placeRuntimeStatusService) {
        this.placeRuntimeStatusService = placeRuntimeStatusService;
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
