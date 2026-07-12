param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSkinProtocolService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$routerPath = Join-Path $Root "src\main\java\dev\lemonos\BackendPluginMessageRouterService.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendSkinProtocolService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$router = Get-Content -Raw -LiteralPath $routerPath

foreach ($required in @(
    "final class BackendSkinProtocolService",
    "BackendSkinProtocolService(Plugin plugin, SkinResultHandler resultHandler)",
    "void sendSkinApplyRequest(Player player, String skinName)",
    "boolean handleSkinResultMessage(byte[] data)",
    "interface SkinResultHandler",
    "void handle(UUID uuid, String skinName, String result)",
    "ByteStreams.newDataOutput()",
    "byteArrayDataOutput.writeUTF(BackendAdminProtocol.SKIN_APPLY)",
    "byteArrayDataOutput.writeUTF(player.getUniqueId().toString())",
    "byteArrayDataOutput.writeUTF(skinName)",
    "player.sendPluginMessage(this.plugin, BackendAdminProtocol.ADMIN_CHANNEL",
    "new DataInputStream(new ByteArrayInputStream(data))",
    "BackendAdminProtocol.SKIN_RESULT.equals(command)",
    "UUID.fromString(dataInputStream.readUTF())",
    "Bukkit.getScheduler().runTask(this.plugin",
    "this.resultHandler.handle(uuid, skinName, result)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendSkinProtocolService missing required skin protocol contract text: $required"
    }
}

foreach ($required in @(
    "private BackendSkinProtocolService skinProtocolService",
    "this.skinProtocolService = new BackendSkinProtocolService((Plugin)this, this::finishProxySkinApply)",
    "this.skinProtocolService.sendSkinApplyRequest(player, string)",
    "private void finishProxySkinApply(UUID uUID, String string, String string2)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendSkinProtocolService: $required"
    }
}

if (-not $router.Contains("this.skinProtocolService.handleSkinResultMessage(data)")) {
    throw "BackendPluginMessageRouterService is not wired through BackendSkinProtocolService."
}

foreach ($forbidden in @(
    "private void sendSkinApplyRequest(",
    "private boolean handleSkinResultMessage(",
    "byteArrayDataOutput.writeUTF(BackendAdminProtocol.SKIN_APPLY)",
    "BackendAdminProtocol.SKIN_RESULT.equals(string)",
    "Bukkit.getScheduler().runTask((Plugin)this, () -> this.finishProxySkinApply"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend skin protocol transport detail: $forbidden"
    }
}

Write-Host "LemonOS backend skin protocol contract tests passed."
