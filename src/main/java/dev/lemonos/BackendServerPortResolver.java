package dev.lemonos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Resolves backend identity and probe ports from the owning Honey runtime. */
final class BackendServerPortResolver {
    private static final Pattern SERVER = Pattern.compile("^  ([A-Za-z0-9_-]+):\\s*$");
    private static final Pattern PORT = Pattern.compile("^    port:\\s*([0-9]+)\\s*(?:#.*)?$");
    private static final Map<String, Integer> DEFAULT_PORTS = Map.of(
            "lobby", 30066,
            "survival", 30067,
            "creative", 30068);

    Resolution resolve(File runtimeRoot, int currentPort) {
        LinkedHashMap<String, Integer> ports = new LinkedHashMap<String, Integer>(DEFAULT_PORTS);
        ArrayList<String> diagnostics = new ArrayList<String>();
        File config = runtimeRoot == null ? null : new File(runtimeRoot, "honey.yml");
        if (config != null && config.isFile()) {
            this.loadConfiguredPorts(config, ports, diagnostics);
        }

        String currentServer = null;
        for (Map.Entry<String, Integer> entry : ports.entrySet()) {
            if (entry.getValue() != currentPort) continue;
            if (currentServer != null) {
                diagnostics.add("Duplicate backend port " + currentPort + " maps to both " + currentServer + " and " + entry.getKey() + ".");
                currentServer = null;
                break;
            }
            currentServer = entry.getKey();
        }
        if (currentServer == null && diagnostics.stream().noneMatch(message -> message.startsWith("Duplicate backend port"))) {
            diagnostics.add("Backend port " + currentPort + " does not match the Honey runtime port map " + ports + ".");
        }
        return new Resolution(Map.copyOf(ports), currentServer, List.copyOf(diagnostics));
    }

    private void loadConfiguredPorts(File config, Map<String, Integer> ports, List<String> diagnostics) {
        try {
            boolean inServers = false;
            String server = null;
            for (String line : Files.readAllLines(config.toPath())) {
                if (line.matches("^servers:\\s*(?:#.*)?$")) {
                    inServers = true;
                    server = null;
                    continue;
                }
                if (!inServers) continue;
                if (!line.isBlank() && !Character.isWhitespace(line.charAt(0))) break;
                Matcher serverMatcher = SERVER.matcher(line);
                if (serverMatcher.matches()) {
                    server = serverMatcher.group(1);
                    continue;
                }
                Matcher portMatcher = PORT.matcher(line);
                if (server == null || !DEFAULT_PORTS.containsKey(server) || !portMatcher.matches()) continue;
                int port = Integer.parseInt(portMatcher.group(1));
                if (port < 1 || port > 65535) {
                    diagnostics.add("Ignoring invalid Honey port " + port + " for " + server + ".");
                    continue;
                }
                ports.put(server, port);
            }
        }
        catch (IOException | NumberFormatException exception) {
            diagnostics.add("Unable to read Honey backend ports from " + config + ": " + exception.getMessage());
        }
    }

    record Resolution(Map<String, Integer> ports, String currentServer, List<String> diagnostics) {
    }
}
