param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$policy = Get-Content -Raw (Join-Path $Root "src\main\java\dev\lemonos\BackendWorldPolicy.java")
$plugin = Get-Content -Raw (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    'case "lobby" -> new BackendWorldPolicy(true, true, true, true, true)',
    'case "creative" -> new BackendWorldPolicy(false, true, true, true, true)',
    'case "survival" -> new BackendWorldPolicy(false, false, false, false, false)')) {
    if (-not $policy.Contains($required)) { throw "World policy missing: $required" }
}
foreach ($required in @('for (World world : Bukkit.getWorlds())', 'onWorldLoad(WorldLoadEvent worldLoadEvent)',
    'GameRules.SPAWN_MOBS', 'GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER',
    'this.worldPolicy.disableFireTick() ? 0 : 128', 'GameRules.PVP', 'GameRules.FALL_DAMAGE')) {
    if (-not $plugin.Contains($required)) { throw "World policy integration missing: $required" }
}
foreach ($forbidden in @('randomTickSpeed', 'isCreativeSafeDamage', 'EntitiesLoadEvent')) {
    if ($plugin.Contains($forbidden)) { throw "Forbidden legacy/cleanup behavior remains: $forbidden" }
}
Write-Host "LemonOS backend world policy contract tests passed."
