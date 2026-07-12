param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminRequestsClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminRequestsClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminRequestsClickService",
    "AdminRequestsClickResult action(int clickedSlot, int backSlot, int nextPageSlot, Map<Integer, String> slotKeys)",
    "return AdminRequestsClickResult.back();",
    "return AdminRequestsClickResult.nextPage();",
    "String selectedToken = slotKeys == null ? null : slotKeys.get(clickedSlot);",
    "return AdminRequestsClickResult.select(selectedToken);",
    "return AdminRequestsClickResult.none();",
    "enum AdminRequestsAction",
    "BACK,",
    "NEXT_PAGE,",
    "SELECT;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminRequestsClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminRequestsClickService adminRequestsClickService;",
    "new BackendAdminRequestsClickService()",
    "this.handleAdminRequestsClick(player, n);",
    "private void handleAdminRequestsClick(Player player, int slot)",
    "int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;",
    "this.adminRequestsClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "cubeeHolder == null ? null : cubeeHolder.slotKeys",
    "case BACK -> this.openNextTick(() -> this.openAdmin(player));",
    "case NEXT_PAGE -> this.openNextTick(() -> this.openAdminRequests(player, this.adminResetNavigationService.nextLoopPage(pageIndex, this.adminResetNavigationService.tokens().size())));",
    "case SELECT -> this.openNextTick(() -> this.openAdminReset(player, clickResult.selectedToken, pageIndex));",
    "private void addBedrockAdminRequestListButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, String token, String lore)",
    "int virtualSlot = 1000;",
    "this.addBedrockAdminRequestListButton(builder, arrayList, player, new Ui.ButtonSpec(virtualSlot++, Material.PLAYER_HEAD, string2, null), string, this.adminResetNavigationService.requestLore(string2));",
    "this.addBedrockAdminRequestListButton(builder, arrayList, player, Ui.Shared.FORM_BACK, null, null);",
    "Map<Integer, String> slotKeys = new HashMap<>();",
    "slotKeys.put(buttonSpec.slot(), token);",
    "Ui.Shared.FORM_BACK.slot()",
    "Ui.Care.REVIEW.slot()",
    "case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdmin(player));",
    "case SELECT -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> this.openAdminReset(player, clickResult.selectedToken, 0));",
    "case NEXT_PAGE, NONE ->"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminRequestsClickService: $snippet"
    }
}

Write-Host "Backend admin requests click contract OK"
