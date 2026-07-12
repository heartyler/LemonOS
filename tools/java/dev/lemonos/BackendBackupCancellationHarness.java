package dev.lemonos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public final class BackendBackupCancellationHarness {
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("lemonos-backup-contract-");
        try {
            Path honey = root.resolve("honey");
            Path runtime = root.resolve("survival");
            Files.createDirectories(honey.resolve("lemonos-data"));
            Files.createDirectories(honey.resolve("LemonOS"));
            Files.createDirectories(runtime.resolve("world"));
            Files.writeString(honey.resolve("lemonos-data/identities.yml"), "version: 1\n");
            Files.writeString(honey.resolve("LemonOS/config.yml"), "server: auto\n");
            Files.writeString(runtime.resolve("world/level.dat"), "world");

            BackendBackupOperationService service = new BackendBackupOperationService();
            BackendBackupOperationService.BackupPlan cancelledPlan = service.plan(honey, runtime, "survival", "cancelled");
            BackendOperationCancellation cancelled = new BackendOperationCancellation();
            require(cancelled.cancel(), "first cancellation must win");
            try {
                service.write(cancelledPlan, cancelled);
                throw new IllegalStateException("cancelled backup unexpectedly completed");
            }
            catch (IOException expected) {
                // expected
            }
            require(!Files.exists(cancelledPlan.serverBackup), "cancelled server archive must not publish");
            require(!Files.exists(cancelledPlan.lemonosDataBackup), "cancelled data archive must not publish");
            require(!Files.exists(cancelledPlan.serverBackup.resolveSibling(cancelledPlan.serverBackup.getFileName() + ".tmp")), "server temp must be cleaned");
            require(!Files.exists(cancelledPlan.lemonosDataBackup.resolveSibling(cancelledPlan.lemonosDataBackup.getFileName() + ".tmp")), "data temp must be cleaned");

            BackendBackupOperationService.BackupPlan successPlan = service.plan(honey, runtime, "survival", "success");
            BackendOperationCancellation committed = new BackendOperationCancellation();
            service.write(successPlan, committed);
            require(Files.isRegularFile(successPlan.serverBackup), "server archive missing");
            require(Files.isRegularFile(successPlan.lemonosDataBackup), "data archive missing");
            require(!committed.cancel(), "committed operation must reject timeout cancellation");
            System.out.println("Backend backup cancellation harness OK");
        }
        finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
