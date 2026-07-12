param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\PeopleService.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing proxy PeopleService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$proxy = Get-Content -Raw -LiteralPath $proxyPath

foreach ($required in @(
    "final class PeopleService",
    "void sendPeopleState(ServerConnection serverConnection) throws IOException",
    "void meetPlayer(ServerConnection serverConnection, UUID uuid, UUID targetUuid) throws IOException",
    "void bringPlayer(ServerConnection serverConnection, UUID uuid, UUID targetUuid) throws IOException",
    "private void sendPeopleResult(ServerConnection serverConnection, UUID uuid, String result) throws IOException",
    "AdminProtocol.PEOPLE_STATE",
    "AdminProtocol.PEOPLE_ACTION_DONE",
    "AdminProtocol.PEOPLE_ACTION_UNAVAILABLE",
    "dataOutputStream.writeUTF(result)",
    "dataOutputStream.writeUTF(uuid.toString())",
    "Unable to send LemonOS people result.",
    "playerOnSourceServer"
)) {
    if (-not $service.Contains($required)) {
        throw "PeopleService missing required people contract text: $required"
    }
}

foreach ($forbidden in @(
    "private void sendPeopleState(",
    "private void meetPlayer(",
    "private void bringPlayer(",
    "private void sendPeopleResult(",
    "AdminProtocol.PEOPLE_STATE",
    "AdminProtocol.PEOPLE_ACTION_DONE",
    "Unable to send LemonOS people result."
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns people service detail: $forbidden"
    }
}

if ($proxy -notmatch "new PeopleService\(this\.server, this\.logger, this\.adminChannel, this::scheduleProxyTask\)" -or
    $proxy -notmatch "this\.peopleService\.sendPeopleState\(serverConnection\)" -or
    $proxy -notmatch "this\.peopleService\.meetPlayer\(serverConnection, uUID, uUID3\)" -or
    $proxy -notmatch "this\.peopleService\.bringPlayer\(serverConnection, uUID, uUID4\)") {
    throw "LemonOSProxyPlugin is not wired through PeopleService."
}

Write-Host "LemonOS people service contract tests passed."
