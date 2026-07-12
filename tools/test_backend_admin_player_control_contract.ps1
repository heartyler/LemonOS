param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminPlayerControlService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminPlayerControlService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminPlayerControlService",
    "private static final int CONTROL_PAGE_MARKER_BASE = -1000;",
    "Predicate<ItemStack> cubeeItem",
    "Predicate<ItemStack> loginItem",
    "int controlPageMarker(int pageIndex)",
    "return CONTROL_PAGE_MARKER_BASE - Math.max(0, pageIndex);",
    "boolean isControlPageMarker(int pageIndex)",
    "return pageIndex <= CONTROL_PAGE_MARKER_BASE;",
    "int controlPageIndex(int marker)",
    "return Math.max(0, CONTROL_PAGE_MARKER_BASE - marker);",
    "boolean isOnline(Player player)",
    "boolean isSelf(Player actor, Player target)",
    "boolean canOpenGamemode(Player target)",
    "boolean gamemodeApplied(Player target, GameMode gameMode)",
    "boolean verifyClearedInventory(Player player)",
    "inventory.getItemInOffHand()",
    "this.cubeeItem.test(itemStack)",
    "this.loginItem.test(itemStack)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminPlayerControlService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminPlayerControlService adminPlayerControlService;",
    "new BackendAdminPlayerControlService(this::isCubee, this::isLoginItem)",
    "this.adminPlayerControlService.canOpenGamemode(player5)",
    "this.adminPlayerControlService.isSelf(player, target)",
    "this.adminPlayerControlService.isControlPageMarker(pageIndex)",
    "this.adminPlayerControlService.controlPageIndex(pageIndex)",
    "this.adminPlayerControlService.controlPageMarker(pageIndex)",
    "this.adminPlayerControlService.canOpenGamemode(player2)",
    "this.adminPlayerControlService.gamemodeApplied(player2, gameMode)",
    "this.adminPlayerControlService.verifyClearedInventory(player)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminPlayerControlService: $snippet"
    }
}

Write-Host "Backend admin player control contract OK"
