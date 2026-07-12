param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Source = Join-Path $Root "src\main\java\dev\lemonos"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Source "LemonOSPlugin.java")
$Boards = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendBoardLifecycleService.java")
$Atmosphere = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendAtmosphereLifecycleService.java")
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

if (-not $Boards.Contains("extends BackendRepeatingLifecycleService")) { throw "Boards does not own a typed lifecycle boundary." }
if (-not $Atmosphere.Contains("extends BackendRepeatingLifecycleService")) { throw "Atmosphere does not own a typed lifecycle boundary." }

foreach ($snippet in @(
    "private BackendBoardLifecycleService boardLifecycleService;",
    "private BackendAtmosphereLifecycleService atmosphereLifecycleService;",
    "this.boardLifecycleService.stop();",
    "this.atmosphereLifecycleService.stop();",
    "this.boardLifecycleService.start(40L, plan.periodTicks(), this::updateBoards);",
    'this.atmosphereLifecycleService.start(schedule.initialDelayTicks(), schedule.periodTicks(), () -> this.runActionBarProducer("atmosphere", this::tickAtmosphere));'
)) {
    if (-not $Plugin.Contains($snippet)) { throw "Plugin lifecycle wiring missing: $snippet" }
}

foreach ($forbidden in @("private BukkitTask boardTask;", "private BukkitTask atmosphereTask;")) {
    if ($Plugin.Contains($forbidden)) { throw "Plugin still owns feature task directly: $forbidden" }
}

Write-Host "Backend feature lifecycle contract OK"
