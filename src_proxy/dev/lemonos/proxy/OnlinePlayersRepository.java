/*
 * Decompiled-compatible LemonOS online player state writer.
 */
package dev.lemonos.proxy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;

final class OnlinePlayersRepository {
    private final Path onlineFile;
    private final Logger logger;

    OnlinePlayersRepository(Path onlineFile, Logger logger) {
        this.onlineFile = onlineFile;
        this.logger = logger;
    }

    void write(List<PlayerSnapshot> onlinePlayers) {
        try {
            Files.createDirectories(this.onlineFile.getParent(), new FileAttribute[0]);
            StringBuilder builder = new StringBuilder();
            builder.append("updated: ").append(System.currentTimeMillis()).append('\n');
            ArrayList<PlayerSnapshot> sortedPlayers = new ArrayList<PlayerSnapshot>(onlinePlayers);
            sortedPlayers.sort(Comparator.comparing(PlayerSnapshot::name, String.CASE_INSENSITIVE_ORDER));
            for (PlayerSnapshot player : sortedPlayers) {
                String name = normalizeOnlineName(player.name());
                if (!name.matches("\\.?[a-z0-9_]{1,32}")) {
                    continue;
                }
                builder.append("- name: ").append(name).append('\n');
            }
            Files.writeString(this.onlineFile, (CharSequence)builder.toString(), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (IOException exception) {
            this.logger.debug("Unable to write LemonOS online state.", (Throwable)exception);
        }
    }

    private static String normalizeOnlineName(String string) {
        return cleanScalar(string == null ? "" : string).toLowerCase(Locale.ROOT);
    }

    private static String cleanScalar(String string) {
        return string.replace("\"", "").replace("'", "").strip();
    }

    static final class PlayerSnapshot {
        private final String name;

        PlayerSnapshot(String name) {
            this.name = name;
        }

        String name() {
            return this.name;
        }
    }
}
