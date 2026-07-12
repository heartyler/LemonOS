param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminChunksConfirmClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminChunksConfirmClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminChunksConfirmClickService",
    "AdminChunksConfirmAction action(int clickedSlot, int startSlot, int cancelSlot)",
    "return AdminChunksConfirmAction.START;",
    "return AdminChunksConfirmAction.CANCEL;",
    "return AdminChunksConfirmAction.NONE;",
    "enum AdminChunksConfirmAction",
    "START,",
    "CANCEL;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminChunksConfirmClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminChunksConfirmClickService adminChunksConfirmClickService;",
    "new BackendAdminChunksConfirmClickService()",
    "this.handleAdminChunksConfirmClick(player, n);",
    "private void handleAdminChunksConfirmClick(Player player, int slot)",
    "this.adminChunksConfirmClickService.action(",
    "14,",
    "12);",
    "case START -> this.startChunks(player);",
    "case CANCEL ->",
    "player.closeInventory();",
    'player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));'
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminChunksConfirmClickService: $snippet"
    }
}

Write-Host "Backend admin chunks confirm click contract OK"
