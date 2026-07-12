param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxInputModelService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @('Range sizeRange()', 'return new Range(1, 16);', 'String sizeGuidance()', 'return "Type 1-16.";', 'String failureMessage(int value, Range range)', 'boolean inRange(int value, Range range)')) {
    if (-not $service.Contains($required)) { throw "Sandbox input model missing: $required" }
}
foreach ($required in @('this.sandboxInputModelService.sizeGuidance()', 'this.sandboxInputModelService.sizeRange()')) {
    if (-not $plugin.Contains($required)) { throw "Sandbox input model wiring missing: $required" }
}
foreach ($forbidden in @('strengthRange', 'strengthGuidance', 'smoothSize', 'DrawingInputStep.STRENGTH', 'DrawingAction.SMOOTH')) {
    if ($service.Contains($forbidden) -or $plugin.Contains($forbidden)) { throw "Removed Smooth input remains: $forbidden" }
}
Write-Host "Backend Sandbox input model contract OK"
