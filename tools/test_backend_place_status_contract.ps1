param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPlaceStatusService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\places.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendPlaceStatusService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendPlaceStatusService",
    "boolean ensureStatus(FileConfiguration places, String place, String defaultStatus)",
    "void setStatus(FileConfiguration places, String place, String status)",
    "String rawStatus(FileConfiguration places, String place, String defaultStatus)",
    "String normalizedStatus(FileConfiguration places, String place)",
    "boolean closed(String status)",
    "boolean wakeable(String status)",
    "boolean resting(String status)",
    "if (places.isString(path))",
    "places.set(path, (Object)defaultStatus)",
    "places.set(this.statusPath(place), (Object)status)",
    "return places.getString(this.statusPath(place), defaultStatus)",
    "return status == null ? `"ready`" : status.trim().toLowerCase(Locale.ROOT)",
    "`"not_ready`".equals(status)",
    "`"not ready yet.`".equals(status)",
    "`"unavailable.`".equals(status)",
    "`"sleep.`".equals(status)",
    "`"waking up.`".equals(status)",
    "`"resting`".equals(status)",
    "`"resting.`".equals(status)",
    "return `"places.`" + place + `".status`""
)) {
    if (-not $service.Contains($required)) {
        throw "BackendPlaceStatusService missing required place status contract text: $required"
    }
}

foreach ($required in @(
    "private BackendPlaceStatusService placeStatusService",
    "this.placeStatusService = new BackendPlaceStatusService()",
    "this.placeStatusService.ensureStatus(this.places, serverId.proxyName, this.placeConfig(serverId, `"status`", `"ready`"))",
    "private BackendPlaceRuntimeStatusService placeRuntimeStatusService",
    "this.placeRuntimeStatusService = new BackendPlaceRuntimeStatusService(this.placeStatusService)",
    "private BackendPlaceAvailabilityService<ServerId> placeAvailabilityService",
    "this.placeAvailabilityService = new BackendPlaceAvailabilityService<ServerId>(this.placeRuntimeStatusService)",
    "this.placeRuntimeStatusService.setStatus(this.places, serverId.proxyName, string)",
    "this.placeRuntimeStatusService.setStatus(this.places, this.currentServer.proxyName, string)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendPlaceStatusService: $required"
    }
}

foreach ($forbidden in @(
    "private String currentPlaceRuntimeStatusPath()",
    "this.places.set(`"places.`" + serverId.proxyName + `".status`", (Object)string)",
    "this.places.getString(`"places.`" + serverId.proxyName + `".status`", `"ready`")",
    "this.places.getString(this.currentPlaceRuntimeStatusPath(), `"ready`")",
    "this.places.set(this.currentPlaceRuntimeStatusPath(), (Object)string)"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend place runtime status persistence detail: $forbidden"
    }
}

foreach ($place in @("lobby", "survival", "creative")) {
    if ($template -notmatch "(?ms)^places:.*?^\s{2}${place}:\s*\r?\n\s{4}status:\s*ready\s*$") {
        throw "Places template no longer preserves ready status for $place."
    }
}

Write-Host "LemonOS backend place status contract tests passed."
