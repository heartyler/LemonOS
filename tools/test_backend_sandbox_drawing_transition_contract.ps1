param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxDrawingTransitionService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    'return "CIRCLE".equals(action);',
    'return "CIRCLE".equals(action) || "REPLACE".equals(action) || "FLIP".equals(action) || "ROTATE".equals(action);',
    'return new StartPlan(this.startsWithSize(action), this.usesMoreTool(action));',
    'return BlockClickRoute.CIRCLE_BLOCK_PICK;',
    'return BlockClickRoute.CIRCLE_CENTER_APPLY;',
    'return BlockClickRoute.REPLACE_SOURCE_PICK;',
    'return SelectionTransition.FLIP_INPUT;',
    'return SelectionTransition.ROTATION_INPUT;',
    'record StartPlan(boolean startsWithSize, boolean moreTool)'
)) { if (-not $service.Contains($required)) { throw "Drawing transition behavior missing: $required" } }
foreach ($required in @(
    'this.sandboxDrawingTransitionService.startPlan(drawingAction.name())',
    'this.sandboxInputModelService.sizeGuidance()',
    'this.sandboxDrawingTransitionService.blockClickRoute',
    'case CIRCLE_CENTER_APPLY -> this.applySphere',
    'case CIRCLE_BLOCK -> this.handleCircleBlockInput'
)) { if (-not $plugin.Contains($required)) { throw "Drawing transition wiring missing: $required" } }
foreach ($forbidden in @('SMOOTH', 'smoothSize', 'STRENGTH', 'handleStrengthInput', 'applyCenterDrawing')) {
    if ($service.Contains($forbidden) -or $plugin.Contains($forbidden)) { throw "Removed Smooth transition remains: $forbidden" }
}
Write-Host "Backend Sandbox drawing transition contract OK"
