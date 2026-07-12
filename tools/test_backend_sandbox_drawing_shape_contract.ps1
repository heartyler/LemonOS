param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxDrawingShapeService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    'boolean shouldDraw(ShapePolicy policy',
    'case VOLUME -> true;',
    'case FLOOR -> y == minY;',
    'case WALL -> x == minX || x == maxX || z == minZ || z == maxZ;',
    'case NONE -> false;',
    'enum ShapePolicy'
)) { if (-not $service.Contains($required)) { throw "Semantic shape policy missing: $required" } }
foreach ($required in @(
    'case SET, CLEAR, REPLACE -> BackendSandboxDrawingShapeService.ShapePolicy.VOLUME;',
    'case WALL -> BackendSandboxDrawingShapeService.ShapePolicy.WALL;',
    'case FLOOR -> BackendSandboxDrawingShapeService.ShapePolicy.FLOOR;',
    'case CLONE, CIRCLE, FLIP, ROTATE -> BackendSandboxDrawingShapeService.ShapePolicy.NONE;'
)) { if (-not $plugin.Contains($required)) { throw "Drawing action policy mapping missing: $required" } }
foreach ($forbidden in @('actionOrdinal', 'drawingAction.ordinal()')) {
    if ($service.Contains($forbidden) -or $plugin.Contains($forbidden)) { throw "Ordinal shape coupling remains: $forbidden" }
}
Write-Host "Backend semantic drawing shape contract OK"
