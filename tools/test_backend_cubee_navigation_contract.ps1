param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendCubeeNavigationService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendCubeeNavigationService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendCubeeNavigationService",
    "private final Map<UUID, CubeeRoot> roots = new ConcurrentHashMap<UUID, CubeeRoot>();",
    "private final Map<UUID, CubeeSurface> surfaces = new ConcurrentHashMap<UUID, CubeeSurface>();",
    "CubeeRoot root(UUID playerId)",
    "return this.roots.getOrDefault(playerId, CubeeRoot.CUBEE);",
    "CubeeSurface surface(UUID playerId)",
    "return this.surfaces.getOrDefault(playerId, CubeeSurface.HOME);",
    "void switchRoot(UUID playerId, CubeeRoot root)",
    "void switchSurface(UUID playerId, CubeeSurface surface)",
    "void reset(UUID playerId)",
    "this.roots.put(playerId, CubeeRoot.CUBEE);",
    "this.surfaces.put(playerId, CubeeSurface.HOME);",
    "void remove(UUID playerId)",
    "void clear()",
    "enum CubeeSurface",
    "HOME,",
    "PLACES,",
    "SANDBOX,",
    "PEOPLE,",
    "ADMIN_PEOPLE;",
    "enum CubeeRoot",
    "CUBEE,",
    "CARE;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendCubeeNavigationService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendCubeeNavigationService cubeeNavigationService;",
    "new BackendCubeeNavigationService()",
    "this.cubeeNavigationService.clear();",
    "this.cubeeNavigationService.reset(player.getUniqueId());",
    "return this.cubeeNavigationService.root(player.getUniqueId());",
    "return this.cubeeNavigationService.surface(player.getUniqueId());",
    "this.cubeeNavigationService.switchRoot(player.getUniqueId(), cubeeRoot);",
    "this.cubeeNavigationService.switchSurface(player.getUniqueId(), cubeeSurface);",
    "this.cubeeNavigationService.remove(uUID);",
    "if (cubeeRoot != CubeeRoot.CARE)",
    "this.clearCareWorldStatus(player.getUniqueId());"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendCubeeNavigationService: $snippet"
    }
}

$removedPluginSnippets = @(
    "private final Map<UUID, CubeeRoot> cubeeRoots",
    "private final Map<UUID, CubeeSurface> cubeeSurfaces",
    "private static enum CubeeSurface",
    "private static enum CubeeRoot",
    "cubeeRoots.",
    "cubeeSurfaces."
)

foreach ($snippet in $removedPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still contains old Cubee navigation state: $snippet"
    }
}

Write-Host "Backend Cubee navigation contract OK"
