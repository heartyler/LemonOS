param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendDisplayPlacementService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendDisplayPlacementService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendDisplayPlacementService",
    "Placement hudPlacement(BackendDisplayConfig config, String configPath, String fallbackWorld)",
    "private Placement placement(BackendDisplayConfig config, String configPath, String fallbackWorld)",
    "config.stringValue(configPath + `".display.world`", fallbackWorld)",
    "config.doubleValue(configPath + `".display.x`", 5.42, -30000000.0, 30000000.0)",
    "config.doubleValue(configPath + `".display.y`", -60.86, -2048.0, 2048.0)",
    "config.doubleValue(configPath + `".display.z`", 0.5, -30000000.0, 30000000.0)",
    "config.doubleValue(configPath + `".display.yaw`", 90.0, -180.0, 180.0)",
    "config.doubleValue(configPath + `".display.pitch`", 0.0, -90.0, 90.0)",
    "static final class Placement",
    "String worldName()",
    "double x()",
    "double y()",
    "double z()",
    "float yaw()",
    "float pitch()"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendDisplayPlacementService missing required placement contract text: $required"
    }
}

foreach ($required in @(
    "private BackendDisplayPlacementService displayPlacementService",
    "this.displayPlacementService = new BackendDisplayPlacementService()",
    "BackendDisplayPlacementService.Placement placement = this.displayPlacementService.hudPlacement(this.backendDisplayConfig(), string5, this.placeSpawnWorld(this.currentServer))",
    "BackendDisplayPlacementService.Placement placement = this.displayPlacementService.hudPlacement(this.backendDisplayConfig(), hudDefinition.configPath(), this.placeSpawnWorld(ServerId.LOBBY))",
    "World world = Bukkit.getWorld((String)placement.worldName())",
    "Location location = this.backendDisplayLocation(world, placement)",
    "this.forceLoadBackendDisplayLocation(world, location)",
    "private Location backendDisplayLocation(World world, BackendDisplayPlacementService.Placement placement)",
    "new Location(world, placement.x(), placement.y(), placement.z())",
    "location.setYaw(placement.yaw())",
    "location.setPitch(placement.pitch())",
    "private void forceLoadBackendDisplayLocation(World world, Location location)",
    "this.boardChunkLeaseService.acquire(world, location.getBlockX() >> 4, location.getBlockZ() >> 4)",
    "if (!world.getChunkAt(location).isLoaded())",
    "world.getChunkAt(location).load()"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendDisplayPlacementService: $required"
    }
}

foreach ($forbidden in @(
    "World world = Bukkit.getWorld((String)this.configString(string5 + `".display.world`"",
    "World world = Bukkit.getWorld((String)this.configString(`"stayed-close.display.world`"",
    "Location location = new Location(world, this.configDouble(string5 + `".display.x`"",
    "Location location = new Location(world, this.configDouble(`"stayed-close.display.x`"",
    "location.setYaw((float)this.configDouble(string5 + `".display.yaw`"",
    "location.setYaw((float)this.configDouble(`"stayed-close.display.yaw`""
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns duplicated display placement config detail: $forbidden"
    }
}

Write-Host "LemonOS backend display placement contract tests passed."
