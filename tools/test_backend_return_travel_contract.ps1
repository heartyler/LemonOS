param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendReturnTravelService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendReturnTravelService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendReturnTravelService",
    "private static final long RETURN_TRAVEL_DELAY_TICKS = 40L",
    "private final Plugin plugin",
    "private final BackendTravelStateService<?> travelStateService",
    "private final Predicate<UUID> busy",
    "private final BiPredicate<Location, Location> sameBlockLocation",
    "BackendReturnTravelService(Plugin plugin, BackendTravelStateService<?> travelStateService, Predicate<UUID> busy, BiPredicate<Location, Location> sameBlockLocation)",
    "void returnHome(Player player, Supplier<Location> locationSupplier)",
    "this.busy.test(player.getUniqueId()) || locationSupplier.get() == null",
    "void returnSpawn(Player player, Supplier<Location> locationSupplier)",
    "this.busy.test(player.getUniqueId())",
    "private void start(Player player, Supplier<Location> locationSupplier)",
    "player.closeInventory()",
    "this.travelStateService.begin(player.getUniqueId(), null, false, `"on the way`", token ->",
    "Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.finish(player, locationSupplier, token), RETURN_TRAVEL_DELAY_TICKS)",
    "private void finish(Player player, Supplier<Location> locationSupplier, BackendOperationToken token)",
    "this.travelStateService.endIfCurrent(player.getUniqueId(), token)",
    "if (!player.isOnline())",
    "Location location = locationSupplier.get()",
    "if (location == null)",
    "if (!player.teleport(location))",
    "Bukkit.getScheduler().runTask(this.plugin, () -> {",
    "this.sameBlockLocation.test(player.getLocation(), location)",
    "Component.text((String)`"nothing changed.`", (TextColor)NamedTextColor.DARK_GRAY)",
    "Component.text((String)`"you're here.`", (TextColor)NamedTextColor.GRAY)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendReturnTravelService missing required return travel contract text: $required"
    }
}

foreach ($required in @(
    "private BackendReturnTravelService returnTravelService",
    "this.returnTravelService = new BackendReturnTravelService((Plugin)this, this.travelStateService, this::isBusy, this::sameBlockLocation)",
    "private void returnSurvivalHome(Player player)",
    "this.returnTravelService.returnHome(player, () -> this.survivalHomeLocation(player))",
    "private void returnServerSpawn(Player player)",
    "this.returnTravelService.returnSpawn(player, this::currentServerSpawnLocation)",
    "private Location currentServerSpawnLocation()",
    "World world = Bukkit.getWorld((String)this.placeSpawnWorld(this.currentServer))",
    "return world.getSpawnLocation()"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendReturnTravelService: $required"
    }
}

foreach ($forbidden in @(
    "Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.finishSurvivalHome(player), 40L)",
    "Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.finishReturnServerSpawn(player), 40L)",
    "this.beginTravel(player.getUniqueId(), bukkitTask);`r`n    }`r`n`r`n    private void finishSurvivalHome",
    "this.beginTravel(player.getUniqueId(), bukkitTask);`r`n    }`r`n`r`n    private void finishReturnServerSpawn"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend return travel orchestration detail: $forbidden"
    }
}

Write-Host "LemonOS backend return travel contract tests passed."
