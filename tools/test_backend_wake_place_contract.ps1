param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendWakePlaceService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendWakePlaceService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendWakePlaceService",
    "BackendWakePlaceService(Plugin plugin)",
    "void sendWakePlaceRequest(Player player, String serverProxyName)",
    "ByteStreams.newDataOutput()",
    "byteArrayDataOutput.writeUTF(BackendAdminProtocol.WAKE_PLACE)",
    "byteArrayDataOutput.writeUTF(player.getUniqueId().toString())",
    "byteArrayDataOutput.writeUTF(serverProxyName)",
    "player.sendPluginMessage(this.plugin, BackendAdminProtocol.ADMIN_CHANNEL"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendWakePlaceService missing required wake-place contract text: $required"
    }
}

foreach ($required in @(
    "private BackendWakePlaceService wakePlaceService",
    "this.wakePlaceService = new BackendWakePlaceService((Plugin)this)",
    "private void sendWakePlaceRequest(Player player, ServerId serverId)",
    "serverId != ServerId.SURVIVAL && serverId != ServerId.CREATIVE",
    "this.wakePlaceService.sendWakePlaceRequest(player, serverId.proxyName)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendWakePlaceService: $required"
    }
}

foreach ($forbidden in @(
    "byteArrayDataOutput.writeUTF(BackendAdminProtocol.WAKE_PLACE)"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns wake-place transport detail: $forbidden"
    }
}

Write-Host "LemonOS backend wake-place contract tests passed."
