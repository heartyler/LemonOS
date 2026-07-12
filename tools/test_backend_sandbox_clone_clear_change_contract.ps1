param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxCloneClearChangeService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxCloneClearChangeService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxCloneClearChangeService",
    "void buildCloneChanges(World world, Iterable<ClonePlacementBlock> blocks, ChangeSink changeSink)",
    "int x = cloneBlock.baseX() + cloneBlock.offsetX();",
    "int y = cloneBlock.baseY() + cloneBlock.offsetY();",
    "int z = cloneBlock.baseZ() + cloneBlock.offsetZ();",
    "Block block = world.getBlockAt(x, y, z);",
    "changeSink.add(world, x, y, z, block.getBlockData(), cloneBlock.blockData());",
    "void buildClearChanges(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockData clearData, ChangeSink changeSink)",
    "for (int x = minX; x <= maxX; ++x)",
    "for (int y = minY; y <= maxY; ++y)",
    "for (int z = minZ; z <= maxZ; ++z)",
    "changeSink.add(world, x, y, z, block.getBlockData(), clearData);",
    "interface ChangeSink",
    "record ClonePlacementBlock(int baseX, int baseY, int baseZ, int offsetX, int offsetY, int offsetZ, BlockData blockData)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxCloneClearChangeService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxCloneClearChangeService sandboxCloneClearChangeService;",
    "this.sandboxCloneClearChangeService = new BackendSandboxCloneClearChangeService();",
    "this.sandboxCloneClearChangeService.buildCloneChanges(clonePreview.world, this.clonePlacementBlocks(clonePreview)",
    "this.sandboxCloneClearChangeService.buildClearChanges(clearPreview.world, clearPreview.minX, clearPreview.minY, clearPreview.minZ, clearPreview.maxX, clearPreview.maxY, clearPreview.maxZ, blockData",
    "private List<BackendSandboxCloneClearChangeService.ClonePlacementBlock> clonePlacementBlocks(ClonePreview clonePreview)",
    "new BackendSandboxCloneClearChangeService.ClonePlacementBlock(clonePreview.minX, clonePreview.minY, clonePreview.minZ, cloneBlock.offsetX, cloneBlock.offsetY, cloneBlock.offsetZ, cloneBlock.blockData)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxCloneClearChangeService: $snippet"
    }
}

Write-Host "Backend sandbox clone/clear change contract OK"
