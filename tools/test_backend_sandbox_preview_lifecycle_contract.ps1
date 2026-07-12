param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxPreviewLifecycleService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxPreviewLifecycleService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxPreviewLifecycleService",
    "List<PreviewKind> clearOrder()",
    "return List.of(PreviewKind.CLONE, PreviewKind.CLEAR, PreviewKind.ROTATE, PreviewKind.FLIP);",
    "List<PreviewKind> recoveryOrder()",
    "return this.clearOrder();",
    "PreviewKind transformKind(boolean rotate)",
    "return rotate ? PreviewKind.ROTATE : PreviewKind.FLIP;",
    "ReadyPreviewLifecycle readyPreviewLifecycle()",
    "return new ReadyPreviewLifecycle(true, true, true);",
    "record ReadyPreviewLifecycle(boolean removeDrawingState, boolean showBox, boolean sendReadyStatus)",
    "enum PreviewKind",
    "CLONE",
    "CLEAR",
    "ROTATE",
    "FLIP"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxPreviewLifecycleService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxPreviewLifecycleService sandboxPreviewLifecycleService;",
    "this.sandboxPreviewLifecycleService = new BackendSandboxPreviewLifecycleService();",
    "for (BackendSandboxPreviewLifecycleService.PreviewKind previewKind : this.sandboxPreviewLifecycleService.recoveryOrder())",
    "this.openRecoveryTick(() -> this.openPreviewConfirm(player, previewKind));",
    "for (BackendSandboxPreviewLifecycleService.PreviewKind previewKind : this.sandboxPreviewLifecycleService.clearOrder())",
    "this.cancelPreview(uUID, previewKind);",
    "private void cancelPreview(UUID uUID, BackendSandboxPreviewLifecycleService.PreviewKind previewKind)",
    "this.removePreview(uUID, previewKind);",
    "this.cancelSandboxIdleTimeout(uUID);",
    "if (!this.hasSandboxStatusState(uUID))",
    "private boolean hasPreview(UUID uUID, BackendSandboxPreviewLifecycleService.PreviewKind previewKind)",
    "case CLONE -> this.sandboxPreviewService.hasClone(uUID);",
    "case CLEAR -> this.sandboxPreviewService.hasClear(uUID);",
    "case ROTATE -> this.sandboxPreviewService.hasRotate(uUID);",
    "case FLIP -> this.sandboxPreviewService.hasFlip(uUID);",
    "private Object removePreview(UUID uUID, BackendSandboxPreviewLifecycleService.PreviewKind previewKind)",
    "case CLONE -> this.sandboxPreviewService.removeClone(uUID);",
    "case CLEAR -> this.sandboxPreviewService.removeClear(uUID);",
    "case ROTATE -> this.sandboxPreviewService.removeRotate(uUID);",
    "case FLIP -> this.sandboxPreviewService.removeFlip(uUID);",
    "private void openPreviewConfirm(Player player, BackendSandboxPreviewLifecycleService.PreviewKind previewKind)",
    "case CLONE -> this.openCloneConfirm(player);",
    "case CLEAR -> this.openClearConfirm(player);",
    "case ROTATE -> this.openRotateConfirm(player);",
    "case FLIP -> this.openFlipConfirm(player);",
    "this.finishPreviewCreation(player, this.sandboxPreviewLifecycleService.transformKind(rotate), rotatePreview, world, transformSummary.minX(), transformSummary.minY(), transformSummary.minZ(), transformSummary.maxX(), transformSummary.maxY(), transformSummary.maxZ());",
    "private void setPreview(UUID uUID, BackendSandboxPreviewLifecycleService.PreviewKind previewKind, Object preview)",
    "case CLONE -> this.sandboxPreviewService.setClone(uUID, preview);",
    "case CLEAR -> this.sandboxPreviewService.setClear(uUID, preview);",
    "case ROTATE -> this.sandboxPreviewService.setRotate(uUID, preview);",
    "case FLIP -> this.sandboxPreviewService.setFlip(uUID, preview);",
    "private void finishPreviewCreation(Player player, BackendSandboxPreviewLifecycleService.PreviewKind previewKind, Object preview, World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ)",
    "BackendSandboxPreviewLifecycleService.ReadyPreviewLifecycle lifecycle = this.sandboxPreviewLifecycleService.readyPreviewLifecycle();",
    "this.setPreview(uUID, previewKind, preview);",
    "if (lifecycle.removeDrawingState())",
    "this.removeDrawingState(uUID);",
    "if (lifecycle.showBox())",
    "this.showBox(player, world, minX, minY, minZ, maxX, maxY, maxZ);",
    "if (lifecycle.sendReadyStatus())",
    "this.sendSandboxReadyStatus(player);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxPreviewLifecycleService: $snippet"
    }
}

$forbiddenPluginSnippets = @(
    "this.cancelClonePreview(uUID);`r`n        this.cancelClearPreview(uUID);",
    "if (this.sandboxPreviewService.hasClone(player.getUniqueId()))",
    "if (this.sandboxPreviewService.hasClear(player.getUniqueId()))",
    "if (this.sandboxPreviewService.hasRotate(player.getUniqueId()))",
    "if (this.sandboxPreviewService.hasFlip(player.getUniqueId()))",
    "if (rotate) {`r`n            this.sandboxPreviewService.setRotate"
)

foreach ($snippet in $forbiddenPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still owns duplicated preview lifecycle policy: $snippet"
    }
}

Write-Host "Backend sandbox preview lifecycle contract OK"
