param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxClearPreviewPlanService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxClearPreviewPlanService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxClearPreviewPlanService",
    "ClearPreviewPlan build(BackendSandboxGeometryService.SelectionBounds bounds)",
    "return new ClearPreviewPlan(bounds.minX(), bounds.minY(), bounds.minZ(), bounds.maxX(), bounds.maxY(), bounds.maxZ());",
    "record ClearPreviewPlan(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxClearPreviewPlanService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxClearPreviewPlanService sandboxClearPreviewPlanService;",
    "this.sandboxClearPreviewPlanService = new BackendSandboxClearPreviewPlanService();",
    "BackendSandboxGeometryService.SelectionBounds bounds = this.sandboxGeometryService.bounds(drawingState.first, drawingState.second);",
    "BackendSandboxClearPreviewPlanService.ClearPreviewPlan clearPlan = this.sandboxClearPreviewPlanService.build(bounds);",
    "ClearPreview clearPreview = new ClearPreview(drawingState.first.getWorld(), clearPlan.minX(), clearPlan.minY(), clearPlan.minZ(), clearPlan.maxX(), clearPlan.maxY(), clearPlan.maxZ());",
    "this.finishPreviewCreation(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLEAR, clearPreview, drawingState.first.getWorld(), clearPlan.minX(), clearPlan.minY(), clearPlan.minZ(), clearPlan.maxX(), clearPlan.maxY(), clearPlan.maxZ());"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxClearPreviewPlanService: $snippet"
    }
}

$createClearPreview = [regex]::Match($Plugin, "private void createClearPreview\(Player player, DrawingState drawingState\) \{(?s).*?\n    \}\r?\n\r?\n    private void placeClone")
if (-not $createClearPreview.Success) {
    throw "Could not isolate createClearPreview body."
}

$forbiddenSnippets = @(
    "int n = bounds.minX();",
    "int n2 = bounds.maxX();",
    "int n3 = bounds.minY();",
    "int n4 = bounds.maxY();",
    "int n5 = bounds.minZ();",
    "int n6 = bounds.maxZ();"
)

foreach ($snippet in $forbiddenSnippets) {
    if ($createClearPreview.Value.Contains($snippet)) {
        throw "LemonOSPlugin createClearPreview still owns clear preview planning: $snippet"
    }
}

Write-Host "Backend sandbox clear preview plan contract OK"
