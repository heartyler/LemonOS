/*
 * Backend-side LemonOS Stayed Close playtime read model.
 */
package dev.lemonos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

final class BackendStayedClosePlaytimeService {
    List<Rank> top(File playtimeFile, int limit) {
        if (!playtimeFile.isFile()) {
            return List.of();
        }
        FileConfiguration playtime = YamlConfiguration.loadConfiguration((File)playtimeFile);
        ConfigurationSection players = playtime.getConfigurationSection("players");
        if (players == null) {
            return List.of();
        }
        ArrayList<Rank> ranks = new ArrayList<Rank>();
        for (String playerKey : players.getKeys(false)) {
            String name = players.getString(playerKey + ".name", playerKey.length() > 8 ? playerKey.substring(0, 8) : playerKey);
            long totalSeconds = Math.max(0L, players.getLong(playerKey + ".total-seconds", 0L));
            if (name == null || name.isBlank()) {
                name = playerKey.length() > 8 ? playerKey.substring(0, 8) : playerKey;
            }
            ranks.add(new Rank(name, totalSeconds));
        }
        ranks.sort((left, right) -> {
            int totalSecondsOrder = Long.compare(right.totalSeconds, left.totalSeconds);
            if (totalSecondsOrder != 0) {
                return totalSecondsOrder;
            }
            return left.name.compareToIgnoreCase(right.name);
        });
        if (ranks.size() <= limit) {
            return ranks;
        }
        return new ArrayList<Rank>(ranks.subList(0, limit));
    }

    static final class Rank {
        private final String name;
        private final long totalSeconds;

        private Rank(String name, long totalSeconds) {
            this.name = name;
            this.totalSeconds = totalSeconds;
        }

        String name() {
            return this.name;
        }

        long totalSeconds() {
            return this.totalSeconds;
        }
    }
}
