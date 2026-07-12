param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendHudConfigMigrationService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendHudConfigMigrationService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendHudConfigMigrationService",
    "BackendHudConfigMigrationService(BackendConfigMigrationService configMigrationService)",
    "boolean setBoardDefaults(FileConfiguration config, Board board)",
    'configPath + ".enabled", false',
    'configPath + ".title", board.defaultTitle()',
    'configPath + ".subtitle", board.defaultSubtitle()',
    'configPath + ".bottom-line", board.defaultBottomLine()',
    'configPath + ".refresh-minutes", 1',
    'configPath + ".top", 5',
    'configPath + ".name-width", 12',
    'configPath + ".scoring.track-blocks-changed", true',
    "private boolean setDisplayDefaults(FileConfiguration config, String path, double x, double y, double z)",
    'path + ".world", "world"',
    'path + ".yaw", 90.0',
    'path + ".pitch", 0.0',
    'path + ".billboard", "fixed"',
    'path + ".scale", 0.53',
    'path + ".row-gap", -0.13',
    'path + ".bottom-offset-y", -1.52',
    'path + ".name-offset-z", -0.30',
    'path + ".value-offset-z", 0.46',
    'path + ".view-range", 32',
    'path + ".line-width", 220',
    'path + ".bedrock.enabled", false',
    'path + ".bedrock.row-gap", -0.16',
    'path + ".bedrock.bottom-offset-y", -1.02',
    "private boolean migrateGeneratedDefaults(FileConfiguration config, Board board, String displayPath)",
    "this.positionMatches(config, displayPath, 9.20, -60.86, 0.5)",
    "this.positionMatches(config, displayPath, 12.90, -60.86, 0.5)",
    "private boolean migrateAutoChainDisplay(FileConfiguration config, Board board, String displayPath)",
    '"Chain"',
    '"where work carries on."',
    '"chains completed."',
    "this.positionMatches(config, displayPath, 5.42, -60.86, -0.5)",
    "private void applyDisplayBlueprintDefaults(FileConfiguration config, Board board, String path)",
    'config.set(path + ".title-offset-y", 0.15);',
    'config.set(path + ".subtitle-offset-y", 0.0);',
    'config.set(path + ".bottom-offset-y", -1.52);',
    'config.set(path + ".name-offset-z", -0.30);',
    'config.set(path + ".value-offset-z", 0.46);',
    "return Math.abs(value - expected) < 1.0E-6;",
    "record Board(String dataKey, String configPath, String defaultTitle, String defaultSubtitle, String defaultBottomLine,"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendHudConfigMigrationService missing required behavior snippet: $snippet"
    }
}

if ($Service.Contains('"ideas shaped in Sandbox.".equals(config.getString(configPath + ".bottom-line"))')) {
    throw "HUD migration treats the current Sandbox default as a legacy value and becomes non-idempotent."
}

$requiredPluginSnippets = @(
    "private BackendHudConfigMigrationService hudConfigMigrationService;",
    "this.hudConfigMigrationService = new BackendHudConfigMigrationService(this.configMigrationService);",
    "private boolean setBoardDefaults(BackendBoardDefinition boardDefinition)",
    "BackendHudConfigMigrationService.Board board = new BackendHudConfigMigrationService.Board(",
    "boardDefinition.dataKey(),",
    "boardDefinition.configPath(),",
    "boardDefinition.defaultTitle(),",
    "boardDefinition.defaultSubtitle(),",
    "boardDefinition.defaultBottomLine(),",
    "boardDefinition.defaultX(),",
    "boardDefinition.defaultY(),",
    "boardDefinition.defaultZ(),",
    "boardDefinition.trackBlocksChanged());",
    "boolean changed = this.hudConfigMigrationService.setBoardDefaults(this.boards, board);",
    "this.boardsConfigDirty |= changed;"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendHudConfigMigrationService: $snippet"
    }
}

$forbiddenPluginSnippets = @(
    "private boolean setHudDisplayDefaults(",
    "private boolean migrateGeneratedHudBoardDefaults(",
    "private boolean migrateAutoChainHudDisplay(",
    "private void applyHudDisplayBlueprintDefaults(",
    "private boolean nearly(double"
)

foreach ($snippet in $forbiddenPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still owns HUD config migration behavior: $snippet"
    }
}

Write-Host "Backend HUD config migration contract OK"
