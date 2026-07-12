param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminChunksSizeClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminChunksSizeClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminChunksSizeClickService",
    "AdminChunksSizeClick action(int clickedSlot, int backSlot, BackendAdminWorldNavigationService<?> worldNavigationService)",
    "return new AdminChunksSizeClick(AdminChunksSizeAction.BACK, null);",
    "Integer size = worldNavigationService.chunkSizeForSlot(clickedSlot);",
    "return new AdminChunksSizeClick(AdminChunksSizeAction.SELECT, size);",
    "return new AdminChunksSizeClick(AdminChunksSizeAction.NONE, null);",
    "record AdminChunksSizeClick",
    "enum AdminChunksSizeAction",
    "BACK,",
    "SELECT;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminChunksSizeClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminChunksSizeClickService adminChunksSizeClickService;",
    "new BackendAdminChunksSizeClickService()",
    "this.handleAdminChunksSizeClick(player, n);",
    "private void handleAdminChunksSizeClick(Player player, int slot)",
    "this.adminChunksSizeClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "this.adminWorldNavigationService",
    "case BACK -> this.openAdminChunks(player);",
    "this.setChunksSize(click.size());",
    "this.openNextTick(() -> this.openAdminChunks(player));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminChunksSizeClickService: $snippet"
    }
}

Write-Host "Backend admin chunks size click contract OK"
