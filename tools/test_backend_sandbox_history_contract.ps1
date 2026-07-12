param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxHistoryService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxHistoryService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxHistoryService",
    "private final Map<UUID, Deque<Object>> undoHistory = new HashMap<UUID, Deque<Object>>();",
    "private final Map<UUID, Deque<Object>> redoHistory = new HashMap<UUID, Deque<Object>>();",
    "Object popUndo(UUID uuid)",
    "Object popRedo(UUID uuid)",
    "void restoreUndo(UUID uuid, Object change)",
    "void restoreRedo(UUID uuid, Object change)",
    "void moveUndoToRedo(UUID uuid, Object change)",
    "void moveRedoToUndo(UUID uuid, Object change)",
    "void record(UUID uuid, Object change, int historyLimit)",
    "this.redoHistory.remove(uuid);",
    "history.push(change);",
    "while (history.size() > historyLimit)",
    "history.removeLast();",
    "void clearRedo(UUID uuid)",
    "void clear()",
    "private void push(Map<UUID, Deque<Object>> histories, UUID uuid, Object change)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxHistoryService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxHistoryService sandboxHistoryService;",
    "this.sandboxHistoryService = new BackendSandboxHistoryService();",
    "this.sandboxHistoryService.clear();",
    "DrawingChange drawingChange = (DrawingChange)this.sandboxHistoryService.popUndo(player.getUniqueId())",
    "this.sandboxHistoryService.restoreUndo(player.getUniqueId(), drawingChange);",
    "this.sandboxHistoryService.moveUndoToRedo(player.getUniqueId(), drawingChange);",
    "DrawingChange drawingChange = (DrawingChange)this.sandboxHistoryService.popRedo(player.getUniqueId())",
    "this.sandboxHistoryService.restoreRedo(player.getUniqueId(), drawingChange);",
    "this.sandboxHistoryService.moveRedoToUndo(player.getUniqueId(), drawingChange);",
    "this.sandboxHistoryService.clearRedo(player.getUniqueId());",
    "if (drawingChange.isEmpty())",
    "this.recordMadeRoomSandboxAction(player, drawingChange.blocks.size());",
    "this.sandboxHistoryService.record(player.getUniqueId(), drawingChange, this.sandboxHistoryLimit());"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxHistoryService: $snippet"
    }
}

$forbiddenPluginSnippets = @(
    "private final Map<UUID, Deque<DrawingChange>> undoHistory",
    "private final Map<UUID, Deque<DrawingChange>> redoHistory",
    "this.undoHistory",
    "this.redoHistory",
    "import java.util.Deque;"
)

foreach ($snippet in $forbiddenPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still owns raw sandbox history state: $snippet"
    }
}

Write-Host "Backend sandbox history contract OK"
