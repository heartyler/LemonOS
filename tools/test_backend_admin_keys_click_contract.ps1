param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminKeysClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminKeysClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminKeysClickService",
    "AdminKeysAction action(int clickedSlot, int backSlot, int giveSlot, int holdersSlot)",
    "return AdminKeysAction.BACK;",
    "return AdminKeysAction.GIVE;",
    "return AdminKeysAction.HOLDERS;",
    "return AdminKeysAction.NONE;",
    "enum AdminKeysAction",
    "BACK,",
    "GIVE,",
    "HOLDERS;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminKeysClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminKeysClickService adminKeysClickService;",
    "new BackendAdminKeysClickService()",
    "this.handleAdminKeysClick(player, n);",
    "private void handleAdminKeysClick(Player player, int slot)",
    "this.adminKeysClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "case BACK -> this.openAdmin(player);",
    "case GIVE -> this.openNextTick(() -> this.openAdminKeyPeople(player, 0));",
    "case HOLDERS -> this.openNextTick(() -> this.openAdminKeyHolders(player, 0));",
    "private void addBedrockAdminKeysButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, String lore)",
    "Ui.Shared.FORM_BACK.slot()",
    "Ui.Care.KEY_PEOPLE.slot()",
    "Ui.Care.KEY_HOLDERS.slot()",
    "this.addBedrockAdminKeysButton(builder, arrayList, player, Ui.Care.KEY_PEOPLE, null);",
    "this.addBedrockAdminKeysButton(builder, arrayList, player, Ui.Care.KEY_HOLDERS, this.keyHoldersStatus());",
    "this.addBedrockAdminKeysButton(builder, arrayList, player, Ui.Shared.FORM_BACK, null);",
    "this.switchCubeeSurface(player, CubeeSurface.HOME);",
    "case GIVE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminKeyPeople(player, 0));",
    "case HOLDERS -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> this.openAdminKeyHolders(player, 0));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminKeysClickService: $snippet"
    }
}

Write-Host "Backend admin keys click contract OK"
