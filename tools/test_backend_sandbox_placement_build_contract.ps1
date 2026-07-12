param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredPluginSnippets = @(
    "this.finishSandboxPlacement(player, this.buildClonePlacementChange(clonePreview));",
    "this.finishSandboxPlacement(player, this.buildClearPlacementChange(clearPreview));",
    "private DrawingChange buildClonePlacementChange(ClonePreview clonePreview)",
    "DrawingChange drawingChange = new DrawingChange();",
    "this.sandboxCloneClearChangeService.buildCloneChanges(clonePreview.world, this.clonePlacementBlocks(clonePreview), (world, x, y, z, oldData, newData) -> this.addBlockChange(drawingChange, world, x, y, z, oldData, newData));",
    "return drawingChange;",
    "private DrawingChange buildClearPlacementChange(ClearPreview clearPreview)",
    "BlockData blockData = Material.AIR.createBlockData();",
    "this.sandboxCloneClearChangeService.buildClearChanges(clearPreview.world, clearPreview.minX, clearPreview.minY, clearPreview.minZ, clearPreview.maxX, clearPreview.maxY, clearPreview.maxZ, blockData, (world, x, y, z, oldData, newData) -> this.addBlockChange(drawingChange, world, x, y, z, oldData, newData));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to placement build model: $snippet"
    }
}

$placementRegion = [regex]::Match($Plugin, "private void placeClone\(Player player\) \{(?s).*?\n    \}\r?\n\r?\n    private DrawingChange buildClonePlacementChange")
if (-not $placementRegion.Success) {
    throw "Could not isolate placement action methods."
}

$forbiddenSnippets = @(
    "DrawingChange drawingChange = new DrawingChange();",
    "this.sandboxCloneClearChangeService.buildCloneChanges",
    "this.sandboxCloneClearChangeService.buildClearChanges",
    "Material.AIR.createBlockData()"
)

foreach ($snippet in $forbiddenSnippets) {
    if ($placementRegion.Value.Contains($snippet)) {
        throw "Placement action methods still own clone/clear change building: $snippet"
    }
}

Write-Host "Backend sandbox placement build contract OK"
