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

    boolean stayedCloseCollectionEnabled(Path hudConfigFile) {
        if (hudConfigFile == null) return false;
        if (Files.isRegularFile(hudConfigFile, new LinkOption[0])) {
            return this.nestedFeatureEnabled(hudConfigFile, "hud", "stayed-close");
        }
        Path configRoot = hudConfigFile.getParent();
        if (configRoot == null) return false;
        Path legacyBoards = configRoot.resolve("boards.yml");
        if (Files.isRegularFile(legacyBoards, new LinkOption[0])) {
            return this.nestedFeatureEnabled(legacyBoards, "boards", "stayed-close");
        }
        return this.topLevelFeatureEnabled(configRoot.resolve("config.yml"), "stayed-close");
    }

    private boolean nestedFeatureEnabled(Path file, String rootName, String featureName) {
        try {
            boolean rootSection = false;
            boolean featureSection = false;
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String stripped = line.strip();
                if (!rootSection && (rootName + ":").equals(stripped) && this.exactIndent(line, 0)) {
                    rootSection = true;
                    continue;
                }
                if (rootSection && (featureName + ":").equals(stripped) && this.exactIndent(line, 2)) {
                    featureSection = true;
                    continue;
                }
                if (!featureSection) continue;
                if (!line.isBlank() && !this.atLeastIndent(line, 4)) return false;
                if (this.exactIndent(line, 4) && stripped.startsWith("enabled:")) {
                    return "true".equalsIgnoreCase(AccessRepository.cleanScalar(stripped.substring("enabled:".length()).strip()));
                }
            }
        }
        catch (IOException | RuntimeException exception) {
            this.logGateReadFailure(file, exception);
        }
        return false;
    }

    private boolean topLevelFeatureEnabled(Path file, String featureName) {
        if (!Files.isRegularFile(file, new LinkOption[0])) return false;
        try {
            boolean featureSection = false;
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String stripped = line.strip();
                if (!featureSection && (featureName + ":").equals(stripped) && this.exactIndent(line, 0)) {
                    featureSection = true;
                    continue;
                }
                if (!featureSection) continue;
                if (!line.isBlank() && !this.atLeastIndent(line, 2)) return false;
                if (this.exactIndent(line, 2) && stripped.startsWith("enabled:")) {
                    return "true".equalsIgnoreCase(AccessRepository.cleanScalar(stripped.substring("enabled:".length()).strip()));
                }
            }
        }
        catch (IOException | RuntimeException exception) {
            this.logGateReadFailure(file, exception);
        }
        return false;
    }

    private boolean exactIndent(String line, int spaces) {
        return this.atLeastIndent(line, spaces) && (line.length() == spaces || line.charAt(spaces) != ' ');
    }

    private boolean atLeastIndent(String line, int spaces) {
        if (line == null || line.length() < spaces) return false;
        for (int index = 0; index < spaces; index++) {
            if (line.charAt(index) != ' ') return false;
        }
        return true;
    }

    private void logGateReadFailure(Path file, Exception exception) {
        if (this.logger != null) this.logger.debug("Unable to read Stayed Close collection gate. Source: {}", file, exception);
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
