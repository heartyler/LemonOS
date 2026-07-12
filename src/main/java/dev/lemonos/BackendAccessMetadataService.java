/*
 * Backend-side LemonOS access metadata read model.
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

final class BackendAccessMetadataService {
    private final Function<String, String> nameNormalizer;
    private final Predicate<String> nameValidator;

    BackendAccessMetadataService(Function<String, String> nameNormalizer, Predicate<String> nameValidator) {
        this.nameNormalizer = nameNormalizer;
        this.nameValidator = nameValidator;
    }

    List<String> accessHolderNames(Path accessFile) {
        if (!Files.isRegularFile(accessFile, new LinkOption[0])) {
            return List.of();
        }
        ArrayList<String> names = new ArrayList<String>();
        try {
            boolean admins = false;
            for (String line : Files.readAllLines(accessFile, StandardCharsets.UTF_8)) {
                String stripped = line.trim();
                if (stripped.equals("admins:")) {
                    admins = true;
                    continue;
                }
                if (!stripped.startsWith("-")) {
                    admins = false;
                    continue;
                }
                String name = this.nameNormalizer.apply(stripped.substring(1).trim());
                if (!admins || !this.nameValidator.test(name)) {
                    continue;
                }
                names.add(name);
            }
        }
        catch (IOException exception) {
            return List.of();
        }
        return names.stream().distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }
}
