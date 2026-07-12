param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxTransformPreviewService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxTransformPreviewService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxTransformPreviewService",
    "TransformPlan<P> build(BackendSandboxGeometryService.SelectionBounds bounds, PositionFactory<P> positionFactory, BlockTransform<P> blockTransform)",
    "HashMap<P, BlockData> blockDataByPosition = new HashMap<P, BlockData>();",
    "HashSet<P> affectedPositions = new HashSet<P>();",
    "affectedPositions.add(sourcePosition);",
    "affectedPositions.add(transformedBlock.position());",
    "blockDataByPosition.put(transformedBlock.position(), transformedBlock.blockData());",
    "boolean tooManyPositions(TransformPlan<P> plan, int maxBlocks)",
    "boolean allWithinVerticalRange(Iterable<P> positions, ToIntFunction<P> yValue, int minHeight, int maxHeight)",
    "PreviewStatus previewStatus(boolean invalidSelection, boolean tooManyPositions, boolean outsideVerticalRange, boolean empty)",
    'return new PreviewStatus(false, true, "too large.", NamedTextColor.DARK_GRAY);',
    'return new PreviewStatus(false, true, "nothing changed.", NamedTextColor.DARK_GRAY);',
    'return new PreviewStatus(true, false, "", NamedTextColor.GRAY);',
    "PreviewFailureLifecycle previewFailureLifecycle(PreviewStatus status)",
    "return new PreviewFailureLifecycle(!status.ready(), status.sendStatus());",
    "record TransformedBlock<P>(P position, BlockData blockData)",
    "record TransformPlan<P>(HashSet<P> affectedPositions, HashMap<P, BlockData> blockDataByPosition)",
    "record PreviewStatus(boolean ready, boolean sendStatus, String message, NamedTextColor color)",
    "record PreviewFailureLifecycle(boolean removeDrawingState, boolean sendStatus)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxTransformPreviewService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxTransformPreviewService sandboxTransformPreviewService;",
    "this.sandboxTransformPreviewService = new BackendSandboxTransformPreviewService();",
    "this.createTransformPreview(player, drawingState, true);",
    "this.createTransformPreview(player, drawingState, false);",
    "BackendSandboxTransformPreviewService.TransformPlan<BlockPosition> transformPlan = this.sandboxTransformPreviewService.build(bounds",
    "this.sandboxTransformPreviewService.tooManyPositions(transformPlan, this.sandboxMaxBlocks())",
    "this.sandboxTransformPreviewService.allWithinVerticalRange(transformPlan.affectedPositions(), blockPosition -> blockPosition.y, world.getMinHeight(), world.getMaxHeight())",
    "BackendSandboxTransformPreviewService.PreviewStatus previewStatus = this.sandboxTransformPreviewService.previewStatus(!this.validSelection(player, drawingState), false, false, false);",
    "previewStatus = this.sandboxTransformPreviewService.previewStatus(false, this.sandboxTransformPreviewService.tooManyPositions(transformPlan, this.sandboxMaxBlocks()), false, false);",
    "previewStatus = this.sandboxTransformPreviewService.previewStatus(false, false, !this.sandboxTransformPreviewService.allWithinVerticalRange(transformPlan.affectedPositions(), blockPosition -> blockPosition.y, world.getMinHeight(), world.getMaxHeight()), false);",
    "if (this.finishFailedTransformPreview(player, previewStatus))",
    "private boolean finishFailedTransformPreview(Player player, BackendSandboxTransformPreviewService.PreviewStatus previewStatus)",
    "BackendSandboxTransformPreviewService.PreviewFailureLifecycle lifecycle = this.sandboxTransformPreviewService.previewFailureLifecycle(previewStatus);",
    "if (lifecycle.removeDrawingState())",
    "this.removeDrawingState(player.getUniqueId());",
    "if (lifecycle.sendStatus())",
    "this.sendSandboxStatus(player, previewStatus.message(), previewStatus.color());",
    "transformPlan.blockDataByPosition().getOrDefault(blockPosition, blockData)",
    "this.finishPreviewCreation(player, this.sandboxPreviewLifecycleService.transformKind(rotate), rotatePreview, world, transformSummary.minX(), transformSummary.minY(), transformSummary.minZ(), transformSummary.maxX(), transformSummary.maxY(), transformSummary.maxZ());",
    "case ROTATE -> this.sandboxPreviewService.setRotate(uUID, preview);",
    "case FLIP -> this.sandboxPreviewService.setFlip(uUID, preview);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxTransformPreviewService: $snippet"
    }
}

$createTransformPreview = [regex]::Match($Plugin, "private void createTransformPreview\(Player player, DrawingState drawingState, boolean rotate\) \{(?s).*?\n    \}\r?\n\r?\n    private boolean finishFailedTransformPreview")
if (-not $createTransformPreview.Success) {
    throw "Could not isolate createTransformPreview body."
}

$forbiddenPreviewSnippets = @(
    "if (!previewStatus.ready())",
    "this.removeDrawingState(player.getUniqueId());`r`n            this.sendSandboxStatus(player, previewStatus.message(), previewStatus.color());"
)

foreach ($snippet in $forbiddenPreviewSnippets) {
    if ($createTransformPreview.Value.Contains($snippet)) {
        throw "LemonOSPlugin createTransformPreview still owns duplicated transform preview failure lifecycle: $snippet"
    }
}

Write-Host "Backend sandbox transform preview contract OK"
