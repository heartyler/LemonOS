/*
 * Backend-side LemonOS Stayed Close display model.
 */
package dev.lemonos;

import java.util.ArrayList;
import java.util.List;

final class BackendStayedCloseDisplayService {
    BackendDisplayModel model(BackendDisplayConfig config, List<Rank> ranks) {
        ArrayList<BackendDisplayModel.Entry> entries = new ArrayList<BackendDisplayModel.Entry>();
        int top = config.intValue("stayed-close.top", 5, 1, 10);
        double subtitleOffsetY = config.doubleValue("stayed-close.display.subtitle-offset-y", -0.10, -4.0, 4.0);
        double rowStartOffsetY = config.doubleValue("stayed-close.display.row-start-offset-y", -0.34, -4.0, 4.0);
        double rowGap = config.doubleValue("stayed-close.display.row-gap", -0.13, -2.0, 2.0);
        double nameOffsetZ = config.doubleValue("stayed-close.display.name-offset-z", -0.32, -8.0, 8.0);
        double valueOffsetZ = config.doubleValue("stayed-close.display.value-offset-z", "stayed-close.display.time-offset-z", 0.48, -8.0, 8.0);
        boolean bedrockEnabled = config.booleanValue("stayed-close.display.bedrock.enabled", true);
        double titleOffsetX = config.doubleValue("stayed-close.display.title-offset-x", 0.0, -8.0, 8.0);
        double titleOffsetY = config.doubleValue("stayed-close.display.title-offset-y", 0.0, -4.0, 4.0);
        double titleOffsetZ = config.doubleValue("stayed-close.display.title-offset-z", 0.0, -8.0, 8.0);
        String title = config.stringValue("stayed-close.title", "Stayclose").trim();
        String subtitle = config.stringValue("stayed-close.subtitle", "where small steps stay.").trim();
        String bottomLine = config.stringValue("stayed-close.bottom-line", "time spent here.").trim();
        entries.add(new BackendDisplayModel.Entry("stayed_close_title", titleOffsetX, titleOffsetY, titleOffsetZ, title, BackendDisplayModel.ColorRole.WHITE, BackendDisplayModel.Alignment.CENTER));
        entries.add(new BackendDisplayModel.Entry("stayed_close_subtitle", 0.0, subtitleOffsetY, 0.0, subtitle, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.CENTER));
        double bottomOffsetY = config.doubleValue("stayed-close.display.bottom-offset-y", -1.02, -4.0, 4.0);
        double bottomOffsetZ = config.doubleValue("stayed-close.display.bottom-offset-z", 0.0, -8.0, 8.0);
        entries.add(new BackendDisplayModel.Entry("stayed_close_bottom", 0.0, bottomOffsetY, bottomOffsetZ, bottomLine, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.CENTER));
        if (bedrockEnabled) {
            double bedrockTitleOffsetX = config.doubleValue("stayed-close.display.bedrock.title-offset-x", 0.0, -8.0, 8.0);
            double bedrockTitleOffsetY = config.doubleValue("stayed-close.display.bedrock.title-offset-y", 0.0, -4.0, 4.0);
            double bedrockTitleOffsetZ = config.doubleValue("stayed-close.display.bedrock.title-offset-z", 0.0, -8.0, 8.0);
            double bedrockSubtitleOffsetX = config.doubleValue("stayed-close.display.bedrock.subtitle-offset-x", 0.0, -8.0, 8.0);
            double bedrockSubtitleOffsetY = config.doubleValue("stayed-close.display.bedrock.subtitle-offset-y", -0.10, -4.0, 4.0);
            double bedrockSubtitleOffsetZ = config.doubleValue("stayed-close.display.bedrock.subtitle-offset-z", 0.0, -8.0, 8.0);
            double bedrockBottomOffsetX = config.doubleValue("stayed-close.display.bedrock.bottom-offset-x", 0.0, -8.0, 8.0);
            double bedrockBottomOffsetY = config.doubleValue("stayed-close.display.bedrock.bottom-offset-y", -1.02, -4.0, 4.0);
            double bedrockBottomOffsetZ = config.doubleValue("stayed-close.display.bedrock.bottom-offset-z", 0.0, -8.0, 8.0);
            entries.add(new BackendDisplayModel.Entry("stayed_close_bedrock_title", bedrockTitleOffsetX, bedrockTitleOffsetY, bedrockTitleOffsetZ, title, BackendDisplayModel.ColorRole.WHITE, BackendDisplayModel.Alignment.CENTER));
            entries.add(new BackendDisplayModel.Entry("stayed_close_bedrock_subtitle", bedrockSubtitleOffsetX, bedrockSubtitleOffsetY, bedrockSubtitleOffsetZ, subtitle, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.CENTER));
            entries.add(new BackendDisplayModel.Entry("stayed_close_bedrock_bottom", bedrockBottomOffsetX, bedrockBottomOffsetY, bedrockBottomOffsetZ, bottomLine, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.CENTER));
        }
        for (int i = 0; i < top; ++i) {
            Rank rank = i < ranks.size() ? ranks.get(i) : null;
            double rowOffsetY = rowStartOffsetY + (double)i * rowGap;
            String name = rank == null ? "" : BackendDisplayText.fitName(rank.name(), config.intValue("stayed-close.name-width", 12, 4, 16));
            String time = rank == null ? "" : formatTime(rank.totalSeconds());
            entries.add(new BackendDisplayModel.Entry("stayed_close_name_" + (i + 1), 0.0, rowOffsetY, nameOffsetZ, name, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.LEFT));
            entries.add(new BackendDisplayModel.Entry("stayed_close_time_" + (i + 1), 0.0, rowOffsetY, valueOffsetZ, time, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.RIGHT));
            if (bedrockEnabled) {
                int bedrockNameWidth = config.intValue("stayed-close.display.bedrock.name-width", 12, 4, 24);
                double bedrockRowStartOffsetX = config.doubleValue("stayed-close.display.bedrock.row-start-offset-x", 0.0, -8.0, 8.0);
                double bedrockRowStartOffsetY = config.doubleValue("stayed-close.display.bedrock.row-start-offset-y", -0.34, -4.0, 4.0);
                double bedrockRowStartOffsetZ = config.doubleValue("stayed-close.display.bedrock.row-start-offset-z", 0.0, -8.0, 8.0);
                double bedrockRowGap = config.doubleValue("stayed-close.display.bedrock.row-gap", -0.16, -2.0, 2.0);
                String bedrockRow = rank == null ? "" : BackendDisplayText.fitName(rank.name(), bedrockNameWidth) + " " + formatTime(rank.totalSeconds());
                entries.add(new BackendDisplayModel.Entry("stayed_close_bedrock_row_" + (i + 1), bedrockRowStartOffsetX, bedrockRowStartOffsetY + (double)i * bedrockRowGap, bedrockRowStartOffsetZ, bedrockRow, BackendDisplayModel.ColorRole.GRAY, BackendDisplayModel.Alignment.CENTER));
            }
        }
        return new BackendDisplayModel(bedrockEnabled, entries);
    }

    String formatTime(long totalSeconds) {
        long safeTotalSeconds = Math.max(0L, totalSeconds);
        if (safeTotalSeconds < 3600L) {
            return Math.max(0L, safeTotalSeconds / 60L) + "m";
        }
        return safeTotalSeconds / 3600L + "h";
    }

    static final class Rank {
        private final String name;
        private final long totalSeconds;

        Rank(String name, long totalSeconds) {
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
