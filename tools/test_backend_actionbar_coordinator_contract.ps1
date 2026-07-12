param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$sourceRoot = Join-Path $Root "src\main\java\dev\lemonos"
$coordinator = Get-Content -Raw -LiteralPath (Join-Path $sourceRoot "BackendActionBarCoordinator.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $sourceRoot "LemonOSPlugin.java")

foreach ($required in @(
    'final class BackendActionBarCoordinator',
    'MUSIC(100)', 'ATMOSPHERE(200)', 'PENDING(300)', 'CHAIN(400)',
    'CARE_WORLD(500)', 'SANDBOX(600)', 'SANDBOX_NOTIFICATION(650)', 'TRAVEL(700)',
    'synchronized void publish', 'synchronized void notify', 'synchronized void clear',
    'synchronized Frame frame', 'System.nanoTime()', 'Component.empty()',
    'ownerEntries.entrySet().removeIf', 'this.lastFrames.remove(uuid)',
    'private static final long KEEPALIVE_NANOS = 1_000_000_000L',
    'boolean changed = winnerOwner != lastOwner || !winner.component.equals(lastFrame)',
    'nowNanos - lastSent < KEEPALIVE_NANOS'
)) {
    $haystack = if ($required -eq 'System.nanoTime()') { $plugin } else { $coordinator }
    if (-not $haystack.Contains($required)) { throw "Action Bar coordinator contract missing: $required" }
}

$writers = Get-ChildItem -LiteralPath $sourceRoot -Filter '*.java' -File |
    Select-String -Pattern '\.sendActionBar\('
if ($writers.Count -ne 1 -or $writers[0].Path -notlike '*LemonOSPlugin.java' -or $writers[0].Line -notmatch 'player\.sendActionBar\(component\)') {
    throw "Action Bar must have exactly one platform writer; found $($writers.Count)."
}

foreach ($required in @(
    'private void startActionBarRenderTask()',
    'this.actionBarCoordinator.frame(player.getUniqueId(), this.actionBarHeartbeatNanos)',
    'this.writeActionBar(player, frame.component())',
    '}, 1L, 1L);',
    'this.checkActionBarRenderer()',
    'this.actionBarCoordinator.remove(uUID)'
)) { if (-not $plugin.Contains($required)) { throw "Action Bar renderer wiring missing: $required" } }

Write-Host "Backend Action Bar coordinator contract OK"
