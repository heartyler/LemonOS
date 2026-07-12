param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxClonePreviewPlanService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxClonePreviewPlanService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxClonePreviewPlanService",
    "ClonePreviewPlan build(World world, BackendSandboxGeometryService.SelectionBounds bounds, Location location, Predicate<Entity> excludedEntity)",
    "int targetMinX = location.getBlockX();",
    "int targetMinY = location.getBlockY();",
    "int targetMinZ = location.getBlockZ();",
    "int targetMaxX = targetMinX + bounds.sizeX() - 1;",
    "int targetMaxY = targetMinY + bounds.sizeY() - 1;",
    "int targetMaxZ = targetMinZ + bounds.sizeZ() - 1;",
    "for (int x = bounds.minX(); x <= bounds.maxX(); ++x)",
    "for (int y = bounds.minY(); y <= bounds.maxY(); ++y)",
    "for (int z = bounds.minZ(); z <= bounds.maxZ(); ++z)",
    "blocks.add(new SourceBlock(x - bounds.minX(), y - bounds.minY(), z - bounds.minZ(), world.getBlockAt(x, y, z).getBlockData()));",
    "boolean outsideVerticalRange(ClonePreviewPlan plan, int minHeight, int maxHeight)",
    "return plan.minY() < minHeight || plan.maxY() >= maxHeight;",
    "record ClonePreviewPlan(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, List<SourceBlock> blocks, List<SourceEntity> entities)",
    "record SourceBlock(int offsetX, int offsetY, int offsetZ, BlockData blockData)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxClonePreviewPlanService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxClonePreviewPlanService sandboxClonePreviewPlanService;",
    "this.sandboxClonePreviewPlanService = new BackendSandboxClonePreviewPlanService();",
    "clonePlan = this.sandboxClonePreviewPlanService.build(world, bounds, location, this::isSandboxCloneExcludedEntity);",
    "this.sandboxClonePreviewPlanService.outsideVerticalRange(clonePlan, world.getMinHeight(), world.getMaxHeight())",
    "ClonePreview clonePreview = new ClonePreview(world, clonePlan.minX(), clonePlan.minY(), clonePlan.minZ(), clonePlan.maxX(), clonePlan.maxY(), clonePlan.maxZ(), CLONE_RELOCATIONS);",
    "for (BackendSandboxClonePreviewPlanService.SourceBlock sourceBlock : clonePlan.blocks())",
    "clonePreview.blocks.add(new CloneBlock(sourceBlock.offsetX(), sourceBlock.offsetY(), sourceBlock.offsetZ(), sourceBlock.blockData()));",
    "this.finishPreviewCreation(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLONE, clonePreview, world, clonePreview.minX, clonePreview.minY, clonePreview.minZ, clonePreview.maxX, clonePreview.maxY, clonePreview.maxZ);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxClonePreviewPlanService: $snippet"
    }
}

$createClonePreview = [regex]::Match($Plugin, "private void createClonePreview\(Player player, DrawingState drawingState, Location location\) \{(?s).*?\n    \}\r?\n\r?\n    private void createClearPreview")
if (-not $createClonePreview.Success) {
    throw "Could not isolate createClonePreview body."
}

$forbiddenSnippets = @(
    "int n7 = bounds.sizeX() - 1;",
    "int n8 = bounds.sizeY() - 1;",
    "int n9 = bounds.sizeZ() - 1;",
    "clonePreview.minY < world.getMinHeight() || clonePreview.maxY >= world.getMaxHeight()",
    "for (int i =",
    "for (int j =",
    "for (int k =",
    "world.getBlockAt(i, j, k).getBlockData()"
)

foreach ($snippet in $forbiddenSnippets) {
    if ($createClonePreview.Value.Contains($snippet)) {
        throw "LemonOSPlugin createClonePreview still owns clone preview planning: $snippet"
    }
}

Write-Host "Backend sandbox clone preview plan contract OK"
