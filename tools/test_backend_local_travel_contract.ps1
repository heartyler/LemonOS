param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendLocalTravelService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendLocalTravelService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendLocalTravelService",
    "private static final long LOCAL_TRAVEL_DELAY_TICKS = 40L",
    "private final Plugin plugin",
    "private final BackendTravelStateService<?> travelStateService",
    "private final BiPredicate<Location, Location> sameBlockLocation",
    "BackendLocalTravelService(Plugin plugin, BackendTravelStateService<?> travelStateService, BiPredicate<Location, Location> sameBlockLocation)",
    "void start(Player player, Player target)",
    "this.travelStateService.contains(player.getUniqueId())",
    "player.closeInventory()",
    "this.travelStateService.begin(player.getUniqueId(), null, false, `"on the way`", token ->",
    "Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.finish(player, target, token), LOCAL_TRAVEL_DELAY_TICKS)",
    "private void finish(Player player, Player target, BackendOperationToken token)",
    "this.travelStateService.endIfCurrent(player.getUniqueId(), token)",
    "if (!player.isOnline() || !target.isOnline())",
    "if (player.isOnline())",
    "Location location = target.getLocation()",
    "if (!player.teleport(location))",
    "Bukkit.getScheduler().runTask(this.plugin, () -> {",
    "this.sameBlockLocation.test(player.getLocation(), location)",
    "Component.text((String)`"nothing changed.`", (TextColor)NamedTextColor.DARK_GRAY)",
    "Component.text((String)`"try again.`", (TextColor)NamedTextColor.DARK_GRAY)",
    "Component.text((String)`"you're here.`", (TextColor)NamedTextColor.GRAY)",
    "target.sendMessage(((TextComponent)Component.text((String)player.getName(), (TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.space())).append((Component)Component.text((String)`"is here.`", (TextColor)NamedTextColor.GRAY)))"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendLocalTravelService missing required local travel contract text: $required"
    }
}

foreach ($required in @(
    "private BackendLocalTravelService localTravelService",
    "this.localTravelService = new BackendLocalTravelService((Plugin)this, this.travelStateService, this::sameBlockLocation)",
    "private void startLocalTravel(Player player, Player player2)",
    "this.localTravelService.start(player, player2)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendLocalTravelService: $required"
    }
}

foreach ($forbidden in @(
    "private void finishLocalTravel(Player player, Player player2)",
    "Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.finishLocalTravel(player, player2), 40L)",
    "this.beginTravel(player.getUniqueId(), bukkitTask);`r`n    }`r`n`r`n    private void finishLocalTravel"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend local travel orchestration detail: $forbidden"
    }
}

Write-Host "LemonOS backend local travel contract tests passed."
