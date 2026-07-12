param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\AccessMessageService.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing proxy AccessMessageService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$proxy = Get-Content -Raw -LiteralPath $proxyPath

foreach ($required in @(
    "final class AccessMessageService",
    "private static final int LEGACY_ACCESS_WRITE = 42",
    "private static final int LEGACY_ACCESS_ACK = 43",
    "boolean handleLegacyAccessWrite(ServerConnection serverConnection, byte[] data)",
    "boolean handleAccessMessage(ServerConnection serverConnection, String command, DataInputStream dataInputStream) throws IOException",
    "AdminProtocol.REQUEST_ACCESS",
    "AdminProtocol.REQUEST_KEYS",
    "AdminProtocol.SET_ACCESS",
    "AdminProtocol.ACCESS_STATE",
    "AdminProtocol.KEYS_STATE",
    "AdminProtocol.ACCESS_SAVED",
    "AdminProtocol.ROLE_ADMIN",
    "AdminProtocol.ROLE_DEFAULT",
    "private void sendAccessState(ServerConnection serverConnection, UUID uuid) throws IOException",
    "private void sendKeysState(ServerConnection serverConnection) throws IOException",
    "private boolean hasAdminAccess(UUID uuid)",
    "private boolean hasAdminAccess(Player player)",
    "private boolean playerOnSourceServer(ServerConnection serverConnection, UUID uuid)",
    "private boolean sourceHasAdmin(ServerConnection serverConnection)",
    "private void writeAccess(UUID uuid, UUID targetUuid, String targetName, String role) throws IOException",
    "private boolean isLegacyAccessWrite(byte[] data)",
    "private void sendLegacyAccessAck(ServerConnection serverConnection, long requestId, boolean saved)",
    "private void sendAccessSaved(ServerConnection serverConnection, UUID uuid, UUID targetUuid, String role) throws IOException",
    "AccessRepository.normalizeAccessName",
    "this.accessRepository.updateAccess(targetUuid, targetName, role)",
    "this.accessRepository.updateNameAccess(name, admin)",
    "dataOutputStream.writeByte(LEGACY_ACCESS_ACK)",
    "dataOutputStream.writeByte(saved ? 1 : 0)",
    "LemonOS access updated by {} for {} as {}.",
    "LemonOS access updated for {} as {}.",
    "Ignoring malformed LemonOS legacy access message.",
    "Unable to send LemonOS legacy access ack."
)) {
    if (-not $service.Contains($required)) {
        throw "AccessMessageService missing required access protocol contract text: $required"
    }
}

foreach ($forbidden in @(
    "private void sendAccessState(",
    "private void sendKeysState(",
    "private boolean hasAdminAccess(UUID",
    "private boolean hasAdminAccess(Player",
    "private boolean sourceHasAdmin(",
    "private void writeAccess(",
    "private String accessFileContent(",
    "private boolean isLegacyAccessWrite(",
    "private void handleLegacyAccessWrite(",
    "private void sendLegacyAccessAck(",
    "private void sendAccessSaved(",
    "AdminProtocol.ACCESS_STATE",
    "AdminProtocol.KEYS_STATE",
    "AdminProtocol.ACCESS_SAVED",
    "Ignoring malformed LemonOS legacy access message.",
    "Unable to send LemonOS legacy access ack."
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns access message detail: $forbidden"
    }
}

if ($proxy -notmatch "new AccessMessageService\(this\.server, this\.logger, this\.adminChannel, this\.accessRepository\)" -or
    $proxy -notmatch "this\.accessMessageService\.handleLegacyAccessWrite\(serverConnection, pluginMessageEvent\.getData\(\)\)" -or
    $proxy -notmatch "this\.accessMessageService\.handleAccessMessage\(serverConnection, string, \(DataInputStream\)object\)") {
    throw "LemonOSProxyPlugin is not wired through AccessMessageService."
}

Write-Host "LemonOS access message service contract tests passed."
