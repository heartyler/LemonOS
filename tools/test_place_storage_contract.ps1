param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$repositoryPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaceStatusRepository.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"
$runtimeStatePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyRuntimeStateService.java"

if (-not (Test-Path -LiteralPath $repositoryPath -PathType Leaf)) {
    throw "Missing proxy PlaceStatusRepository."
}

$repository = Get-Content -Raw -LiteralPath $repositoryPath
$proxy = Get-Content -Raw -LiteralPath $proxyPath
$runtimeState = Get-Content -Raw -LiteralPath $runtimeStatePath

foreach ($required in @(
    "final class PlaceStatusRepository",
    "static String defaultFile()",
    "String status(String place)",
    "void setStatus(String place, String status)",
    "static boolean isReadyStatus(String status)",
    "static boolean isWakeStatus(String status)",
    "writeStringAtomic",
    "LemonOS.DATA_SCHEMA_VERSION",
    "places:",
    "status: ready",
    "waking up."
)) {
    if (-not $repository.Contains($required)) {
        throw "PlaceStatusRepository missing required storage contract text: $required"
    }
}

foreach ($forbidden in @(
    "Files.readAllLines(this.placesFile",
    "Files.createDirectories(this.placesFile.getParent()",
    "this.writeStringAtomic(this.placesFile",
    "version: `"2.0`"`r`nplaces:"
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns place storage detail: $forbidden"
    }
}

if ($repository.Contains("com.velocitypowered.api.proxy.Player") -or
    $repository.Contains("com.velocitypowered.api.proxy.server.RegisteredServer") -or
    $repository.Contains("java.net.Socket")) {
    throw "PlaceStatusRepository must not depend on Velocity runtime objects or socket checks."
}

if ($proxy -notmatch "new PlaceStatusRepository\(this\.placesFile, this\.logger\)" -or
    $proxy -notmatch "new ProxyRuntimeStateService\(this\.server, this\.logger, this\.onlineFile, this\.playtimeFile, this\.placesFile, this\.onlinePlayersRepository, this\.playtimeRepository, this\.placeStatusRepository" -or
    $proxy -notmatch "this\.placeStatusRepository\.status\(string\)" -or
    $runtimeState -notmatch "this\.placeStatusRepository\.status\(place\)" -or
    $runtimeState -notmatch "this\.placeStatusRepository\.setStatus\(place, status\)" -or
    $proxy -notmatch "PlaceStatusRepository\.isReadyStatus\(string\)" -or
    $proxy -notmatch "PlaceStatusRepository\.isWakeStatus\(string\)" -or
    $runtimeState -notmatch "PlaceStatusRepository\.isReadyStatus\(status\)" -or
    $runtimeState -notmatch "PlaceStatusRepository\.isWakeStatus\(status\)") {
    throw "LemonOSProxyPlugin is not wired through PlaceStatusRepository."
}

Write-Host "LemonOS place storage contract tests passed."
