param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxUndoRedoService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxUndoRedoService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxUndoRedoService",
    "UndoRedoStatus missingChange()",
    "return new UndoRedoStatus(`"nothing changed.`", NamedTextColor.DARK_GRAY);",
    "UndoRedoStatus applyFailed()",
    "return new UndoRedoStatus(`"try again.`", NamedTextColor.DARK_GRAY);",
    "UndoRedoStatus applied()",
    "return new UndoRedoStatus(`"done.`", NamedTextColor.GRAY);",
    "boolean undoUsesOldData()",
    "return true;",
    "boolean redoUsesOldData()",
    "return false;",
    "IdleRoute idleRoute(boolean busy, boolean undo)",
    "return IdleRoute.BLOCKED;",
    "return undo ? IdleRoute.UNDO : IdleRoute.REDO;",
    "enum IdleRoute",
    "BLOCKED",
    "UNDO",
    "REDO",
    "record UndoRedoStatus(String message, NamedTextColor color)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxUndoRedoService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxUndoRedoService sandboxUndoRedoService;",
    "this.sandboxUndoRedoService = new BackendSandboxUndoRedoService();",
    "this.runUndoRedoIfIdle(player, true);",
    "this.runUndoRedoIfIdle(player, false);",
    "private void runUndoRedoIfIdle(Player player, boolean undo)",
    "switch (this.sandboxUndoRedoService.idleRoute(this.isBusy(player.getUniqueId()), undo))",
    "case BLOCKED -> this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.missingChange());",
    "case UNDO -> this.undoDrawing(player);",
    "case REDO -> this.redoDrawing(player);",
    "this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.missingChange());",
    "this.applyAndVerifyChange(drawingChange, this.sandboxUndoRedoService.undoUsesOldData())",
    "this.sandboxHistoryService.restoreUndo(player.getUniqueId(), drawingChange);",
    "this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.applyFailed());",
    "this.sandboxHistoryService.moveUndoToRedo(player.getUniqueId(), drawingChange);",
    "this.applyAndVerifyChange(drawingChange, this.sandboxUndoRedoService.redoUsesOldData())",
    "this.sandboxHistoryService.restoreRedo(player.getUniqueId(), drawingChange);",
    "this.sandboxHistoryService.moveRedoToUndo(player.getUniqueId(), drawingChange);",
    "this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.applied());",
    "private void sendSandboxUndoRedoStatus(Player player, BackendSandboxUndoRedoService.UndoRedoStatus undoRedoStatus)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxUndoRedoService: $snippet"
    }
}

$idleRegion = [regex]::Match($Plugin, "private void undoDrawingIfIdle\(Player player\) \{(?s).*?\n    \}\r?\n\r?\n    private void undoDrawing")
if (-not $idleRegion.Success) {
    throw "Could not isolate Sandbox undo/redo idle lifecycle."
}

$forbiddenIdleSnippets = @(
    'this.sendSandboxStatus(player, "nothing changed.", NamedTextColor.DARK_GRAY);',
    "if (this.isBusy(player.getUniqueId()))"
)

foreach ($snippet in $forbiddenIdleSnippets) {
    if ($idleRegion.Value.Contains($snippet)) {
        throw "LemonOSPlugin still owns Sandbox undo/redo idle lifecycle policy: $snippet"
    }
}

Write-Host "Backend sandbox undo/redo contract OK"
