param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxPlacementService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxPlacementService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxPlacementService",
    "PlacementStatus missingPreview()",
    "return new PlacementStatus(`"nothing changed.`", NamedTextColor.DARK_GRAY);",
    "PlacementStatus applyFailed()",
    "return new PlacementStatus(`"try again.`", NamedTextColor.DARK_GRAY);",
    "PlacementStatus applied(boolean emptyChange)",
    "return emptyChange ? new PlacementStatus(`"nothing changed.`", NamedTextColor.DARK_GRAY) : new PlacementStatus(`"done.`", NamedTextColor.GRAY);",
    "record PlacementStatus(String message, NamedTextColor color)"
    "PlacementResult result(boolean applied, boolean emptyChange)"
    "record PlacementResult(boolean applied, PlacementStatus status)"
    "MissingPlacementLifecycle missingLifecycle()"
    "return new MissingPlacementLifecycle(true, true);"
    "PlacementCompletionLifecycle completionLifecycle(PlacementResult result)"
    "return new PlacementCompletionLifecycle(result.applied(), true, true);"
    "record MissingPlacementLifecycle(boolean closeInventory, boolean sendStatus)"
    "record PlacementCompletionLifecycle(boolean recordChange, boolean closeInventory, boolean sendStatus)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxPlacementService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxPlacementService sandboxPlacementService;",
    "this.sandboxPlacementService = new BackendSandboxPlacementService();",
    "this.finishMissingSandboxPlacement(player);",
    "this.finishSandboxPlacement(player, this.buildClonePlacementChange(clonePreview));",
    "this.finishSandboxPlacement(player, this.buildClearPlacementChange(clearPreview));",
    "this.finishSandboxPlacement(player, rotatePreview.change);",
    "private void finishMissingSandboxPlacement(Player player)",
    "BackendSandboxPlacementService.MissingPlacementLifecycle lifecycle = this.sandboxPlacementService.missingLifecycle();",
    "if (lifecycle.closeInventory())",
    "BackendSandboxPlacementService.PlacementStatus placementStatus = this.sandboxPlacementService.missingPreview();",
    "private void finishSandboxPlacement(Player player, DrawingChange drawingChange)",
    "BackendSandboxPlacementService.PlacementResult placementResult = this.sandboxPlacementService.result(applied, drawingChange.isEmpty());",
    "BackendSandboxPlacementService.PlacementCompletionLifecycle lifecycle = this.sandboxPlacementService.completionLifecycle(placementResult);",
    "if (lifecycle.recordChange())",
    "if (lifecycle.sendStatus())",
    "BackendSandboxPlacementService.PlacementStatus placementStatus = placementResult.status();"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxPlacementService: $snippet"
    }
}

Write-Host "Backend sandbox placement contract OK"
