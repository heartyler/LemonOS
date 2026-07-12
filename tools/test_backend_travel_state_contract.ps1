param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendTravelStateService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendTravelStateService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendTravelStateService<T>",
    "private final BackendOperationRegistry<UUID, TravelState<T>> travels",
    "private final AtomicLong generationCounter",
    "BackendTravelStateService(ResumeDelay resumeDelay, BackendActionBarCoordinator actionBarCoordinator)",
    "TravelState<T> get(UUID uuid)",
    "boolean contains(UUID uuid)",
    "BackendOperationToken begin(UUID uuid, T target, boolean wake, String status, Function<BackendOperationToken, BukkitTask> scheduler)",
    "boolean showIfCurrent(UUID uuid, BackendOperationToken token, String status)",
    "TravelState<T> endIfCurrent(UUID uuid, BackendOperationToken token)",
    "void cancel(Player player, boolean notify)",
    "void cancelTasksAndClearStatuses()",
    "void clear()",
    "this.travels.removeIfCurrent(uuid, token)",
    "this.resumeDelay.delay(uuid)",
    "new BackendOperationStatusLease(this.actionBarCoordinator, uuid, BackendActionBarCoordinator.Owner.TRAVEL)",
    "Component.text((String)`"nothing changed.`", (TextColor)NamedTextColor.DARK_GRAY)",
    "interface ResumeDelay",
    "static final class TravelState<T>",
    "BackendOperationToken token()",
    "T target()",
    "boolean wake()"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendTravelStateService missing required travel state contract text: $required"
    }
}

foreach ($required in @(
    "private BackendTravelStateService<ServerId> travelStateService",
    "this.travelStateService = new BackendTravelStateService<ServerId>(this::delayAtmosphereMusicActionBarResume, this.actionBarCoordinator)",
    "this.travelStateService.cancelTasksAndClearStatuses()",
    "this.travelStateService.clear()",
    "this.travelStartService = new BackendTravelStartService<ServerId>((Plugin)this, this.travelStateService, this::isBusy, this::isServerAvailable, this::isPlaceWakeable, this::startWakeTravel, this::finishTravel)",
    "this.travelStateService.contains(player.getUniqueId())",
    "this.travelStateService.cancel(player, bl)",
    "this.travelStateService.contains(uUID)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendTravelStateService: $required"
    }
}

foreach ($forbidden in @(
    "private final Map<UUID, TravelState> travels",
    "private static final class TravelState",
    "new TravelState(",
    "this.travels.containsKey",
    "this.travels.put",
    "this.travels.remove",
    "this.travels.clear",
    "private void clearTravelStatus("
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns travel state lifecycle detail: $forbidden"
    }
}

Write-Host "LemonOS backend travel state contract tests passed."
