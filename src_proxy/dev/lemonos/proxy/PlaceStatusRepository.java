/*
 * Decompiled-compatible LemonOS place status storage.
 */
package dev.lemonos.proxy;

import dev.lemonos.common.LemonOS;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;

final class PlaceStatusRepository {
    private final Path placesFile;
    private final Logger logger;

    PlaceStatusRepository(Path placesFile, Logger logger) {
        this.placesFile = placesFile;
        this.logger = logger;
    }

    static String defaultFile() {
        return "version: \"" + LemonOS.DATA_SCHEMA_VERSION + "\"\nplaces:\n  lobby:\n    status: ready\n  survival:\n    status: ready\n  creative:\n    status: ready\n";
    }

    String status(String place) {
        if (!Files.isRegularFile(this.placesFile, new LinkOption[0])) {
            return "ready";
        }
        try {
            boolean inPlace = false;
            for (String line : Files.readAllLines(this.placesFile, StandardCharsets.UTF_8)) {
                String stripped = line.strip();
                if (stripped.equals(place + ":")) {
                    inPlace = true;
                    continue;
                }
                if (inPlace && stripped.endsWith(":") && !stripped.startsWith("status:")) {
                    return "ready";
                }
                if (inPlace && stripped.startsWith("status:")) {
                    return cleanScalar(stripped.substring("status:".length()).strip()).toLowerCase(Locale.ROOT);
                }
            }
        }
        catch (IOException exception) {
            this.logger.debug("Unable to read LemonOS place status.", (Throwable)exception);
        }
        return "ready";
    }

    void setStatus(String place, String status) {
        try {
            Files.createDirectories(this.placesFile.getParent(), new FileAttribute[0]);
            List<String> lines = Files.exists(this.placesFile, new LinkOption[0]) ? Files.readAllLines(this.placesFile, StandardCharsets.UTF_8) : new ArrayList<String>();
            List<String> output = new ArrayList<String>();
            boolean inPlace = false;
            boolean wroteStatus = false;
            boolean hasPlaces = false;
            for (String line : lines) {
                String stripped = line.strip();
                if (stripped.equals("places:")) {
                    hasPlaces = true;
                }
                if (stripped.equals(place + ":")) {
                    inPlace = true;
                    wroteStatus = false;
                    output.add(line);
                    continue;
                }
                if (inPlace && stripped.endsWith(":") && !stripped.startsWith("status:")) {
                    if (!wroteStatus) {
                        output.add("    status: " + status);
                    }
                    inPlace = false;
                }
                if (inPlace && stripped.startsWith("status:")) {
                    output.add("    status: " + status);
                    wroteStatus = true;
                    continue;
                }
                output.add(line);
            }
            if (inPlace && !wroteStatus) {
                output.add("    status: " + status);
            }
            if (!hasPlaces) {
                output.add("version: \"" + LemonOS.DATA_SCHEMA_VERSION + "\"");
                output.add("places:");
            }
            if (!lines.stream().anyMatch(line -> line.strip().equals(place + ":"))) {
                output.add("  " + place + ":");
                output.add("    status: " + status);
            }
            this.writeStringAtomic(String.join(System.lineSeparator(), output) + System.lineSeparator());
        }
        catch (IOException exception) {
            this.logger.debug("Unable to write LemonOS place status.", (Throwable)exception);
        }
    }

    static boolean isReadyStatus(String status) {
        return status == null || status.isBlank() || "ready".equals(status) || "ready.".equals(status);
    }

    static boolean isWakeStatus(String status) {
        return "sleep".equals(status) || "sleep.".equals(status) || "waking".equals(status) || "waking.".equals(status) || "waking up".equals(status) || "waking up.".equals(status) || "resting".equals(status) || "resting.".equals(status);
    }

    private static String cleanScalar(String string) {
        return string.replace("\"", "").replace("'", "").strip();
    }

    private void writeStringAtomic(String string) throws IOException {
        Files.createDirectories(this.placesFile.getParent(), new FileAttribute[0]);
        Path temporary = this.placesFile.resolveSibling(String.valueOf(this.placesFile.getFileName()) + ".tmp");
        Files.writeString(temporary, (CharSequence)string, StandardCharsets.UTF_8, new OpenOption[0]);
        try {
            Files.move(temporary, this.placesFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (IOException exception) {
            Files.move(temporary, this.placesFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
