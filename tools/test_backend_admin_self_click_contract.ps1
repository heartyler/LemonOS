param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminSelfClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminSelfClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminSelfClickService",
    "AdminSelfAction action(int clickedSlot, int backSlot, int gamemodeSlot, int clearSlot)",
    "return AdminSelfAction.BACK;",
    "return AdminSelfAction.GAMEMODE;",
    "return AdminSelfAction.CLEAR;",
    "return AdminSelfAction.NONE;",
    "enum AdminSelfAction",
    "BACK,",
    "GAMEMODE,",
    "CLEAR;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminSelfClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminSelfClickService adminSelfClickService;",
    "new BackendAdminSelfClickService()",
    "this.handleAdminSelfClick(player, n);",
    "private void handleAdminSelfClick(Player player, int slot)",
    "int pageIndex = cubeeHolder == null ? -1 : cubeeHolder.pageIndex;",
    "this.adminSelfClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "case BACK ->",
    "this.openNextTick(() -> this.openAdminPeople(player, pageIndex));",
    "this.openNextTick(() -> this.openAdmin(player));",
    "case GAMEMODE ->",
    "this.openRecoveryTick(() -> this.openCubee(player));",
    "this.openNextTick(() -> this.openAdminGamemode(player, player, pageIndex));",
    "case CLEAR ->",
    "this.openNextTick(() -> this.openCareSelfClearConfirm(player));",
    "private void addBedrockCareSelfButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, int pageIndex, Ui.ButtonSpec buttonSpec)",
    "this.addBedrockCareSelfButton(builder, arrayList, player, n, new Ui.ButtonSpec(Ui.CareSelf.GAMEMODE.slot(), this.gamemodeMaterial(player.getGameMode()), Ui.CareSelf.GAMEMODE.title(), Ui.CareSelf.GAMEMODE.lore()));",
    "this.addBedrockCareSelfButton(builder, arrayList, player, n, Ui.CareSelf.CLEAR);",
    "this.addBedrockCareSelfButton(builder, arrayList, player, n, Ui.Shared.FORM_BACK);",
    "Ui.Shared.FORM_BACK.slot()",
    "Ui.CareSelf.GAMEMODE.slot()",
    "Ui.CareSelf.CLEAR.slot()",
    "case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminPeople(player, pageIndex));",
    "case GAMEMODE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminGamemode(player, player, pageIndex));",
    "case CLEAR -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openBedrockCareSelfClearConfirm(player, pageIndex));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminSelfClickService: $snippet"
    }
}

Write-Host "Backend admin self click contract OK"
