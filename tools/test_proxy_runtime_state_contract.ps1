param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyRuntimeStateService.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing proxy ProxyRuntimeStateService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$proxy = Get-Content -Raw -LiteralPath $proxyPath

foreach ($required in @(
    "final class ProxyRuntimeStateService",
    "void writeOnlinePlayers()",
    "synchronized void loadPlaytime()",
    "synchronized void syncPlaytime()",
    "synchronized void savePlaytime()",
    "synchronized void clearPlaytimeState()",
    "void reconcilePlaceStatuses()",
    "private void setPlaceStatus(String place, String status)",
    "new OnlinePlayersRepository.PlayerSnapshot(player.getUsername())",
    "new PlaytimeRepository.PlayerSnapshot(player.getUniqueId(), player.getUsername())",
    "this.onlinePlayersRepository.write(list)",
    "this.playtimeRepository.load()",
    "this.playtimeRepository.sync(list)",
    "this.playtimeRepository.save()",
    "this.playtimeRepository.clear()",
    "Unable to write LemonOS playtime state.",
    "Unable to clear disabled Stayed Close state.",
    "List.of(`"lobby`", `"survival`", `"creative`")",
    "this.placeRuntimeProbe.port(place)",
    "this.placeRuntimeProbe.canConnect(port)",
    "this.placeStatusRepository.status(place)",
    "this.placeStatusRepository.setStatus(place, status)",
    "PlaceStatusRepository.isWakeStatus(status)",
    "PlaceStatusRepository.isReadyStatus(status)",
    "`"unavailable`".equals(status)",
    '"unavailable.".equals(status) || PlaceStatusRepository.isWakeStatus(status) || status.isBlank()'
)) {
    if (-not $service.Contains($required)) {
        throw "ProxyRuntimeStateService missing required runtime state contract text: $required"
    }
}

foreach ($forbidden in @(
    "private void writeOnlinePlayers(",
    "private synchronized void loadPlaytime(",
    "private synchronized void syncPlaytime(",
    "private synchronized void savePlaytime(",
    "private synchronized void clearPlaytimeState(",
    "private void reconcilePlaceStatuses(",
    "private void setPlaceStatus(",
    "new OnlinePlayersRepository.PlayerSnapshot",
    "new PlaytimeRepository.PlayerSnapshot",
    "Unable to write LemonOS playtime state.",
    "Unable to clear disabled Stayed Close state."
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns runtime state detail: $forbidden"
    }
}

if ($proxy -notmatch "new ProxyRuntimeStateService\(this\.server, this\.logger, this\.onlineFile, this\.playtimeFile, this\.placesFile, this\.onlinePlayersRepository, this\.playtimeRepository, this\.placeStatusRepository, this\.placeRuntimeProbe, this::stayedCloseCollectionEnabled\)" -or
    $proxy -notmatch "this\.runtimeStateService\.writeOnlinePlayers\(\)" -or
    $proxy -notmatch "this\.runtimeStateService\.syncPlaytime\(\)" -or
    $proxy -notmatch "this\.runtimeStateService\.reconcilePlaceStatuses\(\)" -or
    $proxy -notmatch "this\.runtimeStateService::writeOnlinePlayers" -or
    $proxy -notmatch "this\.runtimeStateService::syncPlaytime" -or
    $proxy -notmatch "this\.runtimeStateService::reconcilePlaceStatuses") {
    throw "LemonOSProxyPlugin is not wired through ProxyRuntimeStateService."
}

Write-Host "LemonOS proxy runtime state contract tests passed."
