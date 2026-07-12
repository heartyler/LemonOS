param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminChunksClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminChunksClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminChunksClickService",
    "AdminChunksAction action(int clickedSlot, int backSlot, int centerSlot, int sizeSlot, int dimensionSlot, int cancelSlot, int startSlot)",
    "return AdminChunksAction.BACK;",
    "return AdminChunksAction.CENTER;",
    "return AdminChunksAction.SIZE;",
    "return AdminChunksAction.DIMENSION;",
    "return AdminChunksAction.CANCEL;",
    "return AdminChunksAction.START;",
    "return AdminChunksAction.NONE;",
    "enum AdminChunksAction",
    "CENTER,",
    "SIZE,",
    "DIMENSION,",
    "CANCEL,",
    "START;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminChunksClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminChunksClickService adminChunksClickService;",
    "new BackendAdminChunksClickService()",
    "this.handleAdminChunksClick(player, n);",
    "private void handleAdminChunksClick(Player player, int slot)",
    "this.adminChunksClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "case BACK -> this.openAdminUpkeep(player);",
    "case CENTER ->",
    "this.adminChunkActionService.canEditChunks(this.chunksRunning())",
    "this.setChunksCenter(player);",
    "case SIZE ->",
    "this.openNextTick(() -> this.openAdminChunksSize(player));",
    "case DIMENSION ->",
    "this.openNextTick(() -> this.openAdminChunksDimension(player));",
    "case CANCEL ->",
    "this.adminChunkActionService.canCancelChunks(this.chunksRunning())",
    "this.cancelChunks(player);",
    "case START ->",
    "this.adminChunkActionService.canStartChunks(this.chunksRunning())",
    "this.openNextTick(() -> this.openAdminChunksConfirm(player));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminChunksClickService: $snippet"
    }
}

Write-Host "Backend admin chunks click contract OK"
