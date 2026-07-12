/*
 * Backend-side LemonOS online player read model.
 */
package dev.lemonos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class BackendOnlineNamesService {
    private static final long ONLINE_SNAPSHOT_MAX_AGE_MILLIS = 15000L;
    private final Function<String, String> nameNormalizer;
    private final Predicate<String> nameValidator;

    BackendOnlineNamesService(Function<String, String> nameNormalizer, Predicate<String> nameValidator) {
        this.nameNormalizer = nameNormalizer;
        this.nameValidator = nameValidator;
    }

    List<String> onlineNames(Path onlineFile, long nowMillis) {
        if (!Files.isRegularFile(onlineFile, new LinkOption[0])) {
            return new ArrayList<String>();
        }
        ArrayList<String> names = new ArrayList<String>();
        try {
            List<String> lines = Files.readAllLines(onlineFile, StandardCharsets.UTF_8);
            long updated = 0L;
            for (String line : lines) {
                String stripped = line.trim();
                if (!stripped.startsWith("updated:")) {
                    continue;
                }
                updated = Long.parseLong(stripped.substring("updated:".length()).trim());
                break;
            }
            if (updated <= 0L || nowMillis - updated > ONLINE_SNAPSHOT_MAX_AGE_MILLIS) {
                return names;
            }
            for (String line : lines) {
                String stripped = line.trim();
                if (!stripped.startsWith("- name:")) {
                    continue;
                }
                String name = this.nameNormalizer.apply(stripped.substring("- name:".length()).trim());
                if (!this.nameValidator.test(name)) {
                    continue;
                }
                names.add(name);
            }
        }
        catch (IOException | NumberFormatException exception) {
            return new ArrayList<String>();
        }
        return names;
    }
}
