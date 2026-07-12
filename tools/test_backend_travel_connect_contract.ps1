param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendTravelConnectService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendTravelConnectService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendTravelConnectService",
    "private static final String CONNECT_COMMAND = `"Connect`"",
    "BackendTravelConnectService(Plugin plugin, String channel)",
    "void connect(Player player, String serverProxyName)",
    "ByteStreams.newDataOutput()",
    "byteArrayDataOutput.writeUTF(CONNECT_COMMAND)",
    "byteArrayDataOutput.writeUTF(serverProxyName)",
    "player.sendPluginMessage(this.plugin, this.channel, byteArrayDataOutput.toByteArray())"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendTravelConnectService missing required connect contract text: $required"
    }
}

foreach ($required in @(
    "private BackendTravelConnectService travelConnectService",
    "this.travelConnectService = new BackendTravelConnectService((Plugin)this, BUNGEE_CHANNEL)",
    "private BackendTravelFinishService<ServerId> travelFinishService",
    "this.travelFinishService = new BackendTravelFinishService<ServerId>(this.travelStateService, this.travelConnectService, this::saveIdentityTransfer, serverId -> serverId.proxyName)",
    "private void finishTravel(Player player, ServerId serverId, BackendOperationToken token)",
    "this.travelFinishService.finish(player, serverId, token)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendTravelConnectService: $required"
    }
}

foreach ($forbidden in @(
    "byteArrayDataOutput.writeUTF(`"Connect`")",
    "player.sendPluginMessage((Plugin)this, BUNGEE_CHANNEL, byteArrayDataOutput.toByteArray())"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns Bungee connect transport detail: $forbidden"
    }
}

Write-Host "LemonOS backend travel connect contract tests passed."
