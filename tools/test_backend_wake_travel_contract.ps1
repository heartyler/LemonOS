param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendWakeTravelService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendWakeTravelService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendWakeTravelService<T>",
    "private static final long WAKE_TIMEOUT_MILLIS = 120000L",
    "private static final long INITIAL_DELAY_TICKS = 1L",
    "private static final long POLL_PERIOD_TICKS = 20L",
    "private final Plugin plugin",
    "private final BackendTravelStateService<T> travelStateService",
    "private final BiConsumer<Player, T> wakeRequest",
    "private final BiConsumer<T, String> runtimeStatus",
    "private final Predicate<T> canConnect",
    "private final TravelFinisher<T> finishTravel",
    "BackendWakeTravelService(Plugin plugin, BackendTravelStateService<T> travelStateService, BiConsumer<Player, T> wakeRequest, BiConsumer<T, String> runtimeStatus, Predicate<T> canConnect, TravelFinisher<T> finishTravel)",
    "void start(Player player, T target, String wakingStatus)",
    "player.closeInventory()",
    "this.wakeRequest.accept(player, target)",
    "this.runtimeStatus.accept(target, wakingStatus)",
    "long timeoutAt = System.nanoTime() / 1_000_000L + WAKE_TIMEOUT_MILLIS",
    "this.travelStateService.begin(player.getUniqueId(), target, true, `"waiting`", token ->",
    "Bukkit.getScheduler().runTaskTimer(this.plugin",
    "this.travelStateService.endIfCurrent(player.getUniqueId(), token)",
    "this.canConnect.test(target)",
    "this.finishTravel.finish(player, target, token)",
    "Component.text((String)`"no signal.`", (TextColor)NamedTextColor.DARK_GRAY)",
    "this.travelStateService.showIfCurrent(player.getUniqueId(), token, `"waiting`")",
    "interface TravelFinisher<T>"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendWakeTravelService missing required wake travel contract text: $required"
    }
}

foreach ($required in @(
    "private BackendWakeTravelService<ServerId> wakeTravelService",
    "this.wakeTravelService = new BackendWakeTravelService<ServerId>((Plugin)this, this.travelStateService, this::sendWakePlaceRequest, (serverId, status) -> this.setPlaceRuntimeStatus(serverId, status), this.placeRuntimeService::canConnect, this::finishTravel)",
    "private void startWakeTravel(Player player, ServerId serverId)",
    "this.wakeTravelService.start(player, serverId, this.restWakingStatus())"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendWakeTravelService: $required"
    }
}

foreach ($forbidden in @(
    "long l = System.currentTimeMillis() + 120000L",
    "BukkitTask[] bukkitTaskArray = new BukkitTask[1]",
    "this.beginTravel(player.getUniqueId(), bukkitTaskArray[0], serverId, true, `"waiting`")"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend wake travel orchestration detail: $forbidden"
    }
}

Write-Host "LemonOS backend wake travel contract tests passed."
