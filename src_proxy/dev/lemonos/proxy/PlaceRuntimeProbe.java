/*
 * Decompiled-compatible LemonOS place runtime probe and launcher.
 */
package dev.lemonos.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

final class PlaceRuntimeProbe {
    private static final Set<String> SUPPORTED_PLACES = Set.of("lobby", "survival", "creative");
    private final Path runtimeRoot;
    private final Logger logger;
    private final AddressResolver addressResolver;
    private final Set<String> reportedResolutionFailures = ConcurrentHashMap.newKeySet();

    PlaceRuntimeProbe(Path runtimeRoot, Logger logger, AddressResolver addressResolver) {
        this.runtimeRoot = runtimeRoot == null ? null : runtimeRoot.toAbsolutePath().normalize();
        this.logger = logger;
        this.addressResolver = addressResolver;
    }

    int port(String place) {
        InetSocketAddress address = this.resolveAddress(place);
        return address == null ? -1 : address.getPort();
    }

    boolean canConnect(String place) {
        InetSocketAddress address = this.resolveAddress(place);
        return address != null && canConnect(address);
    }

    private static boolean canConnect(InetSocketAddress address) {
        try (Socket socket = new Socket()) {
            socket.connect(address, 300);
            return true;
        }
        catch (IOException | SecurityException exception) {
            return false;
        }
    }

    boolean startServer(String place) {
        String normalizedPlace = normalizePlace(place);
        if (this.runtimeRoot == null || this.port(normalizedPlace) <= 0) {
            return false;
        }
        Path serverDirectory = this.runtimeRoot.resolve(normalizedPlace).normalize();
        if (!serverDirectory.startsWith(this.runtimeRoot) || !Files.isDirectory(serverDirectory, new LinkOption[0])) {
            this.warn("LemonOS backend directory missing for {}: {}", normalizedPlace, serverDirectory);
            return false;
        }
        Path launcher = this.runtimeRoot.resolve(".honeydock").resolve("launchers").resolve(normalizedPlace + ".bat").normalize();
        if (Files.isRegularFile(launcher, new LinkOption[0])) {
            return this.startHoneyDockLauncher(normalizedPlace, launcher, serverDirectory);
        }
        Path entrypoint = this.runtimeRoot.resolve("honeydock.bat").normalize();
        if (Files.isRegularFile(entrypoint, new LinkOption[0])) {
            return this.startHoneyDockEntrypoint(normalizedPlace, entrypoint);
        }
        this.warn("LemonOS HoneyDock launcher missing for {}: {} or {}", normalizedPlace, launcher, entrypoint);
        return false;
    }

    private InetSocketAddress resolveAddress(String place) {
        String normalizedPlace = normalizePlace(place);
        if (!SUPPORTED_PLACES.contains(normalizedPlace)) {
            this.reportResolutionFailure(normalizedPlace, "unsupported place");
            return null;
        }
        if (this.addressResolver == null) {
            this.reportResolutionFailure(normalizedPlace, "address resolver is unavailable");
            return null;
        }
        InetSocketAddress address;
        try {
            address = this.addressResolver.resolve(normalizedPlace);
        }
        catch (RuntimeException exception) {
            this.reportResolutionFailure(normalizedPlace, "Velocity server lookup failed: " + safeMessage(exception));
            return null;
        }
        if (address == null) {
            this.reportResolutionFailure(normalizedPlace, "server is not registered in Velocity");
            return null;
        }
        if (address.getPort() <= 0 || address.getPort() > 65535) {
            this.reportResolutionFailure(normalizedPlace, "registered port is invalid");
            return null;
        }
        if (!isLoopback(address)) {
            this.reportResolutionFailure(normalizedPlace, "registered address is not loopback: " + address.getHostString());
            return null;
        }
        this.reportedResolutionFailures.remove(normalizedPlace);
        return address;
    }

    private static boolean isLoopback(InetSocketAddress address) {
        InetAddress resolved = address.getAddress();
        if (resolved != null) {
            return resolved.isLoopbackAddress();
        }
        String host = address.getHostString();
        return "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host) || "[::1]".equals(host);
    }

    private void reportResolutionFailure(String place, String reason) {
        String key = place.isBlank() ? "<blank>" : place;
        if (this.reportedResolutionFailures.add(key)) {
            this.warn("LemonOS place probe disabled for {}: {}.", key, reason);
        }
    }

    private static String normalizePlace(String place) {
        return place == null ? "" : place.trim().toLowerCase(Locale.ROOT);
    }

    private static String safeMessage(Throwable exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
    }

    private boolean startHoneyDockLauncher(String place, Path launcher, Path serverDirectory) {
        try {
            String command = "Start-Process -FilePath 'cmd.exe' -ArgumentList '/c'," + powerShellLiteral(launcher.toString()) + " -WorkingDirectory " + powerShellLiteral(serverDirectory.toString()) + " -WindowStyle Hidden";
            new ProcessBuilder("powershell.exe", "-NoProfile", "-WindowStyle", "Hidden", "-Command", command).start();
            this.info("LemonOS wake launched service {} with HoneyDock launcher.", place);
            return true;
        }
        catch (IOException | SecurityException exception) {
            this.warn("Unable to start LemonOS service {}: {}", place, safeMessage(exception));
            return false;
        }
    }

    private boolean startHoneyDockEntrypoint(String place, Path entrypoint) {
        try {
            String command = "Start-Process -FilePath 'cmd.exe' -ArgumentList '/c'," + powerShellLiteral(entrypoint.getFileName().toString()) + ",'start'," + powerShellLiteral(place) + " -WorkingDirectory " + powerShellLiteral(entrypoint.getParent().toString()) + " -WindowStyle Hidden";
            new ProcessBuilder("powershell.exe", "-NoProfile", "-WindowStyle", "Hidden", "-Command", command).start();
            this.info("LemonOS wake launched service {} with HoneyDock entrypoint.", place);
            return true;
        }
        catch (IOException | SecurityException exception) {
            this.warn("Unable to start LemonOS service {}: {}", place, safeMessage(exception));
            return false;
        }
    }

    private void info(String message, Object argument) {
        if (this.logger != null) {
            this.logger.info(message, argument);
        }
    }

    private void warn(String message, Object ... arguments) {
        if (this.logger != null) {
            this.logger.warn(message, arguments);
        }
    }

    private static String powerShellLiteral(String string) {
        return "'" + string.replace("'", "''") + "'";
    }

    interface AddressResolver {
        InetSocketAddress resolve(String place);
    }
}
