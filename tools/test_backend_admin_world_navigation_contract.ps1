param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminWorldNavigationService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminWorldNavigationService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminWorldNavigationService<D>",
    "D dimensionForSlot(int slot, D world, D nether, D theEnd)",
    "case 12 -> world",
    "case 13 -> nether",
    "case 14 -> theEnd",
    "Integer chunkSizeForSlot(int slot)",
    "case 12 -> 1500",
    "case 13 -> 3000",
    "case 14 -> 5000",
    "String timeForSlot(int slot)",
    'case 11 -> "day"',
    'case 12 -> "night"',
    "Boolean weatherForSlot(int slot)",
    "case 13 -> Boolean.TRUE",
    "case 14 -> Boolean.FALSE"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminWorldNavigationService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminWorldNavigationService<ChunkDimension> adminWorldNavigationService;",
    "new BackendAdminWorldNavigationService<ChunkDimension>()",
    "this.adminWorldNavigationService",
    "this.adminChunksDimensionClickService.action(",
    "this.adminChunksSizeClickService.action("
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminWorldNavigationService: $snippet"
    }
}

Write-Host "Backend admin world navigation contract OK"
