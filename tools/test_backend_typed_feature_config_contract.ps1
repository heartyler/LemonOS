param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$SourceRoot = Join-Path $Root "src\main\java\dev\lemonos"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $SourceRoot "LemonOSPlugin.java")
$Hud = Get-Content -Raw -LiteralPath (Join-Path $SourceRoot "BackendHudConfig.java")
$Atmosphere = Get-Content -Raw -LiteralPath (Join-Path $SourceRoot "BackendAtmosphereConfig.java")

foreach ($snippet in @(
    "final class BackendHudConfig implements BackendDisplayConfig",
    'static final String STAYED_CLOSE = "stayed-close"',
    'return "hud." + hudKey',
    "boolean enabled(String hudKey)",
    "int refreshMinutes(String hudKey)",
    "int top(String hudKey)",
    "boolean trackBlocksChanged(String hudKey)"
)) {
    if (-not $Hud.Contains($snippet)) { throw "Typed HUD config missing: $snippet" }
}

foreach ($snippet in @(
    "final class BackendAtmosphereConfig",
    "boolean enabled()",
    "int actionBarDurationSeconds()",
    "boolean musicActionBarEnabled()",
    "boolean activityTriggerEnabled(String key)"
)) {
    if (-not $Atmosphere.Contains($snippet)) { throw "Typed Atmosphere config missing: $snippet" }
}

foreach ($snippet in @(
    "private static final List<BackendHudDefinition> HUD_DEFINITIONS",
    "private void updateHudDisplays()",
    "private void updateHudDisplay(BackendHudDefinition hudDefinition)",
    "return this.hudConfig;",
    "return this.atmosphereConfig.enabled();"
)) {
    if (-not $Plugin.Contains($snippet)) { throw "Typed feature wiring missing: $snippet" }
}

foreach ($legacyRuntimeAccess in @(
    'this.configBoolean("atmosphere.',
    'this.configInt("atmosphere.',
    'this.configDouble("atmosphere.',
    'this.configString("atmosphere.',
    'this.configBoolean("stayed-close.',
    'this.configInt("stayed-close.',
    'private FileConfiguration featureConfig('
)) {
    if ($Plugin.Contains($legacyRuntimeAccess)) { throw "Generic feature config access remains: $legacyRuntimeAccess" }
}

Write-Host "Backend typed feature config contract OK"
