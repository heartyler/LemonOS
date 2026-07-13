param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$Build = Get-Content -Raw -LiteralPath (Join-Path $Root "build_lemonos.ps1")
$Harness = Get-Content -Raw -LiteralPath (Join-Path $Root "tools\java\dev\lemonos\BackendConfigMigrationStagingHarness.java")
$Runner = Get-Content -Raw -LiteralPath (Join-Path $Root "tools\run_backend_config_migration_staging.ps1")
foreach ($required in @(
    'run_backend_config_migration_staging.ps1',
    'assertValue(hud, "hud.stayed-close.title", "Legacy Stayclose")',
    'verifyOrchestratedMigration',
    'Failed HUD persistence removed a backward-compatible source.',
    'Failed Recipe Book persistence removed the legacy Survival policy.',
    'Legacy HUD roots survived a successful canonical migration.',
    'assertValue(atmosphere, "atmosphere.enabled", false)',
    'assertValue(recipes, "recipe-book.unlock-all.creative", true)',
    'if (secondPassChanged)',
    'BackendConfigMigrationStagingHarness'
)) {
    if (-not ($Build.Contains($required) -or $Harness.Contains($required) -or $Runner.Contains($required))) {
        throw "Config migration staging coverage missing: $required"
    }
}
Write-Host "Backend config migration staging contract OK"
