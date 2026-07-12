param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

foreach ($required in @(
    'private static final long BACKUP_TIMEOUT_TICKS = 6000L',
    'private final BackendOperationRegistry<ServerId, ManualBackupOperation> backupOperations',
    'private final AtomicLong backupGenerationCounter',
    'catch (IOException | RuntimeException exception)',
    'if (!this.isEnabled()) return',
    'this.completeManualBackup(serverId, string, backupPlan, token, bl2, string3)',
    'this.timeoutManualBackup(serverId, token)',
    'this.backupOperations.removeIfCurrent(serverId, token)',
    'private void abortManualBackup(ServerId serverId, BackendOperationToken token)',
    'operation.workerTask.replace(backupTask)',
    'operation.timeoutTask.replace(timeoutTask)',
    'operation.cancellation.cancel()'
)) { if (-not $plugin.Contains($required)) { throw "Backup async lifecycle contract missing: $required" } }

Write-Host "Backend backup async lifecycle contract OK"
