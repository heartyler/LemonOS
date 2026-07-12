param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxPlacementService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "PlacementResult result(boolean applied, boolean emptyChange)",
    "return applied ? new PlacementResult(true, this.applied(emptyChange)) : new PlacementResult(false, this.applyFailed());",
    "record PlacementResult(boolean applied, PlacementStatus status)",
    "PlacementCompletionLifecycle completionLifecycle(PlacementResult result)",
    "return new PlacementCompletionLifecycle(result.applied(), true, true);",
    "record PlacementCompletionLifecycle(boolean recordChange, boolean closeInventory, boolean sendStatus)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxPlacementService missing placement result snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "boolean applied = this.applyAndVerifyNewData(drawingChange);",
    "BackendSandboxPlacementService.PlacementResult placementResult = this.sandboxPlacementService.result(applied, drawingChange.isEmpty());",
    "BackendSandboxPlacementService.PlacementCompletionLifecycle lifecycle = this.sandboxPlacementService.completionLifecycle(placementResult);",
    "if (lifecycle.recordChange())",
    "this.recordChange(player, drawingChange);",
    "if (lifecycle.closeInventory())",
    "player.closeInventory();",
    "BackendSandboxPlacementService.PlacementStatus placementStatus = placementResult.status();",
    "if (lifecycle.sendStatus())",
    "this.sendSandboxStatus(player, placementStatus.message(), placementStatus.color());"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to placement result model: $snippet"
    }
}

$finishRegion = [regex]::Match($Plugin, "private void finishSandboxPlacement\(Player player, DrawingChange drawingChange\) \{(?s).*?\n    \}\r?\n\r?\n    private void undoDrawingIfIdle")
if (-not $finishRegion.Success) {
    throw "Could not isolate finishSandboxPlacement body."
}

$forbiddenSnippets = @(
    "if (!this.applyAndVerifyNewData(drawingChange))",
    "if (placementResult.applied())",
    "this.sandboxPlacementService.applyFailed()",
    "this.sandboxPlacementService.applied(drawingChange.blocks.isEmpty())"
)

foreach ($snippet in $forbiddenSnippets) {
    if ($finishRegion.Value.Contains($snippet)) {
        throw "finishSandboxPlacement still owns placement status branching: $snippet"
    }
}

Write-Host "Backend sandbox placement result contract OK"
