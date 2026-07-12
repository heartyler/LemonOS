/*
 * Decompiled-compatible LemonOS Stayed Close playtime storage.
 */
package dev.lemonos.proxy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;

final class PlaytimeRepository {
    private final Path playtimeFile;
    private final Logger logger;
    private final Map<UUID, PlaytimeEntry> playtime = new LinkedHashMap<UUID, PlaytimeEntry>();
    private final Map<UUID, Long> activePlaytimeSinceMillis = new LinkedHashMap<UUID, Long>();

    PlaytimeRepository(Path playtimeFile, Logger logger) {
        this.playtimeFile = playtimeFile;
        this.logger = logger;
    }

    static String defaultFile() {
        return "version: \"1.0\"\nupdated: 0\nplayers: {}\n";
    }

    synchronized void load() {
        this.playtime.clear();
        if (!Files.isRegularFile(this.playtimeFile, new LinkOption[0])) {
            return;
        }
        try {
            UUID currentPlayer = null;
            for (String line : Files.readAllLines(this.playtimeFile, StandardCharsets.UTF_8)) {
                String stripped = line.trim();
                if (stripped.isEmpty() || stripped.startsWith("#") || "players:".equals(stripped) || "players: {}".equals(stripped)) {
                    continue;
                }
                if (line.startsWith("  ") && !line.startsWith("    ") && stripped.endsWith(":")) {
                    try {
                        currentPlayer = UUID.fromString(stripped.substring(0, stripped.length() - 1));
                        this.playtime.putIfAbsent(currentPlayer, new PlaytimeEntry());
                    }
                    catch (IllegalArgumentException exception) {
                        currentPlayer = null;
                    }
                    continue;
                }
                if (currentPlayer == null || !line.startsWith("    ")) {
                    continue;
                }
                PlaytimeEntry entry = this.playtime.get(currentPlayer);
                int separator = stripped.indexOf(':');
                if (separator <= 0) {
                    continue;
                }
                String key = stripped.substring(0, separator).trim();
                String value = stripped.substring(separator + 1).trim().replace("\"", "");
                if ("name".equals(key)) {
                    entry.name = value;
                } else if ("total-seconds".equals(key)) {
                    entry.totalSeconds = parseLong(value, 0L);
                } else if ("last-seen".equals(key)) {
                    entry.lastSeenMillis = parseLong(value, 0L);
                }
            }
        }
        catch (IOException exception) {
            this.logger.debug("Unable to load LemonOS playtime state.", (Throwable)exception);
        }
    }

    synchronized void sync(List<PlayerSnapshot> onlinePlayers) {
        long now = System.currentTimeMillis();
        Set<UUID> activePlayers = new HashSet<UUID>();
        for (PlayerSnapshot player : onlinePlayers) {
            UUID uuid = player.uuid();
            activePlayers.add(uuid);
            PlaytimeEntry entry = this.playtime.computeIfAbsent(uuid, ignored -> new PlaytimeEntry());
            entry.name = player.name();
            entry.lastSeenMillis = now;
            Long activeSince = this.activePlaytimeSinceMillis.putIfAbsent(uuid, now);
            if (activeSince == null) {
                continue;
            }
            long elapsedSeconds = Math.max(0L, (now - activeSince) / 1000L);
            if (elapsedSeconds > 0L) {
                entry.totalSeconds += elapsedSeconds;
                this.activePlaytimeSinceMillis.put(uuid, now);
            }
        }
        for (UUID uuid : new ArrayList<UUID>(this.activePlaytimeSinceMillis.keySet())) {
            if (activePlayers.contains(uuid)) {
                continue;
            }
            PlaytimeEntry entry = this.playtime.get(uuid);
            Long activeSince = this.activePlaytimeSinceMillis.remove(uuid);
            if (entry == null || activeSince == null) {
                continue;
            }
            long elapsedSeconds = Math.max(0L, (now - activeSince) / 1000L);
            if (elapsedSeconds > 0L) {
                entry.totalSeconds += elapsedSeconds;
            }
            entry.lastSeenMillis = now;
        }
    }

    synchronized void save() throws IOException {
        this.writeStringAtomic(this.content());
    }

    synchronized void clear() throws IOException {
        this.playtime.clear();
        this.activePlaytimeSinceMillis.clear();
        this.writeStringAtomic(defaultFile());
    }

    synchronized String content() {
        StringBuilder builder = new StringBuilder();
        builder.append("version: \"1.0\"\n");
        builder.append("updated: ").append(System.currentTimeMillis()).append('\n');
        builder.append("players:\n");
        this.playtime.entrySet().stream().sorted((first, second) -> {
            int result = Long.compare(second.getValue().totalSeconds, first.getValue().totalSeconds);
            if (result != 0) {
                return result;
            }
            return first.getValue().name.compareToIgnoreCase(second.getValue().name);
        }).forEach(entry -> {
            PlaytimeEntry playtimeEntry = entry.getValue();
            builder.append("  ").append(entry.getKey()).append(":\n");
            builder.append("    name: ").append(safeYamlValue(playtimeEntry.name)).append('\n');
            builder.append("    total-seconds: ").append(Math.max(0L, playtimeEntry.totalSeconds)).append('\n');
            builder.append("    last-seen: ").append(Math.max(0L, playtimeEntry.lastSeenMillis)).append('\n');
        });
        return builder.toString();
    }

    private static long parseLong(String string, long fallback) {
        try {
            return Long.parseLong(string);
        }
        catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private static String safeYamlValue(String string) {
        if (string == null || string.isBlank()) {
            return "\"\"";
        }
        return "\"" + string.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private void writeStringAtomic(String string) throws IOException {
        Files.createDirectories(this.playtimeFile.getParent(), new FileAttribute[0]);
        Path temporary = this.playtimeFile.resolveSibling(String.valueOf(this.playtimeFile.getFileName()) + ".tmp");
        Files.writeString(temporary, (CharSequence)string, StandardCharsets.UTF_8, new OpenOption[0]);
        try {
            Files.move(temporary, this.playtimeFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (IOException exception) {
            Files.move(temporary, this.playtimeFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static final class PlayerSnapshot {
        private final UUID uuid;
        private final String name;

        PlayerSnapshot(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        UUID uuid() {
            return this.uuid;
        }

        String name() {
            return this.name;
        }
    }

    private static final class PlaytimeEntry {
        private String name = "";
        private long totalSeconds;
        private long lastSeenMillis;
    }
}
