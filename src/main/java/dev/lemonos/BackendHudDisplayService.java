/*
 * Backend-side LemonOS HUD scoreboard display model.
 */
package dev.lemonos;

import java.util.ArrayList;
import java.util.List;

final class BackendHudDisplayService {
    BackendDisplayModel model(BackendDisplayConfig config, Board board, List<Rank> ranks) {
        ArrayList<BackendDisplayModel.Entry> entries = new ArrayList<BackendDisplayModel.Entry>();
        String configPath = board.configPath();
        String rolePrefix = board.rolePrefix();
        int top = config.intValue(configPath + ".top", 5, 1, 10);
        double subtitleOffsetY = config.doubleValue(configPath + ".display.subtitle-offset-y", -0.10, -4.0, 4.0);
        double rowStartOffsetY = config.doubleValue(configPath + ".display.row-start-offset-y", -0.34, -4.0, 4.0);
        double rowGap = config.doubleValue(configPath + ".display.row-gap", -0.13, -2.0, 2.0);
        double nameOffsetZ = config.doubleValue(configPath + ".display.name-offset-z", -0.32, -8.0, 8.0);
        double valueOffsetZ = config.doubleValue(configPath + ".display.value-offset-z", configPath + ".display.score-offset-z", 0.48, -8.0, 8.0);
        boolean bedrockEnabled = config.booleanValue(configPath + ".display.bedrock.enabled", false);
        double titleOffsetX = config.doubleValue(configPath + ".display.title-offset-x", 0.0, -8.0, 8.0);
        double titleOffsetY = config.doubleValue(configPath + ".display.title-offset-y", 0.0, -4.0, 4.0);
        double titleOffsetZ = config.doubleValue(configPath + ".display.title-offset-z", 0.0, -8.0, 8.0);
        String title = config.stringValue(configPath + ".title", board.defaultTitle());
        String subtitle = config.stringValue(configPath + ".subtitle", board.defaultSubtitle());
        String bottomLine = config.stringValue(configPath + ".bottom-line", board.defaultBottomLine());
        entries.add(new BackendDisplayModel.Entry(rolePrefix + "title", titleOffsetX, titleOffsetY, titleOffsetZ, title, BackendDisplayModel.ColorRole.WHITE, BackendDisplayModel.Alignment.CENTER));
        entries.add(new BackendDisplayModel.Entry(rolePrefix + "subtitle", 0.0, subtitleOffsetY, 0.0, subtitle, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.CENTER));
        double bottomOffsetY = config.doubleValue(configPath + ".display.bottom-offset-y", -1.02, -4.0, 4.0);
        double bottomOffsetZ = config.doubleValue(configPath + ".display.bottom-offset-z", 0.0, -8.0, 8.0);
        entries.add(new BackendDisplayModel.Entry(rolePrefix + "bottom", 0.0, bottomOffsetY, bottomOffsetZ, bottomLine, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.CENTER));
        for (int i = 0; i < top; ++i) {
            Rank rank = i < ranks.size() ? ranks.get(i) : null;
            double rowOffsetY = rowStartOffsetY + (double)i * rowGap;
            String name = rank == null ? "" : BackendDisplayText.fitName(rank.name(), config.intValue(configPath + ".name-width", 12, 4, 16));
            String score = rank == null ? "" : Long.toString(rank.score());
            entries.add(new BackendDisplayModel.Entry(rolePrefix + "name_" + (i + 1), 0.0, rowOffsetY, nameOffsetZ, name, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.LEFT));
            entries.add(new BackendDisplayModel.Entry(rolePrefix + "score_" + (i + 1), 0.0, rowOffsetY, valueOffsetZ, score, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.RIGHT));
            if (bedrockEnabled) {
                int bedrockNameWidth = config.intValue(configPath + ".display.bedrock.name-width", 12, 4, 24);
                double bedrockRowStartOffsetX = config.doubleValue(configPath + ".display.bedrock.row-start-offset-x", 0.0, -8.0, 8.0);
                double bedrockRowStartOffsetY = config.doubleValue(configPath + ".display.bedrock.row-start-offset-y", -0.34, -4.0, 4.0);
                double bedrockRowStartOffsetZ = config.doubleValue(configPath + ".display.bedrock.row-start-offset-z", 0.0, -8.0, 8.0);
                double bedrockRowGap = config.doubleValue(configPath + ".display.bedrock.row-gap", -0.16, -2.0, 2.0);
                double bedrockBottomOffsetX = config.doubleValue(configPath + ".display.bedrock.bottom-offset-x", 0.0, -8.0, 8.0);
                double bedrockBottomOffsetY = config.doubleValue(configPath + ".display.bedrock.bottom-offset-y", -1.02, -4.0, 4.0);
                double bedrockBottomOffsetZ = config.doubleValue(configPath + ".display.bedrock.bottom-offset-z", 0.0, -8.0, 8.0);
                String bedrockRow = rank == null ? "" : BackendDisplayText.fitName(rank.name(), bedrockNameWidth) + " " + rank.score();
                entries.add(new BackendDisplayModel.Entry(rolePrefix + "bedrock_row_" + (i + 1), bedrockRowStartOffsetX, bedrockRowStartOffsetY + (double)i * bedrockRowGap, bedrockRowStartOffsetZ, bedrockRow, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.CENTER));
                if (i == top - 1) {
                    entries.add(new BackendDisplayModel.Entry(rolePrefix + "bedrock_bottom", bedrockBottomOffsetX, bedrockBottomOffsetY, bedrockBottomOffsetZ, bottomLine, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.CENTER));
                }
            }
        }
        return new BackendDisplayModel(bedrockEnabled, entries);
    }

    static final class Board {
        private final String configPath;
        private final String rolePrefix;
        private final String defaultTitle;
        private final String defaultSubtitle;
        private final String defaultBottomLine;

        Board(String configPath, String rolePrefix, String defaultTitle, String defaultSubtitle, String defaultBottomLine) {
            this.configPath = configPath;
            this.rolePrefix = rolePrefix;
            this.defaultTitle = defaultTitle;
            this.defaultSubtitle = defaultSubtitle;
            this.defaultBottomLine = defaultBottomLine;
        }

        String configPath() {
            return this.configPath;
        }

        String rolePrefix() {
            return this.rolePrefix;
        }

        String defaultTitle() {
            return this.defaultTitle;
        }

        String defaultSubtitle() {
            return this.defaultSubtitle;
        }

        String defaultBottomLine() {
            return this.defaultBottomLine;
        }
    }

    static final class Rank {
        private final String name;
        private final long score;

        Rank(String name, long score) {
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
