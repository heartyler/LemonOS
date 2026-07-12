param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminPlayerControlClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminPlayerControlClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminPlayerControlClickService",
    "AdminPlayerControlAction action(int clickedSlot, int backSlot, int gamemodeSlot, int clearSlot, int sendSlot)",
    "return AdminPlayerControlAction.BACK;",
    "return AdminPlayerControlAction.GAMEMODE;",
    "return AdminPlayerControlAction.CLEAR;",
    "return AdminPlayerControlAction.SEND;",
    "return AdminPlayerControlAction.NONE;",
    "enum AdminPlayerControlAction",
    "GAMEMODE,",
    "CLEAR,",
    "SEND;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminPlayerControlClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminPlayerControlClickService adminPlayerControlClickService;",
    "new BackendAdminPlayerControlClickService()",
    "this.handleAdminPlayerControlClick(player, player7, n25, n);",
    "private void handleAdminPlayerControlClick(Player player, Player target, int pageIndex, int slot)",
    "this.adminPlayerControlClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "12,",
    "14);",
    "case BACK ->",
    "target != null && target.isOnline()",
    "this.openNextTick(() -> this.openAdminPlayer(player, target, pageIndex));",
    "this.openNextTick(() -> this.openAdminPeople(player, pageIndex));",
    "case GAMEMODE ->",
    "this.adminPeopleActionService.canTarget(player, target)",
    "this.openNextTick(() -> this.openAdminGamemode(player, target, this.adminPlayerControlService.controlPageMarker(pageIndex)));",
    "case CLEAR ->",
    "case SEND ->",
    "this.openNextTick(() -> this.openAdminPlayerSendPlaces(player, target, pageIndex));",
    "this.openNextTick(() -> this.openAdminPlayerClearConfirm(player, target, pageIndex));",
    "private void addBedrockAdminPlayerControlActionButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, int pageIndex, Ui.ButtonSpec buttonSpec)",
    "BackendAdminPlayerControlClickService.AdminPlayerControlAction action = this.adminPlayerControlClickService.action(",
    "case GAMEMODE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminGamemodeIfCurrent(player, target.getUniqueId(), this.adminControlPageMarker(pageIndex)));",
    "case CLEAR -> this.bedrockButton(builder, actions, buttonSpec, () ->",
    "Player currentTarget = Bukkit.getPlayer((UUID)target.getUniqueId());",
    "this.openBedrockAdminPlayerClearConfirm(player, currentTarget, pageIndex);",
    "this.openAdminPeople(player, pageIndex);",
    "this.addBedrockAdminPlayerControlActionButton(builder, arrayList, player, player2, n, Ui.Shared.FORM_BACK);",
    "this.addBedrockAdminPlayerControlActionButton(builder, arrayList, player, player2, n, Ui.CarePlayer.SEND);",
    "Ui.Shared.FORM_BACK.slot()",
    "case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminPlayerIfCurrent(player, target.getUniqueId(), pageIndex));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminPlayerControlClickService: $snippet"
    }
}

Write-Host "Backend admin player control click contract OK"
