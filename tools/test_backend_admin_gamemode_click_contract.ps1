param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminGamemodeClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminGamemodeClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminGamemodeClickService",
    "AdminGamemodeClick action(int clickedSlot, int backSlot, GameMode currentGameMode)",
    "return new AdminGamemodeClick(AdminGamemodeAction.BACK, null);",
    "GameMode gameMode = this.gameModeForSlot(clickedSlot);",
    "if (gameMode == currentGameMode)",
    "return new AdminGamemodeClick(AdminGamemodeAction.CURRENT, gameMode);",
    "return new AdminGamemodeClick(AdminGamemodeAction.SELECT, gameMode);",
    "return new AdminGamemodeClick(AdminGamemodeAction.NONE, null);",
    "case 11 -> GameMode.ADVENTURE",
    "case 12 -> GameMode.SURVIVAL",
    "case 13 -> GameMode.CREATIVE",
    "case 14 -> GameMode.SPECTATOR",
    "record AdminGamemodeClick",
    "enum AdminGamemodeAction",
    "BACK,",
    "CURRENT,",
    "SELECT;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminGamemodeClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminGamemodeClickService adminGamemodeClickService;",
    "new BackendAdminGamemodeClickService()",
    "this.handleAdminGamemodeClick(player, player5, n20, n);",
    "private void handleAdminGamemodeClick(Player player, Player target, int pageIndex, int slot)",
    "this.adminGamemodeClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "target.getGameMode()",
    "case BACK ->",
    "this.adminPlayerControlService.isSelf(player, target)",
    "this.openNextTick(() -> this.openCareSelf(player, Math.max(0, pageIndex)));",
    "this.adminPlayerControlService.isControlPageMarker(pageIndex)",
    "this.adminPlayerControlService.controlPageIndex(pageIndex)",
    "this.openNextTick(() -> this.openAdminPlayerControl(player, target, controlPageIndex));",
    "this.openNextTick(() -> this.openAdminPlayer(player, target, pageIndex));",
    "case SELECT ->",
    "player.closeInventory();",
    "this.setAdminGamemode(player, target.getUniqueId(), click.gameMode());",
    "case CURRENT ->",
    "private void addBedrockAdminGamemodeButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, int pageIndex, Ui.ButtonSpec buttonSpec)",
    "this.addBedrockAdminGamemodeButton(builder, arrayList, player, player2, n, Ui.Shared.FORM_BACK);",
    "buttonSpec.slot()",
    "Ui.Shared.FORM_BACK.slot()",
    "case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> {",
    "this.adminPlayerControlService.isSelf(player, target)",
    "this.openAdminPeople(player, pageIndex);",
    "this.openAdminPlayerIfCurrent(player, target.getUniqueId(), pageIndex);",
    "case SELECT ->",
    "GameMode gameMode = click.gameMode();",
    "this.bedrockButton((Object)builder, actions, buttonSpec, target.getGameMode() == gameMode ? `"current.`" : null, () -> this.setAdminGamemode(player, target.getUniqueId(), gameMode));",
    "case CURRENT -> this.bedrockButton((Object)builder, actions, buttonSpec, `"current.`", () -> this.openBedrockAdminGamemode(player, target, pageIndex));",
    "if (player2.getGameMode() == gameMode)",
    "case NONE ->"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminGamemodeClickService: $snippet"
    }
}

Write-Host "Backend admin gamemode click contract OK"
