param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendCareWorldStatusService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendCareWorldStatusService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendCareWorldStatusService",
    "private final Map<UUID, String> lastStatuses = new ConcurrentHashMap<UUID, String>();",
    "ConcurrentHashMap.newKeySet()",
    "boolean shouldActivate(boolean bedrockPlayer)",
    "return !bedrockPlayer;",
    "void activate(UUID uuid)",
    "this.viewers.add(uuid);",
    "boolean contains(UUID uuid)",
    "List<UUID> viewerSnapshot()",
    "return List.copyOf(this.viewers);",
    "void clear(UUID uuid)",
    "this.viewers.remove(uuid);",
    "this.lastStatuses.remove(uuid);",
    "void clearAll()",
    "boolean removeLastStatus(UUID uuid)",
    "String takeLastStatus(UUID uuid)",
    "String rememberStatus(UUID uuid, String status)",
    "status == null ? this.lastStatuses.remove(uuid) : this.lastStatuses.put(uuid, status)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendCareWorldStatusService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendCareWorldStatusService careWorldStatusService;",
    "this.careWorldStatusService = new BackendCareWorldStatusService();",
    "this.careWorldStatusService.viewerSnapshot()",
    "this.careWorldStatusService.clearAll();",
    "this.careWorldStatusService.shouldActivate(this.isBedrockPlayer(player))",
    "this.switchCubeeRoot(player, CubeeRoot.CARE);",
    "this.careWorldStatusService.activate(uUID);",
    "this.careWorldStatusService.clear(uUID);",
    "this.delayAtmosphereMusicActionBarResume(uUID);",
    "this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.CARE_WORLD)",
    "this.careWorldStatusService.contains(uUID)",
    "this.careWorldStatusService.removeLastStatus(uUID)",
    "this.careWorldStatusService.takeLastStatus(uUID)",
    "this.careWorldStatusService.rememberStatus(uUID, string)",
    "!this.careWorldStatusService.contains(uUID)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendCareWorldStatusService: $snippet"
    }
}

if ($Plugin.Contains("careWorldStatusViewers") -or $Plugin.Contains("careWorldLastStatuses")) {
    throw "LemonOSPlugin still owns raw care world status collections."
}

Write-Host "Backend care world status contract OK"
