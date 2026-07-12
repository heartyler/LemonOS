param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$Service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendAdminRootClickService.java")
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

$serviceRequired = @(
    "int worldSlot, int atmosphereSlot, int upkeepSlot, boolean worldExpanded",
    "return AdminRootAction.TOGGLE_WORLD;",
    "if (worldExpanded && clickedSlot == atmosphereSlot)",
    "return AdminRootAction.ATMOSPHERE;",
    "if (worldExpanded && clickedSlot == upkeepSlot)",
    "return AdminRootAction.UPKEEP;",
    "TOGGLE_WORLD,",
    "ATMOSPHERE,",
    "UPKEEP;"
)
foreach ($snippet in $serviceRequired) {
    if (-not $Service.Contains($snippet)) { throw "BackendAdminRootClickService missing: $snippet" }
}

$pluginRequired = @(
    "case TOGGLE_WORLD -> this.toggleCareWorldOptions(player);",
    "case ATMOSPHERE -> this.openNextTick(() -> this.openAdminAtmosphere(player));",
    "case UPKEEP -> this.openAdminUpkeep(player);",
    "case TOGGLE_WORLD -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openBedrockAdminWorld(player));",
    "Ui.Care.ATMOSPHERE.slot()",
    "Ui.Care.UPKEEP.slot()"
)
foreach ($snippet in $pluginRequired) {
    if (-not $Plugin.Contains($snippet)) { throw "LemonOSPlugin root toggle wiring missing: $snippet" }
}

Write-Host "Backend admin root toggle contract OK"
