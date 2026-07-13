param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Source = Join-Path $Root "src\main\java\dev\lemonos"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Source "LemonOSPlugin.java")
$Hud = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendHudLifecycleService.java")
$Atmosphere = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendAtmosphereLifecycleService.java")
$Music = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendAtmosphereMusicLifecycleService.java")
$Shared = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendRepeatingLifecycleService.java")

foreach ($snippet in @(
    "private BukkitTask task;",
    "this.stop();",
    "if (action == null",
    "runTaskTimer",
    "this.task.cancel();",
    "this.task = null;",
    "System.nanoTime()",
    "ERROR_LOG_INTERVAL_NANOS",
    "this.errorHandler.accept(exception)"
)) {
    if (-not $Shared.Contains($snippet)) { throw "Shared lifecycle missing: $snippet" }
}

if (-not $Hud.Contains("extends BackendRepeatingLifecycleService")) { throw "HUD does not own a typed lifecycle boundary." }
if (-not $Atmosphere.Contains("extends BackendRepeatingLifecycleService")) { throw "Atmosphere does not own a typed lifecycle boundary." }
if (-not $Music.Contains("extends BackendRepeatingLifecycleService")) { throw "Lobby music does not own a typed lifecycle boundary." }

foreach ($snippet in @(
    "private BackendHudLifecycleService hudLifecycleService;",
    "private BackendAtmosphereLifecycleService atmosphereLifecycleService;",
    "private BackendAtmosphereMusicLifecycleService atmosphereMusicLifecycleService;",
    "this.hudLifecycleService.stop();",
    "this.atmosphereLifecycleService.stop();",
    "this.atmosphereMusicLifecycleService.stop();",
    "this.hudLifecycleService.start(40L, plan.periodTicks(), this::updateHuds);",
    'this.atmosphereLifecycleService.start(schedule.initialDelayTicks(), schedule.periodTicks(), () -> this.runActionBarProducer("atmosphere", this::tickAtmosphere));',
    'this.atmosphereMusicLifecycleService.start(schedule.initialDelayTicks(), schedule.periodTicks()'
)) {
    if (-not $Plugin.Contains($snippet)) { throw "Plugin lifecycle wiring missing: $snippet" }
}

foreach ($forbidden in @("private BukkitTask boardTask;", "private BukkitTask atmosphereTask;")) {
    if ($Plugin.Contains($forbidden)) { throw "Plugin still owns feature task directly: $forbidden" }
}

Write-Host "Backend feature lifecycle contract OK"
