param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPasscodeLifecycleService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendPasscodeLifecycleService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendPasscodeLifecycleService",
    "void cancelTasks(Collection<BukkitTask> tasks)",
    "for (BukkitTask task : tasks)",
    "task.cancel();",
    "void clearStatus(UUID playerId, Map<UUID, String> titleStatuses, Map<UUID, BukkitTask> statusTasks)",
    "titleStatuses.remove(playerId);",
    "this.cancelRemoved(statusTasks.remove(playerId));",
    "void clearSuccess(UUID playerId, Map<UUID, BukkitTask> successTasks)",
    "this.cancelRemoved(successTasks.remove(playerId));",
    "void clearOverflow(UUID playerId, Set<UUID> overflowWarnings, Map<UUID, BukkitTask> overflowTasks)",
    "overflowWarnings.remove(playerId);",
    "this.cancelRemoved(overflowTasks.remove(playerId));",
    "void clearAll(",
    "this.clearStatus(playerId, titleStatuses, statusTasks);",
    "this.clearSuccess(playerId, successTasks);",
    "this.clearOverflow(playerId, overflowWarnings, overflowTasks);",
    "private void cancelRemoved(BukkitTask task)",
    "if (task != null)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendPasscodeLifecycleService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendPasscodeLifecycleService passcodeLifecycleService;",
    "this.passcodeLifecycleService = new BackendPasscodeLifecycleService();",
    "this.passcodeLifecycleService.cancelTasks(this.passcodeOverflowTasks.values());",
    "this.passcodeLifecycleService.cancelTasks(this.passcodeStatusTasks.values());",
    "this.passcodeLifecycleService.cancelTasks(this.passcodeSuccessTasks.values());",
    "private void clearPasscodeStatus(UUID uUID)",
    "this.passcodeLifecycleService.clearStatus(uUID, this.passcodeTitleStatuses, this.passcodeStatusTasks);",
    "private void clearPasscodeFeedback(UUID uUID)",
    "this.passcodeLifecycleService.clearAll(",
    "this.passcodeTitleStatuses",
    "this.passcodeStatusTasks",
    "this.passcodeSuccessTasks",
    "this.passcodeOverflowWarnings",
    "this.passcodeOverflowTasks"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendPasscodeLifecycleService: $snippet"
    }
}

Write-Host "Backend passcode lifecycle contract OK"
