param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$Migration = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendFeatureConfigMigrationService.java")
$Orchestrator = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendConfigMigrationOrchestrator.java")
$HudTemplate = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\hud.yml")
$AtmosphereTemplate = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\atmosphere.yml")
$RecipesTemplate = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\recipes.yml")

$pluginRequired = @(
    'loaded.hudFile()',
    'loaded.legacyBoardsFile()',
    'loaded.atmosphereFile()',
    'loaded.recipesFile()',
    "this.configMigrationOrchestrator.migrate(target, this::applyLegacyConfigDefaults, placeDefaults)",
    'new BackendHudConfig(this.hud)',
    'new BackendAtmosphereConfig(this.atmosphere)',
    'new BackendRecipeBookConfig(this.recipes)',
    "private void startHudTask()",
    "private void updateHuds()",
    'this.hudOrchestrationService.plan(this.currentServer.proxyName, this.hudConfig, HUD_DEFINITIONS)',
    "this.atmosphereOrchestrationService.schedule(this.currentServer == ServerId.SURVIVAL, this.atmosphereConfig)",
    "this.atmosphereOrchestrationService.shouldTick(this.atmosphereConfig, this.resting(), this.restSuspendAtmosphere())",
    "return this.atmosphereConfig.musicTracks(string);"
)
foreach ($snippet in $pluginRequired) { if (-not $Plugin.Contains($snippet)) { throw "Feature config split missing: $snippet" } }

foreach ($snippet in @(
    "this.featureMigrationService.migrateHud(",
    "this.featureMigrationService.migrateAtmosphere(",
    "this.featureMigrationService.migrateRecipes(",
    'if (atmosphereSaved && target.config().contains("atmosphere"))',
    'target.config().set("atmosphere", null)',
    'target.config().set("stayed-close", null)',
    'target.config().set("hud", null)',
    'this.removeLegacyBoardsFile(target.legacyBoardsFile())'
)) { if (-not $Orchestrator.Contains($snippet)) { throw "Feature config orchestration missing: $snippet" } }

$forbiddenPlugin = @("startStayedCloseTask()", "startHudScoreboardTask()", "atmosphereServerEnabled()", '"atmosphere.servers.creative"', '"atmosphere.servers.survival"', 'this.config.getStringList("atmosphere.music.tracks.')
foreach ($snippet in $forbiddenPlugin) { if ($Plugin.Contains($snippet)) { throw "Removed feature architecture remains: $snippet" } }

$migrationRequired = @(
    '"stayed-close", hud, "hud.stayed-close"',
    '"hud", hud, "hud"',
    'legacyBoards, "boards", hud, "hud"',
    'hud.set(path, false)',
    'atmosphere.set("atmosphere.servers", null)',
    "skipServers"
)
foreach ($snippet in $migrationRequired) { if (-not $Migration.Contains($snippet)) { throw "Feature migration missing: $snippet" } }

foreach ($key in @("stayed-close", "made-room", "grew-here", "auto-chain")) {
    if ($HudTemplate -notmatch "(?ms)^  $([regex]::Escape($key)):\s+enabled: false") { throw "hud.yml must default $key to false" }
}
if ($AtmosphereTemplate.Contains("creative:") -or $AtmosphereTemplate.Contains("servers:")) { throw "atmosphere.yml must not expose per-server/Creative switches" }
if ($RecipesTemplate -notmatch '(?ms)^recipe-book:\s+unlock-all:\s+survival: true\s+creative: true') { throw "recipes.yml must own Survival/Creative Recipe Book defaults" }

Write-Host "Backend feature config split contract OK"
