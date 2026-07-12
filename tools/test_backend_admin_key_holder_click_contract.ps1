param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminKeyHolderClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminKeyHolderClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminKeyHolderClickService",
    "AdminKeyHolderAction action(int clickedSlot, int backSlot, int takeSlot, String accessName)",
    "return AdminKeyHolderAction.BACK;",
    "if (clickedSlot == takeSlot && accessName != null)",
    "return AdminKeyHolderAction.TAKE;",
    "return AdminKeyHolderAction.NONE;",
    "enum AdminKeyHolderAction",
    "BACK,",
    "TAKE;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminKeyHolderClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminKeyHolderClickService adminKeyHolderClickService;",
    "new BackendAdminKeyHolderClickService()",
    "this.handleAdminKeyHolderClick(player, n);",
    "private void handleAdminKeyHolderClick(Player player, int slot)",
    "int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;",
    "String accessName = cubeeHolder == null ? null : cubeeHolder.accessName;",
    "this.adminKeyHolderClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "case BACK -> this.openNextTick(() -> this.openAdminKeyHolders(player, pageIndex));",
    "case TAKE -> this.openNextTick(() -> this.openAdminTakeKeyConfirm(player, accessName, pageIndex));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminKeyHolderClickService: $snippet"
    }
}

Write-Host "Backend admin key holder click contract OK"
