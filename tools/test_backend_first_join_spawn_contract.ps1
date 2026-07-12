param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw (Join-Path $Root "src\main\java\dev\lemonos\BackendFirstJoinSpawnService.java")
$plugin = Get-Content -Raw (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    'spawn.getBlockX() + 0.5', 'spawn.getBlockZ() + 0.5', 'location.getBlock().isPassable()',
    'add(0.0, 1.0, 0.0).getBlock().isPassable()', 'add(0.0, -1.0, 0.0).getBlock().getType().isSolid()',
    'spawn.getBlockY() + 16')) {
    if (-not $service.Contains($required)) { throw "First-join spawn service missing: $required" }
}
foreach ($required in @(
    'onFirstJoinSpawn(AsyncPlayerSpawnLocationEvent event)', 'priority=EventPriority.HIGHEST',
    'this.currentServer != ServerId.LOBBY || !event.isNewPlayer()',
    'this.lobbyFirstJoinSpawn', 'event.setSpawnLocation(safeSpawn.clone())',
    'onWorldSpawnChange(SpawnChangeEvent event)', 'this.refreshLobbyFirstJoinSpawn(world)',
    'First-join spawn override unavailable', 'First-join spawn fallback')) {
    if (-not $plugin.Contains($required)) { throw "First-join spawn integration missing: $required" }
}
Write-Host "LemonOS first-join spawn contract tests passed."
