param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendTravelStartService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendTravelStartService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendTravelStartService<T>",
    "private static final long NORMAL_TRAVEL_DELAY_TICKS = 40L",
    "private final Plugin plugin",
    "private final BackendTravelStateService<T> travelStateService",
    "private final Predicate<UUID> busy",
    "private final Predicate<T> available",
    "private final Predicate<T> wakeable",
    "private final BiConsumer<Player, T> wakeTravel",
    "private final TravelFinisher<T> finishTravel",
    "BackendTravelStartService(Plugin plugin, BackendTravelStateService<T> travelStateService, Predicate<UUID> busy, Predicate<T> available, Predicate<T> wakeable, BiConsumer<Player, T> wakeTravel, TravelFinisher<T> finishTravel)",
    "void start(Player player, T target, T currentTarget)",
    "BackendTravelStateService.TravelState<T> travelState = this.travelStateService.get(player.getUniqueId())",
    "travelState != null && travelState.wake() && Objects.equals(travelState.target(), target)",
    "this.travelStateService.showIfCurrent(player.getUniqueId(), travelState.token(), `"waiting`")",
    "Objects.equals(target, currentTarget) || this.busy.test(player.getUniqueId())",
    "!this.available.test(target)",
    "this.wakeable.test(target)",
    "this.wakeTravel.accept(player, target)",
    "player.closeInventory()",
    "this.travelStateService.begin(player.getUniqueId(), target, false, `"on the way`", token ->",
    "Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.finishTravel.finish(player, target, token), NORMAL_TRAVEL_DELAY_TICKS)",
    "interface TravelFinisher<T>"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendTravelStartService missing required travel start contract text: $required"
    }
}

foreach ($required in @(
    "private BackendTravelStartService<ServerId> travelStartService",
    "this.travelStartService = new BackendTravelStartService<ServerId>((Plugin)this, this.travelStateService, this::isBusy, this::isServerAvailable, this::isPlaceWakeable, this::startWakeTravel, this::finishTravel)",
    "private void startTravel(Player player, ServerId serverId)",
    "this.travelStartService.start(player, serverId, this.currentServer)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendTravelStartService: $required"
    }
}

foreach ($forbidden in @(
    "BackendTravelStateService.TravelState<ServerId> travelState = this.travelStateService.get(player.getUniqueId())",
    "travelState.wake() && travelState.target() == serverId",
    "Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.finishTravel(player, serverId), 40L)",
    "this.beginTravel(player.getUniqueId(), bukkitTask, serverId, false, `"on the way`")"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend travel start orchestration detail: $forbidden"
    }
}

Write-Host "LemonOS backend travel start contract tests passed."
