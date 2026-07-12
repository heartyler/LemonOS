param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminUpkeepClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminUpkeepClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminUpkeepClickService",
    "AdminUpkeepAction action(int clickedSlot, int backSlot, int backupSlot, int chunksSlot)",
    "return AdminUpkeepAction.BACK;",
    "return AdminUpkeepAction.BACKUP;",
    "return AdminUpkeepAction.CHUNKS;",
    "return AdminUpkeepAction.NONE;",
    "enum AdminUpkeepAction",
    "BACK,",
    "BACKUP,",
    "CHUNKS;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminUpkeepClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminUpkeepClickService adminUpkeepClickService;",
    "new BackendAdminUpkeepClickService()",
    "this.handleAdminUpkeepClick(player, n);",
    "private void handleAdminUpkeepClick(Player player, int slot)",
    "this.adminUpkeepClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "case BACK -> this.openAdmin(player);",
    "case BACKUP -> this.openNextTick(() -> this.openAdminBackupConfirm(player));",
    "case CHUNKS ->",
    "this.adminChunkActionService.canOpenChunks(this.chunksAvailableHere())",
    "this.openNextTick(() -> this.openAdminChunks(player));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminUpkeepClickService: $snippet"
    }
}

Write-Host "Backend admin upkeep click contract OK"
