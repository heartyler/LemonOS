param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendDisplayBoardLifecycleService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendDisplayBoardLifecycleService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendDisplayBoardLifecycleService",
    "TextDisplay findUniqueDisplay(World world, String role, Function<TextDisplay, String> roleReader)",
    "TextDisplay selected = null",
    "for (TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class))",
    "String displayRole = roleReader.apply(textDisplay)",
    "if (!role.equals(displayRole))",
    "if (selected == null)",
    "selected = textDisplay",
    "textDisplay.remove()",
    "return selected",
    "void clearDisplays(Iterable<World> worlds, Function<TextDisplay, String> roleReader, Predicate<String> rolePredicate)",
    "for (World world : worlds)",
    "if (displayRole != null && rolePredicate.test(displayRole))",
    "void clearDisplayBoard(World world, Location location, Function<TextDisplay, String> roleReader, Predicate<String> rolePredicate, BiPredicate<TextDisplay, Location> nearBasePredicate)",
    "if (displayRole != null)",
    "if (rolePredicate.test(displayRole))",
    "continue",
    "if (nearBasePredicate.test(textDisplay, location))"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendDisplayBoardLifecycleService missing required lifecycle contract text: $required"
    }
}

foreach ($required in @(
    "private BackendDisplayBoardLifecycleService displayBoardLifecycleService",
    "this.displayBoardLifecycleService = new BackendDisplayBoardLifecycleService()",
    "return this.displayBoardLifecycleService.findUniqueDisplay(world, string, this::displayRole)",
    "this.displayBoardLifecycleService.clearDisplays(Bukkit.getWorlds(), this::displayRole, role -> role != null && role.startsWith(string))",
    "this.displayBoardLifecycleService.clearDisplayBoard(world, location, this::displayRole, predicate, this::isNearDisplayBase)",
    "this.displayBoardLifecycleService.clearDisplays(Bukkit.getWorlds(), this::displayRole, role -> this.isStayedCloseDisplayRole(role) || this.isLegacyStayedCloseDisplayRole(role))",
    "this.displayBoardLifecycleService.clearDisplays(Bukkit.getWorlds(), this::currentDisplayRole, this::isBedrockStayedCloseRole)",
    "private boolean isNearDisplayBase(TextDisplay textDisplay, Location location)",
    "location2.distanceSquared(location) <= DISPLAY_BOARD_CLEAR_RADIUS_SQUARED"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendDisplayBoardLifecycleService: $required"
    }
}

foreach ($forbidden in @(
    "private TextDisplay findHudDisplay(World world, String string) {`r`n        TextDisplay textDisplay = null;",
    "private TextDisplay findStayedCloseDisplay(World world, String string) {`r`n        TextDisplay textDisplay = null;",
    "private void clearHudDisplayPrefix(String string) {`r`n        for (World world : Bukkit.getWorlds())",
    "private void clearStayedCloseDisplays() {`r`n        for (World world : Bukkit.getWorlds())",
    "private void clearStayedCloseBedrockDisplays() {`r`n        for (World world : Bukkit.getWorlds())",
    "private void clearDisplayBoard(World world, Location location, Predicate<String> predicate) {`r`n        for (TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class))"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns duplicated display lifecycle loop: $forbidden"
    }
}

Write-Host "LemonOS backend display lifecycle contract tests passed."
