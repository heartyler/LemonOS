/*
 * Decompiled-compatible LemonOS place runtime probe and launcher.
 */
package dev.lemonos.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import org.slf4j.Logger;

final class PlaceRuntimeProbe {
    private final Path runtimeRoot;
    private final Logger logger;

    PlaceRuntimeProbe(Path runtimeRoot, Logger logger) {
        this.runtimeRoot = runtimeRoot;
        this.logger = logger;
    }

    int port(String place) {
        if ("lobby".equals(place)) {
            return 30066;
        }
        if ("survival".equals(place)) {
            return 30067;
        }
        if ("creative".equals(place)) {
            return 30068;
        }
        return -1;
    }

    boolean canConnect(int port) {
        try (Socket socket = new Socket();) {
            socket.connect(new InetSocketAddress("127.0.0.1", port), 300);
            return true;
        }
        catch (IOException exception) {
            return false;
        }
    }

    boolean startServer(String place) {
        if (this.runtimeRoot == null || this.port(place) <= 0) {
            return false;
        }
        Path serverDirectory = this.runtimeRoot.resolve(place).normalize();
        if (!serverDirectory.startsWith(this.runtimeRoot) || !Files.isDirectory(serverDirectory, new LinkOption[0])) {
            this.logger.warn("LemonOS backend directory missing for {}: {}", (Object)place, (Object)serverDirectory);
            return false;
        }
        Path launcher = this.runtimeRoot.resolve(".honeydock").resolve("launchers").resolve(place + ".bat").normalize();
        if (Files.isRegularFile(launcher, new LinkOption[0])) {
            return this.startHoneyDockLauncher(place, launcher, serverDirectory);
        }
        Path entrypoint = this.runtimeRoot.resolve("honeydock.bat").normalize();
        if (Files.isRegularFile(entrypoint, new LinkOption[0])) {
            return this.startHoneyDockEntrypoint(place, entrypoint);
        }
        this.logger.warn("LemonOS HoneyDock launcher missing for {}: {} or {}", (Object)place, (Object)launcher, (Object)entrypoint);
        return false;
    }

    private boolean startHoneyDockLauncher(String place, Path launcher, Path serverDirectory) {
        try {
            String command = "Start-Process -FilePath 'cmd.exe' -ArgumentList '/c'," + powerShellLiteral(launcher.toString()) + " -WorkingDirectory " + powerShellLiteral(serverDirectory.toString()) + " -WindowStyle Hidden";
            new ProcessBuilder("powershell.exe", "-NoProfile", "-WindowStyle", "Hidden", "-Command", command).start();
            this.logger.info("LemonOS wake launched service {} with HoneyDock launcher.", (Object)place);
            return true;
        }
        catch (IOException exception) {
            this.logger.warn("Unable to start LemonOS service {}: {}", (Object)place, (Object)exception.getMessage());
            return false;
        }
    }

    private boolean startHoneyDockEntrypoint(String place, Path entrypoint) {
        try {
            String command = "Start-Process -FilePath 'cmd.exe' -ArgumentList '/c'," + powerShellLiteral(entrypoint.getFileName().toString()) + ",'start'," + powerShellLiteral(place) + " -WorkingDirectory " + powerShellLiteral(entrypoint.getParent().toString()) + " -WindowStyle Hidden";
            new ProcessBuilder("powershell.exe", "-NoProfile", "-WindowStyle", "Hidden", "-Command", command).start();
            this.logger.info("LemonOS wake launched service {} with HoneyDock entrypoint.", (Object)place);
            return true;
        }
        catch (IOException exception) {
            this.logger.warn("Unable to start LemonOS service {}: {}", (Object)place, (Object)exception.getMessage());
            return false;
        }
    }

    private static String powerShellLiteral(String string) {
        return "'" + string.replace("'", "''") + "'";
    }
}
