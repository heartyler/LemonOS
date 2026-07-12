param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxBulkDrawingService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    'BackendSandboxDrawingShapeService.ShapePolicy shapePolicy',
    'this.drawingShapeService.shouldDraw(shapePolicy',
    'if (replace && !this.blockChangeService.sameBlockData(oldData, sourceData))',
    'if (!this.blockChangeService.shouldAddChange(oldData, newData))',
    'consumer.accept(world, x, y, z, oldData, newData);',
    'ApplyStatus applyStatus(boolean verified, boolean changed)',
    'return new ApplyStatus(true, "nothing changed.", NamedTextColor.DARK_GRAY);'
)) { if (-not $service.Contains($required)) { throw "Bulk drawing contract missing: $required" } }
foreach ($required in @(
    'this.drawingShapePolicy(drawingState.action)',
    'drawingState.action == DrawingAction.REPLACE',
    'drawingState.sourceBlockData',
    'blockData'
)) { if (-not $plugin.Contains($required)) { throw "Bulk drawing wiring missing: $required" } }
if ($service.Contains('actionOrdinal') -or $plugin.Contains('drawingState.action.ordinal()')) { throw 'Bulk drawing still depends on enum ordinal.' }
Write-Host "Backend Sandbox bulk drawing contract OK"
