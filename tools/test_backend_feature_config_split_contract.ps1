param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$Migration = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendFeatureConfigMigrationService.java")
$Orchestrator = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendConfigMigrationOrchestrator.java")
$BoardsTemplate = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\boards.yml")
$AtmosphereTemplate = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\atmosphere.yml")

$pluginRequired = @(
    'loaded.boardsFile()',
    'loaded.atmosphereFile()',
    "this.configMigrationOrchestrator.migrate(target, this::applyLegacyConfigDefaults, placeDefaults)",
    'new BackendBoardConfig(this.boards)',
    'new BackendAtmosphereConfig(this.atmosphere)',
    'private String legacyBoardPath(String path)',
    'return "boards." + path;',
    'return "boards." + path.substring("hud.".length());',
    "private void startBoardTask()",
    "private void updateBoards()",
    'this.boardOrchestrationService.plan(this.currentServer.proxyName, this.boardConfig, BOARD_DEFINITIONS)',
    "this.atmosphereOrchestrationService.schedule(this.currentServer == ServerId.SURVIVAL, this.atmosphereConfig)",
    "this.atmosphereOrchestrationService.shouldTick(this.atmosphereConfig, this.resting(), this.restSuspendAtmosphere())"
)
foreach ($snippet in $pluginRequired) { if (-not $Plugin.Contains($snippet)) { throw "Feature config split missing: $snippet" } }

foreach ($snippet in @(
    "this.featureMigrationService.migrateBoards(",
    "this.featureMigrationService.migrateAtmosphere("
)) { if (-not $Orchestrator.Contains($snippet)) { throw "Feature config orchestration missing: $snippet" } }

$forbiddenPlugin = @("startStayedCloseTask()", "startHudScoreboardTask()", "atmosphereServerEnabled()", '"atmosphere.servers.creative"', '"atmosphere.servers.survival"')
foreach ($snippet in $forbiddenPlugin) { if ($Plugin.Contains($snippet)) { throw "Removed feature architecture remains: $snippet" } }

$migrationRequired = @(
    '"stayed-close", boards, "boards.stayed-close"',
    '"hud.made-room", boards, "boards.made-room"',
    '"hud.grew-here", boards, "boards.grew-here"',
    '"hud.auto-chain", boards, "boards.auto-chain"',
    'boards.set(path, false)',
    'atmosphere.set("atmosphere.servers", null)',
    "skipServers"
)
foreach ($snippet in $migrationRequired) { if (-not $Migration.Contains($snippet)) { throw "Feature migration missing: $snippet" } }

foreach ($key in @("stayed-close", "made-room", "grew-here", "auto-chain")) {
    if ($BoardsTemplate -notmatch "(?ms)^  $([regex]::Escape($key)):\s+enabled: false") { throw "boards.yml must default $key to false" }
}
if ($AtmosphereTemplate.Contains("creative:") -or $AtmosphereTemplate.Contains("servers:")) { throw "atmosphere.yml must not expose per-server/Creative switches" }

Write-Host "Backend feature config split contract OK"
