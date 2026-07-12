/*
 * Decompiled-compatible LemonOS access storage.
 */
package dev.lemonos.proxy;

import dev.lemonos.common.AdminProtocol;
import dev.lemonos.common.LemonOS;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;

final class AccessRepository {
    private final Path accessFile;
    private final Logger logger;
    private final Set<UUID> adminAccess = new LinkedHashSet<UUID>();
    private final Set<String> adminNameAccess = new LinkedHashSet<String>();
    private final Map<UUID, String> adminNames = new LinkedHashMap<UUID, String>();

    AccessRepository(Path accessFile, Logger logger) {
        this.accessFile = accessFile;
        this.logger = logger;
    }

    static String defaultFile() {
        return "version: \"" + LemonOS.ACCESS_SCHEMA_VERSION + "\"\nadmins:\n";
    }

    void load() {
        try {
            this.loadStrict();
        }
        catch (IOException exception) {
            this.logger.warn("Unable to load LemonOS access file: {}", (Object)exception.getMessage());
        }
    }

    private void loadStrict() throws IOException {
        this.adminAccess.clear();
        this.adminNameAccess.clear();
        this.adminNames.clear();
        boolean legacyAdminList = false;
        boolean nameAdminList = false;
        UUID currentAdmin = null;
        for (String line : Files.readAllLines(this.accessFile, StandardCharsets.UTF_8)) {
            String stripped = line.strip();
            if (stripped.isEmpty() || stripped.startsWith("#")) continue;
            if (stripped.equals("admins:")) {
                nameAdminList = true;
                legacyAdminList = false;
                currentAdmin = null;
                continue;
            }
            if (stripped.startsWith("admin:")) {
                legacyAdminList = true;
                nameAdminList = false;
                this.readInlineAdminList(stripped);
                continue;
            }
            if (nameAdminList && stripped.startsWith("- ")) {
                this.addAdminNameEntry(stripped.substring(2).strip());
                continue;
            }
            if (legacyAdminList && stripped.startsWith("- ")) {
                currentAdmin = this.addAdminEntry(stripped.substring(2).strip());
                continue;
            }
            if (legacyAdminList && currentAdmin != null && stripped.startsWith("uuid:")) {
                currentAdmin = this.addAdminEntry(stripped.substring("uuid:".length()).strip());
                continue;
            }
            if (legacyAdminList && currentAdmin != null && stripped.startsWith("name:")) {
                String name = cleanScalar(stripped.substring("name:".length()).strip());
                this.adminNames.put(currentAdmin, name);
                this.addAdminNameEntry(name);
                continue;
            }
            if (stripped.startsWith("- ")) continue;
            legacyAdminList = false;
            nameAdminList = false;
            currentAdmin = null;
        }
    }

    boolean hasAdminAccess(UUID uuid) {
        return this.adminAccess.contains(uuid);
    }

    boolean hasAdminAccess(UUID uuid, String name) {
        return this.adminAccess.contains(uuid) || this.adminNameAccess.contains(normalizeAccessName(name));
    }

    Set<UUID> adminUuids() {
        return new LinkedHashSet<UUID>(this.adminAccess);
    }

    String adminName(UUID uuid) {
        return this.adminNames.get(uuid);
    }

    List<String> adminNameAccess() {
        ArrayList<String> names = new ArrayList<String>(this.adminNameAccess);
        names.sort(String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    void setAccess(UUID targetUuid, String name, String role) {
        if (AdminProtocol.ROLE_ADMIN.equals(role)) {
            this.adminAccess.add(targetUuid);
            this.adminNames.put(targetUuid, name);
            this.addAdminNameEntry(name);
            return;
        }
        this.adminAccess.remove(targetUuid);
        this.adminNames.remove(targetUuid);
        this.adminNameAccess.remove(normalizeAccessName(name));
    }

    boolean setNameAccess(String name, boolean admin) {
        String normalized = normalizeAccessName(name);
        if (normalized.isBlank()) {
            return false;
        }
        if (admin) {
            return this.addAdminNameEntry(normalized);
        }
        return this.adminNameAccess.remove(normalized);
    }

    synchronized void updateAccess(UUID targetUuid, String name, String role) throws IOException {
        this.withFileLock(() -> {
            this.loadStrict();
            this.setAccess(targetUuid, name, role);
            this.writeStringAtomic(this.content());
        });
    }

    synchronized void updateNameAccess(String name, boolean admin) throws IOException {
        this.withFileLock(() -> {
            this.loadStrict();
            this.setNameAccess(name, admin);
            this.writeStringAtomic(this.content());
        });
    }

    synchronized void save() throws IOException {
        this.withFileLock(() -> this.writeStringAtomic(this.content()));
    }

    String content() {
        StringBuilder builder = new StringBuilder();
        builder.append("version: \"").append(LemonOS.ACCESS_SCHEMA_VERSION).append("\"\n");
        builder.append("admins:\n");
        for (String name : this.adminNameAccess()) {
            builder.append("  - ").append(name.replace("\"", "")).append("\n");
        }
        return builder.toString();
    }

    static String cleanScalar(String string) {
        return string.replace("\"", "").replace("'", "").strip();
    }

    static String normalizeAccessName(String string) {
        return cleanScalar(string == null ? "" : string).toLowerCase(Locale.ROOT);
    }

    private void readInlineAdminList(String string) {
        int open = string.indexOf(91);
        int close = string.indexOf(93);
        if (open < 0 || close <= open) {
            return;
        }
        String list = string.substring(open + 1, close).strip();
        if (list.isEmpty()) {
            return;
        }
        for (String entry : list.split(",")) {
            this.addAdminEntry(entry.strip());
        }
    }

    private UUID addAdminEntry(String string) {
        String scalar = cleanScalar(string);
        if (scalar.startsWith("uuid:")) {
            scalar = cleanScalar(scalar.substring("uuid:".length()).strip());
        }
        if (scalar.isEmpty()) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(scalar);
            this.adminAccess.add(uuid);
            this.adminNames.putIfAbsent(uuid, "");
            return uuid;
        }
        catch (IllegalArgumentException exception) {
            this.logger.debug("Ignoring non-UUID LemonOS admin access entry: {}", (Object)scalar);
            return null;
        }
    }

    private boolean addAdminNameEntry(String string) {
        String normalized = normalizeAccessName(string);
        if (normalized.isBlank() || normalized.contains(":")) {
            return false;
        }
        this.adminNameAccess.add(normalized);
        return true;
    }

    private void writeStringAtomic(String string) throws IOException {
        Files.createDirectories(this.accessFile.getParent(), new FileAttribute[0]);
        Path temporary = this.accessFile.resolveSibling(String.valueOf(this.accessFile.getFileName()) + ".tmp");
        Files.writeString(temporary, (CharSequence)string, StandardCharsets.UTF_8, new OpenOption[0]);
        try {
            Files.move(temporary, this.accessFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (IOException exception) {
            Files.move(temporary, this.accessFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void withFileLock(IoAction action) throws IOException {
        Path lockFile = this.accessFile.resolveSibling(String.valueOf(this.accessFile.getFileName()) + ".lock");
        Files.createDirectories(lockFile.getParent(), new FileAttribute[0]);
        try (FileChannel channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock ignored = channel.lock()) {
            action.run();
        }
    }

    @FunctionalInterface
    private interface IoAction {
        void run() throws IOException;
    }
}
