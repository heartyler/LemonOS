param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxPlacementService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "PlacementPreview preview(Object preview)",
    "return new PlacementPreview(preview, preview == null);",
    "record PlacementPreview(Object preview, boolean missing)",
    "MissingPlacementLifecycle missingLifecycle()",
    "return new MissingPlacementLifecycle(true, true);",
    "record MissingPlacementLifecycle(boolean closeInventory, boolean sendStatus)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxPlacementService missing placement removal snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxPlacementService.PlacementPreview removePlacementPreview(Player player, BackendSandboxPreviewLifecycleService.PreviewKind previewKind)",
    "Object preview = this.removePreview(player.getUniqueId(), previewKind);",
    "this.cancelSandboxIdleTimeout(player.getUniqueId());",
    "return this.sandboxPlacementService.preview(preview);",
    "this.removePlacementPreview(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLONE)",
    "this.removePlacementPreview(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLEAR)",
    "this.removePlacementPreview(player, BackendSandboxPreviewLifecycleService.PreviewKind.ROTATE)",
    "this.removePlacementPreview(player, BackendSandboxPreviewLifecycleService.PreviewKind.FLIP)",
    "if (placementPreview.missing())",
    "BackendSandboxPlacementService.MissingPlacementLifecycle lifecycle = this.sandboxPlacementService.missingLifecycle();",
    "if (lifecycle.closeInventory())",
    "if (lifecycle.sendStatus())",
    "ClonePreview clonePreview = (ClonePreview)placementPreview.preview();",
    "ClearPreview clearPreview = (ClearPreview)placementPreview.preview();",
    "RotatePreview rotatePreview = (RotatePreview)placementPreview.preview();"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to placement removal model: $snippet"
    }
}

$placementRegion = [regex]::Match($Plugin, "private void placeClone\(Player player\) \{(?s).*?\n    \}\r?\n\r?\n    private void finishMissingSandboxPlacement")
if (-not $placementRegion.Success) {
    throw "Could not isolate placement methods."
}

$forbiddenSnippets = @(
    "this.sandboxPreviewService.removeClone(player.getUniqueId())",
    "this.sandboxPreviewService.removeClear(player.getUniqueId())",
    "this.sandboxPreviewService.removeRotate(player.getUniqueId())",
    "this.sandboxPreviewService.removeFlip(player.getUniqueId())",
    "if (clonePreview == null)",
    "if (clearPreview == null)",
    "if (rotatePreview == null)"
)

foreach ($snippet in $forbiddenSnippets) {
    if ($placementRegion.Value.Contains($snippet)) {
        throw "Placement methods still own duplicated preview removal/missing policy: $snippet"
    }
}

Write-Host "Backend sandbox placement removal contract OK"
