package dev.lemonos;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class BackendBackupOperationService {
    BackupPlan plan(Path honeyRoot, Path runtimeRoot, String serverName, String timestamp) {
        Path lemonosDataRoot = honeyRoot.resolve("lemonos-data");
        Path lemonOsRoot = honeyRoot.resolve("LemonOS");
        Path backupRoot = honeyRoot.resolve("backups");
        Path serverBackup = backupRoot.resolve(serverName).resolve("honey_" + serverName + "_" + timestamp + ".zip");
        Path lemonosDataBackup = backupRoot.resolve("lemonos-data").resolve("honey_lemonos-data_" + timestamp + ".zip");
        return new BackupPlan(honeyRoot, runtimeRoot, runtimeRoot.resolve("world"), lemonosDataRoot, lemonOsRoot, serverBackup, lemonosDataBackup, serverName);
    }

    boolean requiredPathsExist(BackupPlan plan) {
        return Files.isDirectory(plan.worldPath, new LinkOption[0]) && Files.isDirectory(plan.lemonosDataRoot, new LinkOption[0]) && Files.isDirectory(plan.lemonOsRoot, new LinkOption[0]);
    }

    void write(BackupPlan plan) throws IOException {
        this.write(plan, new BackendOperationCancellation());
    }

    void write(BackupPlan plan, BackendOperationCancellation cancellation) throws IOException {
        Files.createDirectories(plan.serverBackup.getParent());
        Files.createDirectories(plan.lemonosDataBackup.getParent());
        Path serverTmp = plan.serverBackup.resolveSibling(String.valueOf(plan.serverBackup.getFileName()) + ".tmp");
        Path dataTmp = plan.lemonosDataBackup.resolveSibling(String.valueOf(plan.lemonosDataBackup.getFileName()) + ".tmp");
        Files.deleteIfExists(serverTmp);
        Files.deleteIfExists(dataTmp);
        try {
            cancellation.check();
            this.writeServerBackup(plan, serverTmp, cancellation);
            cancellation.check();
            this.writeLemonosDataBackup(plan, dataTmp, cancellation);
            cancellation.check();
            cancellation.commit(() -> {
                this.replaceBackupArchive(serverTmp, plan.serverBackup);
                this.replaceBackupArchive(dataTmp, plan.lemonosDataBackup);
            });
        }
        finally {
            Files.deleteIfExists(serverTmp);
            Files.deleteIfExists(dataTmp);
        }
    }

    private void writeServerBackup(BackupPlan plan, Path tmpPath, BackendOperationCancellation cancellation) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tmpPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE));){
            this.addPathToZip(zipOutputStream, plan.worldPath, plan.serverName + "/world", cancellation);
            this.addPathToZip(zipOutputStream, plan.lemonosDataRoot, "lemonos-data", cancellation);
            this.addPathToZip(zipOutputStream, plan.lemonOsRoot, "LemonOS", cancellation);
            this.addNetworkConfigSnapshot(zipOutputStream, plan.runtimeRoot, cancellation);
        }
    }

    private void writeLemonosDataBackup(BackupPlan plan, Path tmpPath, BackendOperationCancellation cancellation) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tmpPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE));){
            this.addPathToZip(zipOutputStream, plan.lemonosDataRoot, "lemonos-data", cancellation);
            this.addPathToZip(zipOutputStream, plan.lemonOsRoot, "LemonOS", cancellation);
        }
    }

    private void replaceBackupArchive(Path tmpPath, Path targetPath) throws IOException {
        try {
            Files.move(tmpPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (AtomicMoveNotSupportedException atomicMoveNotSupportedException) {
            Files.move(tmpPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void addNetworkConfigSnapshot(ZipOutputStream zipOutputStream, Path runtimeRoot, BackendOperationCancellation cancellation) throws IOException {
        for (String serverName : List.of("velocity", "lobby", "survival", "creative")) {
            Path serverRoot = runtimeRoot.resolve(serverName);
            if (!Files.isDirectory(serverRoot, new LinkOption[0])) {
                continue;
            }
            for (String fileName : List.of("velocity.toml", "server.properties", "bukkit.yml", "spigot.yml", "commands.yml", "permissions.yml", "whitelist.json", "ops.json")) {
                this.addPathToZip(zipOutputStream, serverRoot.resolve(fileName), "configs/" + serverName + "/" + fileName, cancellation);
            }
            this.addPathToZip(zipOutputStream, serverRoot.resolve("config"), "configs/" + serverName + "/config", cancellation);
            this.addPathToZip(zipOutputStream, serverRoot.resolve("plugins/SkinsRestorer"), "configs/" + serverName + "/plugins/SkinsRestorer", cancellation);
            this.addPathToZip(zipOutputStream, serverRoot.resolve("plugins/floodgate"), "configs/" + serverName + "/plugins/floodgate", cancellation);
            this.addPathToZip(zipOutputStream, serverRoot.resolve("plugins/Geyser-Spigot"), "configs/" + serverName + "/plugins/Geyser-Spigot", cancellation);
            this.addPathToZip(zipOutputStream, serverRoot.resolve("plugins/Geyser-Velocity"), "configs/" + serverName + "/plugins/Geyser-Velocity", cancellation);
        }
    }

    private void addPathToZip(ZipOutputStream zipOutputStream, Path path, String zipPath, BackendOperationCancellation cancellation) throws IOException {
        cancellation.check();
        if (!Files.exists(path, new LinkOption[0])) {
            return;
        }
        if (Files.isRegularFile(path, new LinkOption[0])) {
            if (!this.skipBackupPath(path)) {
                this.addFileToZip(zipOutputStream, path, zipPath, cancellation);
            }
            return;
        }
        try (Stream<Path> stream = Files.walk(path, new FileVisitOption[0]);){
            List<Path> files = stream.filter(candidate -> Files.isRegularFile(candidate, new LinkOption[0])).filter(candidate -> !this.skipBackupPath(candidate)).sorted().toList();
            for (Path file : files) {
                String relative = path.relativize(file).toString().replace(File.separatorChar, '/');
                this.addFileToZip(zipOutputStream, file, zipPath + "/" + relative, cancellation);
            }
        }
    }

    private void addFileToZip(ZipOutputStream zipOutputStream, Path path, String zipPath, BackendOperationCancellation cancellation) throws IOException {
        cancellation.check();
        ZipEntry zipEntry = new ZipEntry(zipPath.replace('\\', '/'));
        zipOutputStream.putNextEntry(zipEntry);
        Files.copy(path, zipOutputStream);
        zipOutputStream.closeEntry();
        cancellation.check();
    }

    private boolean skipBackupPath(Path path) {
        for (Path part : path) {
            String name = part.toString().toLowerCase(Locale.ROOT);
            if (!name.equals("logs") && !name.equals("cache") && !name.equals("caches") && !name.equals("backups") && !name.equals("crash-reports") && !name.equals("libraries") && !name.equals("versions") && !name.equals(".git")) {
                continue;
            }
            return true;
        }
        String fileName = path.getFileName() == null ? "" : path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".jar") || fileName.endsWith(".log") || fileName.endsWith(".tmp") || fileName.endsWith(".lock") || fileName.endsWith(".zip") || fileName.endsWith(".7z");
    }

    static final class BackupPlan {
        final Path honeyRoot;
        final Path runtimeRoot;
        final Path worldPath;
        final Path lemonosDataRoot;
        final Path lemonOsRoot;
        final Path serverBackup;
        final Path lemonosDataBackup;
        final String serverName;

        BackupPlan(Path honeyRoot, Path runtimeRoot, Path worldPath, Path lemonosDataRoot, Path lemonOsRoot, Path serverBackup, Path lemonosDataBackup, String serverName) {
            this.honeyRoot = honeyRoot;
            this.runtimeRoot = runtimeRoot;
            this.worldPath = worldPath;
            this.lemonosDataRoot = lemonosDataRoot;
            this.lemonOsRoot = lemonOsRoot;
            this.serverBackup = serverBackup;
            this.lemonosDataBackup = lemonosDataBackup;
            this.serverName = serverName;
        }
    }
}
