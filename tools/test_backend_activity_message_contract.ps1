param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendActivityMessageService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendActivityMessageService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendActivityMessageService",
    "private final Map<UUID, ActivityState> activityStates = new ConcurrentHashMap<UUID, ActivityState>()",
    "void remove(UUID uuid)",
    "boolean sessionReady(UUID uuid, long nowMillis, int thresholdMinutes)",
    "long thresholdMillis = (long)thresholdMinutes * 60000L",
    "void markSessionShown(UUID uuid, long nowMillis)",
    "boolean recordProgress(UUID uuid, String trigger, int amount, int threshold)",
    "int total = state.counters.getOrDefault(trigger, 0) + amount",
    "void resetProgress(UUID uuid, String trigger)",
    "boolean canShow(UUID uuid, String trigger, long nowMillis, int globalCooldownSeconds, int triggerCooldownSeconds)",
    "nowMillis - state.lastGlobalMillis < (long)globalCooldownSeconds * 1000L",
    "long lastTriggerMillis = state.lastShownMillis.getOrDefault(trigger, 0L)",
    "lastTriggerMillis <= 0L || nowMillis - lastTriggerMillis >= (long)triggerCooldownSeconds * 1000L",
    "void markShown(UUID uuid, String trigger, long nowMillis)",
    "int defaultThreshold(String trigger)",
    'case "break-blocks" -> 500',
    'case "place-blocks" -> 350',
    'case "pickup-items" -> 240',
    'case "craft-items" -> 64',
    'case "damage-survived" -> 7',
    'case "session-minutes" -> 30',
    "private static final class ActivityState"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendActivityMessageService missing required activity message contract text: $required"
    }
}

foreach ($required in @(
    "private BackendActivityMessageService activityMessageService",
    "this.activityMessageService = new BackendActivityMessageService()",
    "this.activityMessageService.remove(playerQuitEvent.getPlayer().getUniqueId())",
    'this.activityMessageService.sessionReady(player.getUniqueId(), l, this.activityThreshold("session-minutes", 30))',
    "this.activityMessageService.markSessionShown(player.getUniqueId(), l)",
    "this.activityMessageService.recordProgress(player.getUniqueId(), string, n, this.activityThreshold(string, this.defaultActivityThreshold(string)))",
    "this.activityMessageService.resetProgress(player.getUniqueId(), string)",
    "this.activityMessageService.canShow(uUID, string, l, this.activityGlobalCooldownSeconds(), this.activityCooldownSeconds(string))",
    "this.activityMessageService.markShown(uUID, string, l)",
    "return this.activityMessageService.defaultThreshold(string)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendActivityMessageService: $required"
    }
}

foreach ($forbidden in @(
    "private final Map<UUID, ActivityState> activityStates",
    "this.activityStates.remove",
    "this.activityStates.computeIfAbsent",
    "private static final class ActivityState"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns activity actionbar state detail: $forbidden"
    }
}

Write-Host "LemonOS backend activity message contract tests passed."
