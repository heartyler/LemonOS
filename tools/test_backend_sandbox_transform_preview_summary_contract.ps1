param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxTransformPreviewService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "TransformSummary<P> summarizeChanges(Iterable<P> positions, Function<P, BlockData> oldData, Function<P, BlockData> newData, ToIntFunction<P> xValue, ToIntFunction<P> yValue, ToIntFunction<P> zValue, BiPredicate<BlockData, BlockData> sameExact)",
    "ArrayList<ChangedBlock<P>> changes = new ArrayList<ChangedBlock<P>>();",
    "int minX = Integer.MAX_VALUE;",
    "int maxX = Integer.MIN_VALUE;",
    "BlockData oldBlockData = oldData.apply(position);",
    "BlockData newBlockData = newData.apply(position);",
    "if (sameExact.test(oldBlockData, newBlockData))",
    "changes.add(new ChangedBlock<P>(position, oldBlockData, newBlockData));",
    "minX = Math.min(minX, x);",
    "maxZ = Math.max(maxZ, z);",
    "record ChangedBlock<P>(P position, BlockData oldData, BlockData newData)",
    "record TransformSummary<P>(List<ChangedBlock<P>> changes, int minX, int minY, int minZ, int maxX, int maxY, int maxZ)",
    "boolean empty()"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxTransformPreviewService missing summary behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "BackendSandboxTransformPreviewService.TransformSummary<BlockPosition> transformSummary = this.sandboxTransformPreviewService.summarizeChanges(",
    "transformPlan.affectedPositions()",
    "blockPosition -> world.getBlockAt(blockPosition.x, blockPosition.y, blockPosition.z).getBlockData()",
    "blockPosition -> transformPlan.blockDataByPosition().getOrDefault(blockPosition, blockData)",
    "this::sameExactBlockData",
    "for (BackendSandboxTransformPreviewService.ChangedBlock<BlockPosition> changedBlock : transformSummary.changes())",
    "this.addBlockChange(drawingChange, world, blockPosition.x, blockPosition.y, blockPosition.z, changedBlock.oldData(), changedBlock.newData());",
    "previewStatus = this.sandboxTransformPreviewService.previewStatus(false, false, false, transformSummary.empty());",
    "this.sendSandboxStatus(player, previewStatus.message(), previewStatus.color());",
    "new RotatePreview(world, transformSummary.minX(), transformSummary.minY(), transformSummary.minZ(), transformSummary.maxX(), transformSummary.maxY(), transformSummary.maxZ(), drawingChange)",
    "this.finishPreviewCreation(player, this.sandboxPreviewLifecycleService.transformKind(rotate), rotatePreview, world, transformSummary.minX(), transformSummary.minY(), transformSummary.minZ(), transformSummary.maxX(), transformSummary.maxY(), transformSummary.maxZ());"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to transform preview summary: $snippet"
    }
}

$createTransformPreview = [regex]::Match($Plugin, "private void createTransformPreview\(Player player, DrawingState drawingState, boolean rotate\) \{(?s).*?\n    \}\r?\n\r?\n    private void setPreview")
if (-not $createTransformPreview.Success) {
    throw "Could not isolate createTransformPreview body."
}

$forbiddenSnippets = @(
    "int n = Integer.MAX_VALUE;",
    "int n2 = Integer.MAX_VALUE;",
    "int n3 = Integer.MAX_VALUE;",
    "int n4 = Integer.MIN_VALUE;",
    "int n5 = Integer.MIN_VALUE;",
    "int n6 = Integer.MIN_VALUE;",
    "if (this.sameExactBlockData(block.getBlockData(), blockData2)) continue;",
    "Math.min(n, blockPosition.x)",
    "Math.max(n6, blockPosition.z)",
    "if (drawingChange.blocks.isEmpty())",
    'this.sendSandboxStatus(player, "nothing changed.", NamedTextColor.DARK_GRAY);'
)

foreach ($snippet in $forbiddenSnippets) {
    if ($createTransformPreview.Value.Contains($snippet)) {
        throw "LemonOSPlugin createTransformPreview still owns transform summary policy: $snippet"
    }
}

Write-Host "Backend sandbox transform preview summary contract OK"
