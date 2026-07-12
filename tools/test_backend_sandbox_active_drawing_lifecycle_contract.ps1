param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxActiveDrawingLifecycleService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxActiveDrawingLifecycleService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxActiveDrawingLifecycleService",
    "DrawingLifecycle cancel(boolean hadDrawing)",
    "return new DrawingLifecycle(hadDrawing, hadDrawing, hadDrawing);",
    "DrawingLifecycle finish(boolean hadDrawing)",
    "return new DrawingLifecycle(hadDrawing, true, true);",
    "DrawingLifecycle clearSession(boolean hadDrawing)",
    "IdleExpiry expireIdle(boolean hadDrawing, boolean hadPreviews, boolean playerOnline)",
    "boolean hadState = hadDrawing || hadPreviews;",
    "return new IdleExpiry(hadState, hadState && playerOnline, hadState && playerOnline);",
    "record DrawingLifecycle(boolean hadDrawing, boolean cancelIdleTimeout, boolean clearStatus)",
    "record IdleExpiry(boolean hadState, boolean clearStatus, boolean sendNothingChanged)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxActiveDrawingLifecycleService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxActiveDrawingLifecycleService sandboxActiveDrawingLifecycleService;",
    "this.sandboxActiveDrawingLifecycleService = new BackendSandboxActiveDrawingLifecycleService();",
    "this.sandboxActiveDrawingLifecycleService.cancel(this.removeDrawingState(uUID))",
    "return drawingLifecycle.hadDrawing();",
    "this.sandboxActiveDrawingLifecycleService.finish(this.removeDrawingState(uUID))",
    "this.sandboxActiveDrawingLifecycleService.clearSession(this.removeDrawingState(uUID))",
    "this.clearDrawingPreviews(uUID);",
    "this.sandboxDrawingSessionService.removeIdleTimeout(uUID);",
    "boolean bl = this.removeDrawingState(uUID);",
    "boolean bl2 = this.sandboxPreviewService.clearAllFor(uUID);",
    "this.sandboxActiveDrawingLifecycleService.expireIdle(bl, bl2, player.isOnline())",
    "if (idleExpiry.clearStatus())",
    "if (idleExpiry.sendNothingChanged())",
    'player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));',
    "private void applyDrawingLifecycle(UUID uUID, BackendSandboxActiveDrawingLifecycleService.DrawingLifecycle drawingLifecycle)",
    "if (drawingLifecycle.cancelIdleTimeout())",
    "this.cancelSandboxIdleTimeout(uUID);",
    "if (drawingLifecycle.clearStatus())",
    "this.clearSandboxStatus(uUID);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxActiveDrawingLifecycleService: $snippet"
    }
}

Write-Host "Backend sandbox active drawing lifecycle contract OK"
