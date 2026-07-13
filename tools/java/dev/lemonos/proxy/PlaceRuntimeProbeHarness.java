package dev.lemonos.proxy;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class PlaceRuntimeProbeHarness {
    public static void main(String[] args) throws Exception {
        Path runtimeRoot = Files.createTempDirectory("lemonos-place-probe-");
        try {
            Map<String, InetSocketAddress> targets = new HashMap<String, InetSocketAddress>();
            PlaceRuntimeProbe probe = new PlaceRuntimeProbe(runtimeRoot, null, targets::get);

            targets.put("lobby", new InetSocketAddress("127.0.0.1", 30036));
            targets.put("survival", new InetSocketAddress("127.0.0.1", 30037));
            targets.put("creative", new InetSocketAddress("127.0.0.1", 30038));
            require(probe.port("LOBBY") == 30036, "Dev Lobby port was not resolved from the registry.");
            require(probe.port(" survival ") == 30037, "Dev Survival port was not resolved from the registry.");
            require(probe.port("creative") == 30038, "Dev Creative port was not resolved from the registry.");

            targets.put("lobby", new InetSocketAddress("127.0.0.1", 30066));
            targets.put("survival", new InetSocketAddress("127.0.0.1", 30067));
            targets.put("creative", new InetSocketAddress("127.0.0.1", 30068));
            require(probe.port("lobby") == 30066, "Alternate Lobby port was not preserved.");
            require(probe.port("survival") == 30067, "Alternate Survival port was not preserved.");
            require(probe.port("creative") == 30068, "Alternate Creative port was not preserved.");

            targets.put("lobby", InetSocketAddress.createUnresolved("localhost", 31036));
            require(probe.port("lobby") == 31036, "Unresolved localhost target was rejected.");
            targets.put("lobby", InetSocketAddress.createUnresolved("example.invalid", 31036));
            require(probe.port("lobby") == -1, "Non-loopback unresolved target was accepted.");
            targets.put("lobby", InetSocketAddress.createUnresolved("127.example.invalid", 31036));
            require(probe.port("lobby") == -1, "Loopback-looking hostname was accepted.");
            targets.put("lobby", new InetSocketAddress("192.0.2.1", 31036));
            require(probe.port("lobby") == -1, "Non-loopback resolved target was accepted.");
            targets.remove("lobby");
            require(probe.port("lobby") == -1, "Missing Velocity registration was accepted.");
            require(probe.port("unknown") == -1, "Unsupported place was accepted.");
            try (ServerSocket listener = new ServerSocket()) {
                listener.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                targets.put("creative", (InetSocketAddress)listener.getLocalSocketAddress());
                require(probe.canConnect("creative"), "Registered loopback listener was not reachable.");
            }

            PlaceRuntimeProbe failingResolver = new PlaceRuntimeProbe(runtimeRoot, null, place -> {
                throw new IllegalStateException("lookup failed");
            });
            require(failingResolver.port("lobby") == -1, "Resolver failure did not fail closed.");
            PlaceRuntimeProbe missingResolver = new PlaceRuntimeProbe(runtimeRoot, null, null);
            require(missingResolver.port("lobby") == -1, "Missing resolver did not fail closed.");
            require(!probe.startServer("../lobby"), "Unsafe runtime directory traversal was accepted.");
        } finally {
            try (var paths = Files.walk(runtimeRoot)) {
                paths.sorted((left, right) -> right.compareTo(left)).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    }
                    catch (Exception ignored) {
                    }
                });
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
