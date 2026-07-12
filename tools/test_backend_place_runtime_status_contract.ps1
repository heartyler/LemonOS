param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPlaceRuntimeStatusService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendPlaceRuntimeStatusService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendPlaceRuntimeStatusService",
    "private final BackendPlaceStatusService placeStatusService",
    "BackendPlaceRuntimeStatusService(BackendPlaceStatusService placeStatusService)",
    "boolean closed(FileConfiguration places, String place)",
    "return this.placeStatusService.closed(this.normalizedStatus(places, place))",
    "boolean wakeable(FileConfiguration places, String place)",
    "return this.placeStatusService.wakeable(this.normalizedStatus(places, place))",
    "boolean resting(FileConfiguration places, String place)",
    "return this.placeStatusService.resting(this.normalizedStatus(places, place))",
    "void setStatus(FileConfiguration places, String place, String status)",
    "this.placeStatusService.setStatus(places, place, status)",
    "String normalizedStatus(FileConfiguration places, String place)",
    "return this.placeStatusService.normalizedStatus(places, place)",
    "void capturePreRestStatus(FileConfiguration places, String place, BackendRestStateService restStateService, String restingStatus, String wakingStatus)",
    "String status = this.placeStatusService.rawStatus(places, place, `"ready`")",
    "restStateService.capturePreRestPlaceStatus(status, restingStatus, wakingStatus)",
    "String restoreRestStatus(FileConfiguration places, String place, BackendRestStateService restStateService, String restingStatus, String wakingStatus)",
    "return restStateService.restorePlaceStatus(status, restingStatus, wakingStatus)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendPlaceRuntimeStatusService missing required runtime status contract text: $required"
    }
}

foreach ($required in @(
    "private BackendPlaceRuntimeStatusService placeRuntimeStatusService",
    "this.placeRuntimeStatusService = new BackendPlaceRuntimeStatusService(this.placeStatusService)",
    "this.placeAvailabilityService = new BackendPlaceAvailabilityService<ServerId>(this.placeRuntimeStatusService)",
    "this.placeRuntimeStatusService.setStatus(this.places, serverId.proxyName, string)",
    "this.placeRuntimeStatusService.resting(this.places, this.currentServer.proxyName)",
    "this.placeRuntimeStatusService.capturePreRestStatus(this.places, this.currentServer.proxyName, this.restStateService, this.restRestingStatus(), this.restWakingStatus())",
    "String string2 = this.placeRuntimeStatusService.restoreRestStatus(this.places, this.currentServer.proxyName, this.restStateService, this.restRestingStatus(), this.restWakingStatus())",
    "this.placeRuntimeStatusService.setStatus(this.places, this.currentServer.proxyName, string)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendPlaceRuntimeStatusService: $required"
    }
}

foreach ($forbidden in @(
    "return this.placeStatusService.closed(string)",
    "return this.placeStatusService.wakeable(string)",
    "this.placeStatusService.setStatus(this.places, serverId.proxyName, string)",
    "return this.placeStatusService.normalizedStatus(this.places, serverId.proxyName)",
    "this.placeStatusService.rawStatus(this.places, this.currentServer.proxyName, `"ready`")",
    "this.placeStatusService.setStatus(this.places, this.currentServer.proxyName, string)"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend place runtime status orchestration detail: $forbidden"
    }
}

Write-Host "LemonOS backend place runtime status contract tests passed."
