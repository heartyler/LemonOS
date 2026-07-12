/*
 * Backend-side LemonOS rest timestamp file persistence.
 */
package dev.lemonos;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;

final class BackendRestFileService {
    void writeRestingSince(File honeyRoot, String serverProxyName, long restSinceMillis) throws IOException {
        Path directory = this.restDirectory(honeyRoot);
        Files.createDirectories(directory, new FileAttribute[0]);
        Files.writeString(this.restFile(directory, serverProxyName), String.valueOf(restSinceMillis), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    void clearRestingSince(File honeyRoot, String serverProxyName) throws IOException {
        Files.deleteIfExists(this.restFile(this.restDirectory(honeyRoot), serverProxyName));
    }

    private Path restDirectory(File honeyRoot) {
        return honeyRoot.toPath().resolve("lemonos-data").resolve("rest");
    }

    private Path restFile(Path restDirectory, String serverProxyName) {
        return restDirectory.resolve(serverProxyName + ".resting");
    }
}
