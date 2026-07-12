param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaceConnectService.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"
$wakePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaceWakeService.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing proxy PlaceConnectService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$proxy = Get-Content -Raw -LiteralPath $proxyPath
$wake = Get-Content -Raw -LiteralPath $wakePath

foreach ($required in @(
    "final class PlaceConnectService",
    "void connectPlace(ServerConnection serverConnection, UUID uuid, String requestedPlace) throws IOException",
    "void connectPlayerToPlace(ServerConnection serverConnection, Player player, RegisteredServer registeredServer, UUID uuid, String place)",
    "void sendPlaceResult(ServerConnection serverConnection, UUID uuid, String place, String result) throws IOException",
    "AdminProtocol.PLACE_CONNECTED",
    "AdminProtocol.PLACE_UNAVAILABLE",
    "dataOutputStream.writeUTF(result)",
    "dataOutputStream.writeUTF(uuid.toString())",
    "dataOutputStream.writeUTF(place)",
    "playerOnSourceServer",
    "normalizePlaceName",
    "canProxyWake"
)) {
    if (-not $service.Contains($required)) {
        throw "PlaceConnectService missing required connect contract text: $required"
    }
}

foreach ($forbidden in @(
    "private void connectPlace(",
    "private void connectPlayerToPlace(",
    "private void wakeAndConnect(",
    "private void sendPlaceResult(",
    "AdminProtocol.PLACE_CONNECTED",
    "Unable to send LemonOS place result."
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns place connect/result detail: $forbidden"
    }
}

if ($proxy -notmatch "new PlaceConnectService\(this\.server, this\.logger, this\.adminChannel, this\.placeRuntimeProbe, this\.placeStatusRepository, this::scheduleProxyTask" -or
    $proxy -notmatch "this\.placeConnectService\.connectPlace\(serverConnection, uUID, string4\)" -or
    $wake -notmatch "PlaceConnector connector" -or
    $wake -notmatch "PlaceResultSender resultSender") {
    throw "Place connect/wake services are not wired together through the connect boundary."
}

Write-Host "LemonOS place connect service contract tests passed."
