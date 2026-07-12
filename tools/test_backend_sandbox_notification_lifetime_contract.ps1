param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$sourceRoot = Join-Path $Root "src\main\java\dev\lemonos"
$status = Get-Content -Raw -LiteralPath (Join-Path $sourceRoot "BackendSandboxStatusService.java")
$coordinator = Get-Content -Raw -LiteralPath (Join-Path $sourceRoot "BackendActionBarCoordinator.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $sourceRoot "LemonOSPlugin.java")
foreach ($required in @(
    'this.actionBarCoordinator.notify',
    'ownerEntries.entrySet().removeIf(entry -> entry.getValue().untilNanos <= nowNanos)',
    'BackendActionBarCoordinator.Owner.SANDBOX_NOTIFICATION',
    '3000'
)) { if (-not $coordinator.Contains($required) -and -not $plugin.Contains($required)) { throw "Coordinator notification lifetime missing: $required" } }
foreach ($forbidden in @('Notification', 'notifications', '.notify(')) {
    if ($status.Contains($forbidden)) { throw "Sandbox status service still duplicates notification lifetime: $forbidden" }
}
Write-Host "Backend Sandbox notification lifetime contract OK"
