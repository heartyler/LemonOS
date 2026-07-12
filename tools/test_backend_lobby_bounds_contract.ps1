param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw (Join-Path $Root "src\main\java\dev\lemonos\BackendLobbyBoundsService.java")
$plugin = Get-Content -Raw (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    'spawn.getX() - half', 'spawn.getX() + half', 'spawn.getZ() - half', 'spawn.getZ() + half',
    'location.getWorld().getUID().equals(lobbyWorld.getUID())', 'Math.max(bounds.minX()', 'Math.max(bounds.minZ()')) {
    if (-not $service.Contains($required)) { throw "Lobby bounds service missing: $required" }
}
foreach ($required in @(
    'new BackendLobbyBoundsService(LOBBY_BORDER_SIZE)',
    'this.lobbyBoundsService.appliesTo(playerMoveEvent.getTo(), lobbyWorld)',
    'this.lobbyBoundsService.outside(playerMoveEvent.getTo(), lobbySpawn)',
    'this.lobbyBoundsService.clamp(playerMoveEvent.getTo(), lobbySpawn)',
    'world.getWorldBorder().setCenter(spawn.getX(), spawn.getZ())')) {
    if (-not $plugin.Contains($required)) { throw "Lobby bounds integration missing: $required" }
}
foreach ($forbidden in @('outsideLobbyBounds(', 'clampLobbyLocation(', 'setCenter(0.0, 0.0)')) {
    if ($plugin.Contains($forbidden)) { throw "Legacy hardcoded Lobby bounds remain: $forbidden" }
}
Write-Host "LemonOS Lobby spawn-centered bounds contract tests passed."
