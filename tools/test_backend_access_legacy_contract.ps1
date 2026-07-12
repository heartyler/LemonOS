param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAccessLegacyService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$routerPath = Join-Path $Root "src\main\java\dev\lemonos\BackendPluginMessageRouterService.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendAccessLegacyService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$router = Get-Content -Raw -LiteralPath $routerPath

foreach ($required in @(
    "final class BackendAccessLegacyService",
    "private static final int ACCESS_CONFIRM_TIMEOUT_TICKS = 100",
    "private final Map<Long, PendingAccessRequest> pendingAccessRequests",
    "private final AtomicLong accessRequestIds",
    "BackendAccessLegacyService(Plugin plugin, Function<String, String> nameNormalizer, AccessStateUpdater accessStateUpdater)",
    "boolean sendAccessRequest(Player player, String normalizedName, boolean admin)",
    "boolean handleAccessMessage(byte[] data)",
    "void cancelPendingRequests()",
    "void clear()",
    "private void handleAccessAck(long requestId, boolean saved)",
    "private void timeoutAccessRequest(long requestId)",
    "interface AccessStateUpdater",
    "void update(Player player, boolean admin)",
    "private static final class PendingAccessRequest",
    "Bukkit.getScheduler().runTaskLater(this.plugin",
    "byteArrayDataOutput.writeByte(BackendAdminProtocol.ACCESS_REQUEST_MAGIC)",
    "byteArrayDataOutput.writeLong(requestId)",
    "byteArrayDataOutput.writeByte(admin ? 1 : 0)",
    "byteArrayDataOutput.writeUTF(normalizedName)",
    "player.sendPluginMessage(this.plugin, BackendAdminProtocol.ADMIN_CHANNEL",
    "data.length == 10 && data[0] == BackendAdminProtocol.ACCESS_ACK_MAGIC",
    "new UUID(byteBuffer.getLong(), byteBuffer.getLong())",
    "this.accessStateUpdater.update(player, admin)",
    "this.nameNormalizer.apply(onlinePlayer.getName()).equals(pendingAccessRequest.name)",
    "this.accessStateUpdater.update(onlinePlayer, pendingAccessRequest.admin)",
    "Component.text((String)`"try again.`"",
    "Component.text((String)`"saved.`""
)) {
    if (-not $service.Contains($required)) {
        throw "BackendAccessLegacyService missing required legacy access contract text: $required"
    }
}

foreach ($required in @(
    "private BackendAccessLegacyService accessLegacyService",
    "this.accessLegacyService = new BackendAccessLegacyService((Plugin)this, this::normalizeAccessName, this::updateProxyAdmin)",
    "this.accessLegacyService.cancelPendingRequests()",
    "this.accessLegacyService.clear()",
    "private void updateProxyAdmin(Player player, boolean bl)",
    "return this.accessLegacyService.sendAccessRequest(player, string2, bl)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendAccessLegacyService: $required"
    }
}

if (-not $router.Contains("this.accessLegacyService.handleAccessMessage(data);")) {
    throw "BackendPluginMessageRouterService is not wired through BackendAccessLegacyService."
}

foreach ($forbidden in @(
    "private final Map<Long, PendingAccessRequest> pendingAccessRequests",
    "private final AtomicLong accessRequestIds",
    "private void handleAccessAck(",
    "private void timeoutAccessRequest(",
    "private static final class PendingAccessRequest",
    "byteArrayDataOutput.writeByte(ACCESS_REQUEST_MAGIC)",
    "byArray.length == 10 && byArray[0] == ACCESS_ACK_MAGIC"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend legacy access detail: $forbidden"
    }
}

Write-Host "LemonOS backend legacy access contract tests passed."
