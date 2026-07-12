param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxCloneClearPreviewStatusService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxCloneClearPreviewStatusService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxCloneClearPreviewStatusService",
    "PreviewStatus cloneStatus(boolean invalidSelection, boolean differentWorld, boolean outsideVerticalRange)",
    "if (invalidSelection || differentWorld || outsideVerticalRange)",
    'return new PreviewStatus(false, true, "too large.", NamedTextColor.DARK_GRAY);',
    'return new PreviewStatus(true, false, "", NamedTextColor.GRAY);',
    "PreviewStatus clearStatus()",
    "record PreviewStatus(boolean ready, boolean sendStatus, String message, NamedTextColor color)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxCloneClearPreviewStatusService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxCloneClearPreviewStatusService sandboxCloneClearPreviewStatusService;",
    "this.sandboxCloneClearPreviewStatusService = new BackendSandboxCloneClearPreviewStatusService();",
    "BackendSandboxCloneClearPreviewStatusService.PreviewStatus previewStatus = this.sandboxCloneClearPreviewStatusService.cloneStatus(!validSelection, differentWorld, false);",
    "previewStatus = this.sandboxCloneClearPreviewStatusService.cloneStatus(false, false, this.sandboxClonePreviewPlanService.outsideVerticalRange(clonePlan, world.getMinHeight(), world.getMaxHeight()));",
    "BackendSandboxCloneClearPreviewStatusService.PreviewStatus previewStatus = this.sandboxCloneClearPreviewStatusService.clearStatus();",
    "this.sendSandboxStatus(player, previewStatus.message(), previewStatus.color());",
    "this.sendSandboxReadyStatus(player);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxCloneClearPreviewStatusService: $snippet"
    }
}

$createClonePreview = [regex]::Match($Plugin, "private void createClonePreview\(Player player, DrawingState drawingState, Location location\) \{(?s).*?\n    \}\r?\n\r?\n    private void createClearPreview")
if (-not $createClonePreview.Success) {
    throw "Could not isolate createClonePreview body."
}

$forbiddenCloneSnippets = @(
    'this.sendSandboxStatus(player, "too large.", NamedTextColor.DARK_GRAY);',
    "!this.validSelection(player, drawingState) || !drawingState.first.getWorld().equals((Object)location.getWorld())"
)

foreach ($snippet in $forbiddenCloneSnippets) {
    if ($createClonePreview.Value.Contains($snippet)) {
        throw "LemonOSPlugin createClonePreview still owns clone preview status behavior: $snippet"
    }
}

Write-Host "Backend sandbox clone/clear preview status contract OK"
