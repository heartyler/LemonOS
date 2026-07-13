param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaceWakeService.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing proxy PlaceWakeService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$proxy = Get-Content -Raw -LiteralPath $proxyPath

foreach ($required in @(
    "final class PlaceWakeService",
    "private final Set<String> wakingServers",
    "void wakeOnly(String place)",
    "void wakeAndConnect(ServerConnection serverConnection, Player player, RegisteredServer registeredServer, UUID uuid, String place)",
    "waitForWakeAndConnect",
    "waitForWakeStatus",
    "CompletableFuture.runAsync",
    "System.currentTimeMillis() + 120000L",
    "Thread.sleep(1000L)",
    "waking up.",
    "ready",
    "unavailable",
    "AdminProtocol.PLACE_UNAVAILABLE",
    "Unable to send LemonOS wake failure.",
    "Unable to send LemonOS wake result."
)) {
    if (-not $service.Contains($required)) {
        throw "PlaceWakeService missing required wake contract text: $required"
    }
}

foreach ($forbidden in @(
    "private final Set<String> wakingServers",
    "waitForPlaceWake(",
    "waitForPlaceWakeStatus(",
    "CompletableFuture.runAsync(() -> this.waitForPlaceWake",
    "Unable to send LemonOS wake failure.",
    "Unable to send LemonOS wake result."
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns place wake orchestration detail: $forbidden"
    }
}

if ($proxy -notmatch "new PlaceWakeService\(this\.server, this\.logger, this\.placeRuntimeProbe, this\.placeStatusRepository, this::scheduleProxyTask, this\.placeConnectService::connectPlayerToPlace, this\.placeConnectService::sendPlaceResult\)" -or
    $proxy -notmatch "this\.placeWakeService\.wakeOnly\(string2\)" -or
    $proxy -notmatch "this\.placeWakeService\.wakeAndConnect\(serverConnection, player, registeredServer, uuid, place\)") {
    throw "LemonOSProxyPlugin is not wired through PlaceWakeService."
}

Write-Host "LemonOS place wake service contract tests passed."
