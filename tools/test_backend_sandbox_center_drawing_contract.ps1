param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxCenterDrawingService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    'int sphereAffectedBlocks(int radius)',
    'void forEachSphereBlock(int centerX, int centerY, int centerZ, int radius, SphereBlockConsumer consumer)',
    'boolean withinVerticalRange(int centerY, int radius, int minHeight, int maxHeight)',
    'boolean sphereTooLarge(int radius, int maxBlocks)',
    'RepeatStatus repeatStatus(boolean changed, boolean repeatDoneShown)',
    'interface SphereBlockConsumer'
)) { if (-not $service.Contains($required)) { throw "Center drawing behavior missing: $required" } }
foreach ($required in @(
    'this.sandboxCenterDrawingService.sphereTooLarge(n, this.sandboxMaxBlocks())',
    'this.sandboxCenterDrawingService.forEachSphereBlock',
    'this.sandboxCenterDrawingService.repeatStatus'
)) { if (-not $plugin.Contains($required)) { throw "Center drawing wiring missing: $required" } }
foreach ($forbidden in @('smoothAffectedBlocks', 'buildSmoothSnapshot', 'smoothTooLarge', 'applySmooth')) {
    if ($service.Contains($forbidden) -or $plugin.Contains($forbidden)) { throw "Removed Smooth center behavior remains: $forbidden" }
}
Write-Host "Backend Sandbox center drawing contract OK"
