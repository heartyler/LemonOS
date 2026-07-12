param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

foreach ($required in @(
    'private static final int CHUNK_STOPPED_CONFIRMATIONS = 3',
    'private static final int CHUNK_PROBE_FAILURE_LIMIT = 15',
    'private BukkitTask pendingOperationWatchdogTask',
    'this.startPendingOperationWatchdog();',
    'this.pendingOperationWatchdogTask.cancel();',
    'private Boolean chunkyRunningState',
    'int confirmations = ++operation.stoppedConfirmations',
    'int failures = ++operation.probeFailures',
    'this.completeChunkOperation(world, operation.token, false, "completion callback was not received")',
    'this.completeChunkOperation(world, operation.token, false, "state probe failed")',
    'this.setChunkStatusForWorld(world, "idle.")'
)) { if (-not $plugin.Contains($required)) { throw "Pending-operation watchdog contract missing: $required" } }

Write-Host "Backend pending-operation watchdog contract OK"
