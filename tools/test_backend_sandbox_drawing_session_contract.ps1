param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxDrawingSessionService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxDrawingSessionService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxDrawingSessionService",
    "private final Map<UUID, Object> drawings = new HashMap<UUID, Object>();",
    "private final Map<UUID, BukkitTask> idleTimeouts = new HashMap<UUID, BukkitTask>();",
    "boolean contains(UUID uuid)",
    "Object get(UUID uuid)",
    "void put(UUID uuid, Object drawing)",
    "Object remove(UUID uuid)",
    "void setIdleTimeout(UUID uuid, BukkitTask task)",
    "BukkitTask removeIdleTimeout(UUID uuid)",
    "List<BukkitTask> idleTimeouts()",
    "return List.copyOf(this.idleTimeouts.values());",
    "List<DrawingEntry> drawingEntries()",
    "new DrawingEntry(entry.getKey(), entry.getValue())",
    "void clear()",
    "record DrawingEntry(UUID uuid, Object drawing)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxDrawingSessionService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxDrawingSessionService sandboxDrawingSessionService;",
    "this.sandboxDrawingSessionService = new BackendSandboxDrawingSessionService();",
    "this.sandboxDrawingSessionService.idleTimeouts()",
    "this.sandboxDrawingSessionService.clear();",
    "this.sandboxDrawingSessionService.contains(uUID)",
    "this.sandboxDrawingSessionService.setIdleTimeout(uUID, bukkitTask)",
    "this.sandboxDrawingSessionService.removeIdleTimeout(uUID)",
    "for (BackendSandboxDrawingSessionService.DrawingEntry entry : this.sandboxDrawingSessionService.drawingEntries())",
    "(DrawingState)entry.drawing()",
    "private DrawingState drawingState(UUID uUID)",
    "return (DrawingState)this.sandboxDrawingSessionService.get(uUID);",
    "private void putDrawingState(UUID uUID, DrawingState drawingState)",
    "this.sandboxDrawingSessionService.put(uUID, drawingState);",
    "private boolean removeDrawingState(UUID uUID)",
    "return this.sandboxDrawingSessionService.remove(uUID) != null;",
    "this.putDrawingState(player.getUniqueId(), drawingState)",
    "DrawingState drawingState = this.drawingState(player.getUniqueId())",
    "this.drawingState(player.getUniqueId()) != drawingState",
    "this.removeDrawingState(player.getUniqueId())"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxDrawingSessionService: $snippet"
    }
}

$forbiddenPluginSnippets = @(
    "private final Map<UUID, DrawingState> drawings",
    "private final Map<UUID, BukkitTask> sandboxIdleTimeouts",
    "this.drawings",
    "this.sandboxIdleTimeouts"
)

foreach ($snippet in $forbiddenPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still owns raw sandbox drawing session state: $snippet"
    }
}

Write-Host "Backend sandbox drawing session contract OK"
