param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAtmosphereWorldService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendAtmosphereWorldService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendAtmosphereWorldService<T, W>",
    "private final Map<String, WorldAtmosphereState<T, W>> worldAtmosphereStates = new ConcurrentHashMap<String, WorldAtmosphereState<T, W>>()",
    "void clear()",
    "PhaseChange observe(String worldId, T timePhase, W weatherPhase)",
    "this.worldAtmosphereStates.put(worldId, new WorldAtmosphereState<T, W>(timePhase, weatherPhase))",
    "return new PhaseChange(false, false)",
    "boolean timeChanged = state.timePhase != timePhase",
    "boolean weatherChanged = state.weatherPhase != weatherPhase",
    "state.timePhase = timePhase",
    "state.weatherPhase = weatherPhase",
    "return new PhaseChange(timeChanged, weatherChanged)",
    "static final class PhaseChange",
    "boolean timeChanged()",
    "boolean weatherChanged()",
    "private static final class WorldAtmosphereState<T, W>"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendAtmosphereWorldService missing required world phase contract text: $required"
    }
}

foreach ($required in @(
    "private BackendAtmosphereWorldService<TimePhase, WeatherPhase> atmosphereWorldService",
    "this.atmosphereWorldService = new BackendAtmosphereWorldService<TimePhase, WeatherPhase>()",
    "this.atmosphereWorldService.clear()",
    "BackendAtmosphereWorldService.PhaseChange phaseChange = this.atmosphereWorldService.observe(string, timePhase, weatherPhase)",
    "if (phaseChange.timeChanged())",
    'this.broadcastAtmosphere(world, "time." + timePhase.key, l)',
    "if (phaseChange.weatherChanged())",
    'this.broadcastAtmosphere(world, "weather." + weatherPhase.key, l)'
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendAtmosphereWorldService: $required"
    }
}

foreach ($forbidden in @(
    "private final Map<String, WorldAtmosphereState> worldAtmosphereStates",
    "this.worldAtmosphereStates.clear",
    "this.worldAtmosphereStates.get",
    "this.worldAtmosphereStates.put",
    "private static final class WorldAtmosphereState"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns atmosphere world phase state detail: $forbidden"
    }
}

Write-Host "LemonOS backend atmosphere world contract tests passed."
