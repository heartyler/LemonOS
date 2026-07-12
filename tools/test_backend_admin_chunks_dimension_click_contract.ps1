param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminChunksDimensionClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminChunksDimensionClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminChunksDimensionClickService<D>",
    "AdminChunksDimensionClick<D> action(int clickedSlot, int backSlot, BackendAdminWorldNavigationService<D> worldNavigationService, D world, D nether, D theEnd)",
    "return new AdminChunksDimensionClick<D>(AdminChunksDimensionAction.BACK, null);",
    "D dimension = worldNavigationService.dimensionForSlot(clickedSlot, world, nether, theEnd);",
    "return new AdminChunksDimensionClick<D>(AdminChunksDimensionAction.SELECT, dimension);",
    "return new AdminChunksDimensionClick<D>(AdminChunksDimensionAction.NONE, null);",
    "record AdminChunksDimensionClick<D>",
    "enum AdminChunksDimensionAction",
    "BACK,",
    "SELECT;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminChunksDimensionClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminChunksDimensionClickService<ChunkDimension> adminChunksDimensionClickService;",
    "new BackendAdminChunksDimensionClickService<ChunkDimension>()",
    "this.handleAdminChunksDimensionClick(player, n);",
    "private void handleAdminChunksDimensionClick(Player player, int slot)",
    "this.adminChunksDimensionClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "this.adminWorldNavigationService",
    "ChunkDimension.WORLD",
    "ChunkDimension.NETHER",
    "ChunkDimension.THE_END",
    "case BACK -> this.openAdminChunks(player);",
    "this.setChunksDimension(click.dimension());",
    "this.openNextTick(() -> this.openAdminChunks(player));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminChunksDimensionClickService: $snippet"
    }
}

Write-Host "Backend admin chunks dimension click contract OK"
