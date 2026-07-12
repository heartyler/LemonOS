param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminChunkActionService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminChunkActionService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminChunkActionService",
    "boolean canOpenChunks(boolean available)",
    "boolean canEditChunks(boolean running)",
    "return !running;",
    "boolean canStartChunks(boolean running)",
    "boolean canCancelChunks(boolean running)",
    "return running;",
    "boolean hasChunkSignal(boolean available, boolean chunkyReady)",
    "return available && chunkyReady;",
    'return running ? "running." : "prepare the world.";',
    'return running ? "running." : "choose where.";',
    'return running ? "running." : "choose how far.";',
    'return running ? "running." : "start from here.";',
    "boolean isActiveStatus(String status)",
    'return "waiting.".equals(status) || "running.".equals(status) || status.endsWith("% ready");'
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminChunkActionService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminChunkActionService adminChunkActionService;",
    "new BackendAdminChunkActionService()",
    "this.adminChunkActionService.canOpenChunks(this.chunksAvailableHere())",
    "this.adminChunkActionService.canEditChunks(this.chunksRunning())",
    "this.adminChunkActionService.canCancelChunks(this.chunksRunning())",
    "this.adminChunkActionService.canStartChunks(this.chunksRunning())",
    "this.adminChunkActionService.hasChunkSignal(this.chunksAvailableHere(), this.chunkyReady())",
    "this.adminChunkActionService.chunksButtonLore(bl)",
    "this.adminChunkActionService.dimensionLore(bl)",
    "this.adminChunkActionService.sizeLore(bl)",
    "this.adminChunkActionService.centerLore(bl)",
    "this.adminChunkActionService.isActiveStatus(string)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminChunkActionService: $snippet"
    }
}

Write-Host "Backend admin chunk action contract OK"
