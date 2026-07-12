param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendOpenCubeeMessageService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$routerPath = Join-Path $Root "src\main\java\dev\lemonos\BackendPluginMessageRouterService.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendOpenCubeeMessageService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$router = Get-Content -Raw -LiteralPath $routerPath

foreach ($required in @(
    "final class BackendOpenCubeeMessageService",
    "BackendOpenCubeeMessageService(Plugin plugin, OpenCubeeHandler openCubeeHandler)",
    "boolean handleOpenCubeeMessage(Player player, byte[] data)",
    "interface OpenCubeeHandler",
    "void handle(Player player, boolean recovery)",
    "new DataInputStream(new ByteArrayInputStream(data))",
    "BackendAdminProtocol.OPEN_CUBEE.equals(command)",
    "UUID.fromString(dataInputStream.readUTF())",
    "boolean recovery = dataInputStream.available() > 0 && dataInputStream.readBoolean()",
    "Bukkit.getScheduler().runTask(this.plugin",
    "Bukkit.getPlayer(uuid)",
    "targetPlayer == null || !targetPlayer.isOnline()",
    "this.openCubeeHandler.handle(targetPlayer, recovery)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendOpenCubeeMessageService missing required open-cubee contract text: $required"
    }
}

if ($service.Contains("player.getUniqueId().equals(uuid)")) {
    throw "BackendOpenCubeeMessageService must route by payload UUID, not plugin-message carrier UUID."
}

foreach ($required in @(
    "private BackendOpenCubeeMessageService openCubeeMessageService",
    "this.openCubeeMessageService = new BackendOpenCubeeMessageService((Plugin)this, this::handleProxyOpenCubee)",
    "private void handleProxyOpenCubee(Player player, boolean bl)",
    "this.handleHelpCommand(player)",
    "this.handlePadCommand(player)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendOpenCubeeMessageService: $required"
    }
}

if (-not $router.Contains("this.openCubeeMessageService.handleOpenCubeeMessage(player, data)")) {
    throw "BackendPluginMessageRouterService is not wired through BackendOpenCubeeMessageService."
}

foreach ($forbidden in @(
    "private boolean handleOpenCubeeMessage(",
    "BackendAdminProtocol.OPEN_CUBEE.equals(string)",
    "OPEN_HOMEPAD"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend open-cubee transport detail: $forbidden"
    }
}

Write-Host "LemonOS backend open-cubee message contract tests passed."
