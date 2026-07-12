param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxGeometryService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxGeometryService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxGeometryService",
    "boolean validSelection(Location first, Location second, int maxBlocks)",
    "first == null || second == null || first.getWorld() == null || !first.getWorld().equals(second.getWorld())",
    "int volume = this.volume(first, second);",
    "return volume > 0 && volume <= maxBlocks;",
    "int volume(Location first, Location second)",
    "SelectionBounds bounds = this.bounds(first, second);",
    "return bounds == null ? 0 : bounds.volume();",
    "SelectionBounds bounds(Location first, Location second)",
    "Math.min(first.getBlockX(), second.getBlockX())",
    "Math.max(first.getBlockX(), second.getBlockX())",
    "Math.min(first.getBlockY(), second.getBlockY())",
    "Math.max(first.getBlockY(), second.getBlockY())",
    "Math.min(first.getBlockZ(), second.getBlockZ())",
    "Math.max(first.getBlockZ(), second.getBlockZ())",
    "record SelectionBounds(int minX, int maxX, int minY, int maxY, int minZ, int maxZ)",
    "int sizeX()",
    "int sizeY()",
    "int sizeZ()",
    "int volume()"
    "BlockPoint rotatedPosition(int x, int y, int z, double centerX, double centerZ, int rotation)"
    "if (rotation == 180)"
    "else if (rotation == 270)"
    "BlockPoint flippedPosition(int x, int y, int z, double centerX, double centerZ, char axis)"
    "if (axis == 'x')"
    "BlockData rotateBlockData(BlockData blockData, int rotation)"
    "int quarterTurns = rotation / 90;"
    "BlockData flipBlockData(BlockData blockData, char axis)"
    "private BlockData rotateBlockDataOnce(BlockData blockData)"
    "private BlockFace mirrorFace(BlockFace blockFace, char axis)"
    "private BlockFace rotateFace(BlockFace blockFace)"
    "record BlockPoint(int x, int y, int z)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxGeometryService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxGeometryService sandboxGeometryService;",
    "this.sandboxGeometryService = new BackendSandboxGeometryService();",
    "return this.sandboxGeometryService.validSelection(drawingState.first, drawingState.second, this.sandboxMaxBlocks());",
    "return this.sandboxGeometryService.volume(drawingState.first, drawingState.second);",
    "BackendSandboxGeometryService.SelectionBounds bounds = this.sandboxGeometryService.bounds(drawingState.first, drawingState.second);",
    "new BackendSandboxBulkDrawingService.Bounds(bounds.minX(), bounds.maxX(), bounds.minY(), bounds.maxY(), bounds.minZ(), bounds.maxZ())",
    "this.sandboxClonePreviewPlanService.build(world, bounds, location, this::isSandboxCloneExcludedEntity);",
    "this.showBox(player, drawingState.first.getWorld(), bounds.minX(), bounds.minY(), bounds.minZ(), bounds.maxX(), bounds.maxY(), bounds.maxZ());",
    "BackendSandboxGeometryService.BlockPoint rotatedPoint = this.sandboxGeometryService.rotatedPosition(x, y, z, d, d2, drawingState.rotation);",
    "BlockPosition blockPosition = new BlockPosition(rotatedPoint.x(), rotatedPoint.y(), rotatedPoint.z());",
    "this.sandboxGeometryService.rotateBlockData(world.getBlockAt(x, y, z).getBlockData(), drawingState.rotation)",
    "BackendSandboxGeometryService.BlockPoint flippedPoint = this.sandboxGeometryService.flippedPosition(x, y, z, d, d2, drawingState.flipAxis);",
    "BlockPosition blockPosition = new BlockPosition(flippedPoint.x(), flippedPoint.y(), flippedPoint.z());",
    "this.sandboxGeometryService.flipBlockData(world.getBlockAt(x, y, z).getBlockData(), drawingState.flipAxis)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxGeometryService: $snippet"
    }
}

$forbiddenPluginSnippets = @(
    "private BlockPosition flippedPosition",
    "private BlockData flipBlockData",
    "private BlockFace mirrorFace",
    "private BlockPosition rotatedPosition",
    "private BlockData rotateBlockData",
    "private BlockData rotateBlockDataOnce",
    "private BlockFace rotateFace",
    "import org.bukkit.Axis;",
    "import org.bukkit.block.data.Directional;",
    "import org.bukkit.block.data.Orientable;",
    "import org.bukkit.block.data.Rotatable;"
)

foreach ($snippet in $forbiddenPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still owns raw Sandbox geometry behavior: $snippet"
    }
}

Write-Host "Backend sandbox geometry contract OK"
