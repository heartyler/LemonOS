param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxPreviewService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxPreviewService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxPreviewService",
    "private final Map<UUID, Object> clonePreviews = new HashMap<UUID, Object>();",
    "private final Map<UUID, Object> clearPreviews = new HashMap<UUID, Object>();",
    "private final Map<UUID, Object> rotatePreviews = new HashMap<UUID, Object>();",
    "private final Map<UUID, Object> flipPreviews = new HashMap<UUID, Object>();",
    "boolean hasAny(UUID uuid)",
    "boolean hasClone(UUID uuid)",
    "boolean hasClear(UUID uuid)",
    "boolean hasRotate(UUID uuid)",
    "boolean hasFlip(UUID uuid)",
    "void setClone(UUID uuid, Object preview)",
    "void setClear(UUID uuid, Object preview)",
    "void setRotate(UUID uuid, Object preview)",
    "void setFlip(UUID uuid, Object preview)",
    "Object removeClone(UUID uuid)",
    "Object removeClear(UUID uuid)",
    "Object removeRotate(UUID uuid)",
    "Object removeFlip(UUID uuid)",
    "boolean clearAllFor(UUID uuid)",
    "void clearAll()",
    "List<PreviewEntry> cloneEntries()",
    "List<PreviewEntry> clearEntries()",
    "List<PreviewEntry> rotateEntries()",
    "List<PreviewEntry> flipEntries()",
    "record PreviewEntry(UUID uuid, Object preview)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxPreviewService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxPreviewService sandboxPreviewService;",
    "this.sandboxPreviewService = new BackendSandboxPreviewService();",
    "this.sandboxPreviewService.clearAll();",
    "case CLONE -> this.sandboxPreviewService.hasClone(uUID);",
    "case CLEAR -> this.sandboxPreviewService.hasClear(uUID);",
    "case ROTATE -> this.sandboxPreviewService.hasRotate(uUID);",
    "case FLIP -> this.sandboxPreviewService.hasFlip(uUID);",
    "this.sandboxPreviewService.hasAny(uUID)",
    "this.sandboxPreviewService.clearAllFor(uUID)",
    "case CLONE -> this.sandboxPreviewService.removeClone(uUID);",
    "case CLEAR -> this.sandboxPreviewService.removeClear(uUID);",
    "case ROTATE -> this.sandboxPreviewService.removeRotate(uUID);",
    "case FLIP -> this.sandboxPreviewService.removeFlip(uUID);",
    "for (BackendSandboxPreviewService.PreviewEntry entry : this.sandboxPreviewService.cloneEntries())",
    "for (BackendSandboxPreviewService.PreviewEntry entry : this.sandboxPreviewService.clearEntries())",
    "for (BackendSandboxPreviewService.PreviewEntry entry : this.sandboxPreviewService.rotateEntries())",
    "for (BackendSandboxPreviewService.PreviewEntry entry : this.sandboxPreviewService.flipEntries())",
    "(ClonePreview)entry.preview()",
    "(ClearPreview)entry.preview()",
    "(RotatePreview)entry.preview()",
    "case CLONE -> this.sandboxPreviewService.setClone(uUID, preview);",
    "case CLEAR -> this.sandboxPreviewService.setClear(uUID, preview);",
    "this.finishPreviewCreation(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLONE, clonePreview",
    "this.finishPreviewCreation(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLEAR, clearPreview",
    "RotatePreview rotatePreview = new RotatePreview",
    "case ROTATE -> this.sandboxPreviewService.setRotate(uUID, preview);",
    "case FLIP -> this.sandboxPreviewService.setFlip(uUID, preview);",
    "Object preview = this.removePreview(player.getUniqueId(), previewKind);",
    "ClonePreview clonePreview = (ClonePreview)placementPreview.preview();",
    "ClearPreview clearPreview = (ClearPreview)placementPreview.preview();",
    "RotatePreview rotatePreview = (RotatePreview)placementPreview.preview();"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxPreviewService: $snippet"
    }
}

$forbiddenPluginSnippets = @(
    "private final Map<UUID, ClonePreview> clonePreviews",
    "private final Map<UUID, ClearPreview> clearPreviews",
    "private final Map<UUID, RotatePreview> rotatePreviews",
    "private final Map<UUID, RotatePreview> flipPreviews",
    "this.clonePreviews",
    "this.clearPreviews",
    "this.rotatePreviews",
    "this.flipPreviews"
)

foreach ($snippet in $forbiddenPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still owns raw sandbox preview state: $snippet"
    }
}

Write-Host "Backend sandbox preview contract OK"
