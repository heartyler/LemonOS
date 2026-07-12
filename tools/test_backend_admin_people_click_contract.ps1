param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminPeopleClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminPeopleClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminPeopleClickService",
    "AdminPeopleClick action(int clickedSlot, int backSlot, int nextPageSlot, Map<Integer, UUID> slotTargets)",
    "return new AdminPeopleClick(AdminPeopleAction.BACK, null);",
    "return new AdminPeopleClick(AdminPeopleAction.NEXT_PAGE, null);",
    "UUID target = slotTargets == null ? null : slotTargets.get(clickedSlot);",
    "return new AdminPeopleClick(AdminPeopleAction.SELECT, target);",
    "return new AdminPeopleClick(AdminPeopleAction.NONE, null);",
    "record AdminPeopleClick(AdminPeopleAction action, UUID target)",
    "enum AdminPeopleAction",
    "BACK,",
    "NEXT_PAGE,",
    "SELECT;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminPeopleClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminPeopleClickService adminPeopleClickService;",
    "new BackendAdminPeopleClickService()",
    "this.handleAdminPeopleClick(player, n);",
    "private void handleAdminPeopleClick(Player player, int slot)",
    "int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;",
    "this.adminPeopleClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "cubeeHolder == null ? null : cubeeHolder.slotTargets",
    "this.switchCubeeSurface(player, CubeeSurface.HOME);",
    "this.openNextTick(() -> this.openAdmin(player));",
    "case NEXT_PAGE -> this.openNextTick(() -> this.openAdminPeople(player, this.adminPeopleNavigationService.nextLoopPage(pageIndex, this.adminPeopleNavigationService.listPeople(player).size())));",
    "if (targetId.equals(player.getUniqueId()))",
    "this.openNextTick(() -> this.openCareSelf(player, pageIndex));",
    "Player target = Bukkit.getPlayer((UUID)targetId);",
    "this.adminPeopleActionService.canTarget(player, target)",
    "this.openNextTick(() -> this.openAdminPlayer(player, target, pageIndex));",
    "private void addBedrockAdminPeopleListButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, UUID targetId, String lore)",
    "int virtualSlot = 1000;",
    "this.addBedrockAdminPeopleListButton(builder, arrayList, player, new Ui.ButtonSpec(virtualSlot++, Material.PLAYER_HEAD, player2.getName(), null), player2.getUniqueId(), this.adminPeopleNavigationService.bedrockStatus(player, player2));",
    "this.addBedrockAdminPeopleListButton(builder, arrayList, player, Ui.Shared.FORM_BACK, null, null);",
    "Map<Integer, UUID> slotTargets = new HashMap<>();",
    "slotTargets.put(buttonSpec.slot(), targetId);",
    "Ui.Shared.FORM_BACK.slot()",
    "Ui.Care.FIND.slot()",
    "case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> {",
    "case SELECT -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> {",
    "if (adminPeopleClick.target().equals(player.getUniqueId()))",
    "this.openCareSelf(player, 0);",
    "this.openAdminPlayerIfCurrent(player, adminPeopleClick.target(), 0);",
    "case NEXT_PAGE, NONE ->"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminPeopleClickService: $snippet"
    }
}

Write-Host "Backend admin people click contract OK"
