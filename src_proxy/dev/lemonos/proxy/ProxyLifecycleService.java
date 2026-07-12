/*
 * Decompiled-compatible LemonOS proxy lifecycle setup helpers.
 */
package dev.lemonos.proxy;

import dev.lemonos.proxy.runtime.ProxyRuntimeLayout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;
import org.slf4j.Logger;

final class ProxyLifecycleService {
    private final Logger logger;

    ProxyLifecycleService(Logger logger) {
        this.logger = logger;
    }

    void ensureSharedDataFiles(ProxyRuntimeLayout runtimePaths) {
        try {
            Files.createDirectories(runtimePaths.sharedDataFolder, new FileAttribute[0]);
            if (!Files.exists(runtimePaths.accessFile, new LinkOption[0])) {
                Files.writeString(runtimePaths.accessFile, (CharSequence)AccessRepository.defaultFile(), StandardCharsets.UTF_8, new OpenOption[0]);
            }
            if (!Files.exists(runtimePaths.placesFile, new LinkOption[0])) {
                Files.writeString(runtimePaths.placesFile, (CharSequence)PlaceStatusRepository.defaultFile(), StandardCharsets.UTF_8, new OpenOption[0]);
            }
            if (!Files.exists(runtimePaths.playtimeFile, new LinkOption[0])) {
                Files.writeString(runtimePaths.playtimeFile, (CharSequence)PlaytimeRepository.defaultFile(), StandardCharsets.UTF_8, new OpenOption[0]);
            }
        }
        catch (IOException exception) {
            throw new IllegalStateException("Unable to prepare LemonOS shared data.", exception);
        }
    }

    boolean stayedCloseCollectionEnabled(Path boardsConfigFile) {
        if (boardsConfigFile == null || !Files.isRegularFile(boardsConfigFile, new LinkOption[0])) {
            return false;
        }
        try {
            boolean boardsSection = false;
            boolean stayedCloseSection = false;
            for (String line : Files.readAllLines(boardsConfigFile, StandardCharsets.UTF_8)) {
                String stripped = line.strip();
                if (!boardsSection && "boards:".equals(stripped)) {
                    boardsSection = true;
                    continue;
                }
                if (boardsSection && line.startsWith("  ") && !line.startsWith("    ") && "stayed-close:".equals(stripped)) {
                    stayedCloseSection = true;
                    continue;
                }
                if (!stayedCloseSection) {
                    continue;
                }
                if (!line.isBlank() && !line.startsWith("    ")) {
                    return false;
                }
                if (stripped.startsWith("enabled:")) {
                    return "true".equalsIgnoreCase(AccessRepository.cleanScalar(stripped.substring("enabled:".length()).strip()));
                }
            }
        }
        catch (IOException exception) {
            this.logger.debug("Unable to read Stayed Close collection gate.", (Throwable)exception);
        }
        return false;
    }

    String buildSourceSnapshot(Class<?> ownerClass) {
        try (java.io.InputStream inputStream = ownerClass.getClassLoader().getResourceAsStream("lemonos-build.properties")) {
            if (inputStream == null) {
                return "unknown";
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("sourceSnapshotSha256", "unknown");
        }
        catch (IOException exception) {
            return "unknown";
        }
    }

}
