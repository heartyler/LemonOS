param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$sourceRoot = Join-Path $Root "src\main\java\dev\lemonos"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $sourceRoot "LemonOSPlugin.java")
$center = Get-Content -Raw -LiteralPath (Join-Path $sourceRoot "BackendSandboxCenterDrawingService.java")
if (Test-Path -LiteralPath (Join-Path $sourceRoot "BackendSandboxSmoothTransformService.java")) { throw "Removed Smooth service returned." }
foreach ($forbidden in @('SMOOTH', 'Smooth', 'smoothAffectedBlocks', 'buildSmoothSnapshot', 'smoothTooLarge', 'SmoothPositionFactory', 'SmoothDataReader')) {
    if ($plugin.Contains($forbidden) -or $center.Contains($forbidden)) { throw "Removed Smooth behavior remains: $forbidden" }
}
Write-Host "Backend Sandbox Smooth removal contract OK"
