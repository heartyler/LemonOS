/*
 * Backend-side LemonOS HUD data persistence.
 */
package dev.lemonos;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

final class BackendHudDataService {
    boolean ensureDefaults(FileConfiguration hudData, List<String> boardKeys) {
        if (hudData == null) {
            return false;
        }
        boolean changed = false;
        if (!hudData.isString("version")) {
            hudData.set("version", (Object)"1.0");
            changed = true;
        }
        if (!hudData.isLong("updated")) {
            hudData.set("updated", (Object)0L);
            changed = true;
        }
        for (String boardKey : boardKeys) {
            if (!hudData.isConfigurationSection(boardKey + ".players")) {
                hudData.createSection(boardKey + ".players");
                changed = true;
            }
        }
        if (changed) {
            hudData.set("updated", (Object)0L);
        }
        return changed;
    }

    List<Rank> top(FileConfiguration hudData, String boardKey, int limit) {
        if (hudData == null) {
            return List.of();
        }
        ConfigurationSection players = hudData.getConfigurationSection(boardKey + ".players");
        if (players == null) {
            return List.of();
        }
        ArrayList<Rank> ranks = new ArrayList<Rank>();
        for (String playerKey : players.getKeys(false)) {
            String name = players.getString(playerKey + ".name", playerKey.length() > 8 ? playerKey.substring(0, 8) : playerKey);
            long score = Math.max(0L, players.getLong(playerKey + ".score", 0L));
            if (name == null || name.isBlank()) {
                name = playerKey.length() > 8 ? playerKey.substring(0, 8) : playerKey;
            }
            ranks.add(new Rank(name, score));
        }
        ranks.sort((left, right) -> {
            int scoreOrder = Long.compare(right.score, left.score);
            if (scoreOrder != 0) {
                return scoreOrder;
            }
            return left.name.compareToIgnoreCase(right.name);
        });
        if (ranks.size() <= limit) {
            return ranks;
        }
        return new ArrayList<Rank>(ranks.subList(0, limit));
    }

    void recordStat(FileConfiguration hudData, String boardKey, Player player, long scoreDelta, String extraKey, long extraDelta) {
        if (player == null || scoreDelta <= 0L || hudData == null) {
            return;
        }
        String path = boardKey + ".players." + player.getUniqueId();
        hudData.set(path + ".name", (Object)player.getName());
        hudData.set(path + ".score", (Object)Math.max(0L, hudData.getLong(path + ".score", 0L) + scoreDelta));
        if (extraKey != null && !extraKey.isBlank()) {
            hudData.set(path + "." + extraKey, (Object)Math.max(0L, hudData.getLong(path + "." + extraKey, 0L) + Math.max(0L, extraDelta)));
        }
        hudData.set("updated", (Object)System.currentTimeMillis());
    }

    void recordSandboxAction(FileConfiguration hudData, String boardKey, Player player, long blocksChanged, boolean trackBlocksChanged) {
        if (player == null || blocksChanged <= 0L || hudData == null) {
            return;
        }
        String path = boardKey + ".players." + player.getUniqueId();
        hudData.set(path + ".name", (Object)player.getName());
        hudData.set(path + ".score", (Object)Math.max(0L, hudData.getLong(path + ".score", 0L) + 1L));
        hudData.set(path + ".sandbox-actions", (Object)Math.max(0L, hudData.getLong(path + ".sandbox-actions", 0L) + 1L));
        if (trackBlocksChanged) {
            hudData.set(path + ".blocks-changed", (Object)Math.max(0L, hudData.getLong(path + ".blocks-changed", 0L) + blocksChanged));
        }
        hudData.set("updated", (Object)System.currentTimeMillis());
    }

    static final class Rank {
        private final String name;
        private final long score;

        private Rank(String name, long score) {
            this.name = name;
            this.score = score;
        }

        String name() {
            return this.name;
        }

        long score() {
            return this.score;
        }
    }
}
