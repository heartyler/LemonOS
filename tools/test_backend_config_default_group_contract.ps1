param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendConfigDefaultGroupService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$OrchestratorPath = Join-Path $Root "src\main\java\dev\lemonos\BackendConfigMigrationOrchestrator.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendConfigDefaultGroupService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath
$Orchestrator = Get-Content -Raw -LiteralPath $OrchestratorPath

$requiredServiceSnippets = @(
    "final class BackendConfigDefaultGroupService",
    "boolean applyMessageDefaults(FileConfiguration messages)",
    'this.setMissing(messages, "labels.create", "Create")',
    'this.setMissing(messages, "labels.enter", "Sign in")',
    'this.setMissing(messages, "results.not-available-here", "out of range.")',
    'this.setMissing(messages, "atmosphere.weather.thunder", List.of("the storm is near.", "the sky is loud.", "the house feels safe.", "stay close.", "the world feels far."))',
    "boolean applyPlaceDefaults(FileConfiguration places, List<PlaceDefault> defaults)",
    'String path = "places." + place.server() + "."',
    'this.setMissing(places, path + "status", "ready")',
    "boolean applySandboxDefaults(FileConfiguration sandbox)",
    'this.setMissing(sandbox, "sandbox.max-blocks", 32768)',
    'this.setMissing(sandbox, "sandbox.replace-target-material", "OAK_PLANKS")',
    "boolean applySurvivalDefaults(FileConfiguration survival)",
    'this.setMissing(survival, "survival.auto-plant.sugar-cane.radius", 32)',
    'this.setMissing(survival, "survival.chest-sort.containers", List.of("CHEST", "BARREL", "SHULKER_BOX"))',
    'this.setMissing(survival, "survival.chain-status.enabled", true)',
    "return this.configMigrationService.setMissing(configuration, path, value);",
    "record PlaceDefault(String name, String server, String item, String lore)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendConfigDefaultGroupService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendConfigDefaultGroupService configDefaultGroupService;",
    "this.configDefaultGroupService = new BackendConfigDefaultGroupService(this.configMigrationService);",
    "for (ServerId serverId : ServerId.values())",
    "this.defaultPlaceMaterial(serverId).name()",
    "this.configMigrationOrchestrator.migrate(target, this::applyLegacyConfigDefaults, placeDefaults)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendConfigDefaultGroupService: $snippet"
    }
}

$requiredOrchestratorSnippets = @(
    "this.defaultGroupService.applyMessageDefaults(target.messages())",
    "this.defaultGroupService.applyPlaceDefaults(target.places(), placeDefaults)",
    "this.defaultGroupService.applySandboxDefaults(target.sandbox())",
    "this.defaultGroupService.applySurvivalDefaults(target.survival())"
)
foreach ($snippet in $requiredOrchestratorSnippets) {
    if (-not $Orchestrator.Contains($snippet)) {
        throw "BackendConfigMigrationOrchestrator is not wired to default groups: $snippet"
    }
}

$migrationStart = $Plugin.IndexOf("private void migrateLemonOSConfig()")
$migrationEnd = $Plugin.IndexOf("private boolean setMissing", $migrationStart)
if ($migrationStart -lt 0 -or $migrationEnd -lt 0) {
    throw "Could not isolate LemonOSPlugin migrateLemonOSConfig method."
}
$migration = $Plugin.Substring($migrationStart, $migrationEnd - $migrationStart)

$forbiddenMigrationSnippets = @(
    "this.setMissing(this.messages,",
    "this.setMissing(this.sharedPlacesConfig,",
    "this.setMissing(this.sandbox,",
    "this.setMissing(this.survival,"
)

foreach ($snippet in $forbiddenMigrationSnippets) {
    if ($migration.Contains($snippet)) {
        throw "LemonOSPlugin still owns extracted default group: $snippet"
    }
}

Write-Host "Backend config default group contract OK"
