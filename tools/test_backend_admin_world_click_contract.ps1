param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$RemovedServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminWorldClickService.java"
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

if (Test-Path -LiteralPath $RemovedServicePath -PathType Leaf) {
    throw "Obsolete BackendAdminWorldClickService.java must remain removed."
}

$required = @(
    "private void toggleCareWorldOptions(Player player)",
    "CubeeHolder holder = this.currentHolder(player);",
    "if (holder == null || holder.page != CubeePage.ADMIN) return;",
    "holder.worldExpanded = !holder.worldExpanded;",
    "this.renderCareWorldButton(inventory, true);",
    "this.renderCareWorldButton(inventory, false);",
    "private void renderCareWorldButton(Inventory inventory, boolean worldExpanded)",
    "? this.worldLore()",
    ': this.loreLines("keep your world living."));',
    "this.setButton(inventory, Ui.Care.ATMOSPHERE);",
    "this.setButtonLore(inventory, Ui.Care.UPKEEP, this.optionalStatusLore",
    "inventory.setItem(Ui.Care.ATMOSPHERE.slot(), null);",
    "inventory.setItem(Ui.Care.UPKEEP.slot(), null);",
    "boolean worldExpanded;",
    "private void openBedrockAdminWorld(Player player)",
    '.title("World")',
    'String.join((CharSequence)"\n", this.bedrockPlacesText())',
    "Ui.Care.ATMOSPHERE",
    "Ui.Shared.FORM_BACK"
)
foreach ($snippet in $required) {
    if (-not $Plugin.Contains($snippet)) { throw "Care World toggle contract missing: $snippet" }
}

$forbidden = @("ADMIN_WORLD", "CareMode.WORLD", "openAdminWorld(")
foreach ($snippet in $forbidden) {
    if ($Plugin.Contains($snippet)) { throw "Obsolete remembered World-page behavior remains: $snippet" }
}

Write-Host "Backend Care World toggle contract OK"
