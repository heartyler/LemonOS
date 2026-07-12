param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminAtmosphereClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminAtmosphereClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminAtmosphereClickService",
    "AdminAtmosphereClick action(int clickedSlot, int backSlot, BackendAdminWorldNavigationService<?> worldNavigationService)",
    "return new AdminAtmosphereClick(AdminAtmosphereAction.BACK, null, null);",
    "String time = worldNavigationService.timeForSlot(clickedSlot);",
    "return new AdminAtmosphereClick(AdminAtmosphereAction.TIME, time, null);",
    "Boolean weather = worldNavigationService.weatherForSlot(clickedSlot);",
    "return new AdminAtmosphereClick(AdminAtmosphereAction.WEATHER, null, weather);",
    "return new AdminAtmosphereClick(AdminAtmosphereAction.NONE, null, null);",
    "record AdminAtmosphereClick(AdminAtmosphereAction action, String time, Boolean weather)",
    "enum AdminAtmosphereAction",
    "BACK,",
    "TIME,",
    "WEATHER;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminAtmosphereClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminAtmosphereClickService adminAtmosphereClickService;",
    "new BackendAdminAtmosphereClickService()",
    "this.handleAdminAtmosphereClick(player, n);",
    "private void handleAdminAtmosphereClick(Player player, int slot)",
    "this.adminAtmosphereClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "this.adminWorldNavigationService",
    "case BACK -> this.openNextTick(() -> this.openAdmin(player));",
    "case TIME ->",
    "this.setAdminTime(player, adminAtmosphereClick.time());",
    "case WEATHER ->",
    "this.setAdminWeather(player, adminAtmosphereClick.weather());",
    "player.closeInventory();",
    "private void addBedrockAdminAtmosphereButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec)",
    "Ui.Shared.FORM_BACK.slot()",
    "case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openBedrockAdminWorld(player));",
    "case TIME -> this.bedrockButton((Object)builder, actions, buttonSpec, null, () -> this.setAdminTime(player, click.time()));",
    "case WEATHER -> this.bedrockButton((Object)builder, actions, buttonSpec, null, () -> this.setAdminWeather(player, click.weather()));",
    "this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Atmosphere.DAY);",
    "this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Atmosphere.NIGHT);",
    "this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Atmosphere.RAIN);",
    "this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Atmosphere.CLEAR);",
    "this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Shared.FORM_BACK);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminAtmosphereClickService: $snippet"
    }
}

Write-Host "Backend admin atmosphere click contract OK"
