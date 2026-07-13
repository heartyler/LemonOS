param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendConfigMigrationService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$OrchestratorPath = Join-Path $Root "src\main\java\dev\lemonos\BackendConfigMigrationOrchestrator.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendConfigMigrationService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath
$Orchestrator = Get-Content -Raw -LiteralPath $OrchestratorPath

$requiredServiceSnippets = @(
    "final class BackendConfigMigrationService",
    "boolean setMissing(FileConfiguration configuration, String path, Object value)",
    "if (configuration == null || configuration.isSet(path))",
    "return false;",
    "configuration.set(path, value);",
    "return true;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendConfigMigrationService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendConfigMigrationService configMigrationService;",
    "this.configMigrationService = new BackendConfigMigrationService();",
    "private boolean setMissing(FileConfiguration fileConfiguration, String string, Object object)",
    "return this.configMigrationService.setMissing(fileConfiguration, string, object);",
    'this.mainConfigDefaultService.applyCoreDefaults(this.config)',
    'this.configMigrationOrchestrator.migrate(target, this::applyLegacyConfigDefaults, placeDefaults)'
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendConfigMigrationService: $snippet"
    }
}

foreach ($snippet in @(
    "final class BackendConfigMigrationOrchestrator",
    "LegacyDefaultResult legacy = legacyDefaults.apply();",
    "boolean atmosphereSaved = this.save(",
    'if (atmosphereSaved && target.config().contains("atmosphere"))',
    "this.save(target.configFile(), target.config(), configChanged);",
    "private boolean save(File file, FileConfiguration configuration, boolean changed)",
    "this.defaultGroupService.applyMessageDefaults(target.messages())"
)) {
    if (-not $Orchestrator.Contains($snippet)) {
        throw "Backend config migration orchestration missing: $snippet"
    }
}

$adapter = [regex]::Match($Plugin, "private boolean setMissing\(FileConfiguration fileConfiguration, String string, Object object\) \{(?s).*?\n    \}")
if (-not $adapter.Success) {
    throw "Could not isolate LemonOSPlugin setMissing adapter."
}

$forbiddenAdapterSnippets = @(
    "fileConfiguration.isSet(string)",
    "fileConfiguration.set(string, object)"
)

foreach ($snippet in $forbiddenAdapterSnippets) {
    if ($adapter.Value.Contains($snippet)) {
        throw "LemonOSPlugin setMissing adapter still owns migration policy: $snippet"
    }
}

Write-Host "Backend config migration contract OK"
