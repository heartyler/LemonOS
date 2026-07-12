param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendRestStateService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendRestStateService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendRestStateService",
    "private RestState restState = RestState.ACTIVE",
    "private long emptySinceMillis",
    "private long restSinceMillis",
    "private String preRestPlaceStatus",
    "void reset()",
    "RestAction tick(long nowMillis, boolean onlineEmpty, int idleMinutes, boolean autoStop, int sleepMinutes)",
    "return this.wakeFromRestIfNeeded()",
    "this.emptySinceMillis = nowMillis",
    "return this.sleepReady(nowMillis, autoStop, sleepMinutes, onlineEmpty) ? RestAction.SLEEP : RestAction.NONE",
    "return idleMillis >= (long)idleMinutes * 60000L ? RestAction.ENTER_REST : RestAction.NONE",
    "void markEmptyIfNeeded(long nowMillis, boolean onlineEmpty)",
    "RestAction wakeFromRestIfNeeded()",
    "this.restState = RestState.WAKING_UP",
    "boolean isWakingUp()",
    "boolean isSleeping()",
    "void finishWake()",
    "boolean canEnterRest()",
    "void enterRest(long nowMillis)",
    "boolean sleepReady(long nowMillis, boolean autoStop, int sleepMinutes, boolean onlineEmpty)",
    "void markSleeping()",
    "void capturePreRestPlaceStatus(String status, String restingStatus, String wakingStatus)",
    "String restorePlaceStatus(String currentStatus, String restingStatus, String wakingStatus)",
    "boolean isResting()",
    "long restSinceMillis(long nowMillis)",
    "enum RestAction",
    "private enum RestState"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendRestStateService missing required rest contract text: $required"
    }
}

foreach ($required in @(
    "private BackendRestStateService restStateService",
    "this.restStateService = new BackendRestStateService()",
    "this.restStateService.reset()",
    "BackendRestStateService.RestAction restAction = this.restStateService.tick(l, Bukkit.getOnlinePlayers().isEmpty(), this.restIdleMinutes(), this.restAutoStop(), this.restSleepMinutes())",
    "this.scheduleRestWake()",
    "this.restStateService.markEmptyIfNeeded(System.currentTimeMillis(), Bukkit.getOnlinePlayers().isEmpty())",
    "this.restStateService.wakeFromRestIfNeeded() == BackendRestStateService.RestAction.WAKE",
    "!this.restStateService.canEnterRest()",
    "this.restStateService.enterRest(System.currentTimeMillis())",
    "private void sleepNow()",
    "this.restStateService.markSleeping()",
    "private void scheduleRestWake()",
    "this.restStateService.isWakingUp()",
    "this.restStateService.isSleeping()",
    "this.restStateService.finishWake()",
    "this.placeRuntimeStatusService.capturePreRestStatus(this.places, this.currentServer.proxyName, this.restStateService, this.restRestingStatus(), this.restWakingStatus())",
    "String string2 = this.placeRuntimeStatusService.restoreRestStatus(this.places, this.currentServer.proxyName, this.restStateService, this.restRestingStatus(), this.restWakingStatus())",
    "this.restStateService.restSinceMillis(System.currentTimeMillis())",
    "return this.restStateService.isResting()"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendRestStateService: $required"
    }
}

foreach ($forbidden in @(
    "private RestState restState",
    "private long emptySinceMillis",
    "private long restSinceMillis",
    "private String preRestPlaceStatus",
    "private static enum RestState",
    "this.restState =",
    "this.emptySinceMillis",
    "this.restSinceMillis",
    "this.preRestPlaceStatus"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns rest lifecycle state detail: $forbidden"
    }
}

Write-Host "LemonOS backend rest state contract tests passed."
