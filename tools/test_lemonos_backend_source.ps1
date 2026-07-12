param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$sourcePath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$source = Get-Content -Raw -LiteralPath $sourcePath
$openCubeeSource = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendOpenCubeeMessageService.java")
$placeStatusSource = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendPlaceStatusService.java")
$runtimeProbeSource = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaceRuntimeProbe.java")
$proxyPlaceStatusSource = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaceStatusRepository.java")
$backupOperationSource = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendBackupOperationService.java")

$forbiddenPatterns = @(
    "world.save()"
    "GameRule.RANDOM_TICK_SPEED"
    "Class.forName(`"org.bukkit.GameRule`")"
    'setGameRule(world, "randomTickSpeed"'
)
$forbiddenSymbols = @(
    "SkinPropertyData"
    "SkinApplyResult"
    "applySkinProperty"
    "resolveSkin"
    "openAdminKeyHolder"
    "openBedrockAdminKeyHolder"
    "mapsEnabled"
    "mapUrl"
    "mapUrlFor"
    "mapAvailable"
    "openMapLink"
)

foreach ($pattern in $forbiddenPatterns) {
    if ($source.Contains($pattern)) {
        throw "LemonOS backend source still contains forbidden runtime pattern: $pattern"
    }
}

foreach ($symbol in $forbiddenSymbols) {
    if ([regex]::IsMatch($source, "\b$([regex]::Escape($symbol))\b")) {
        throw "LemonOS backend source still contains removed dead symbol: $symbol"
    }
}

if ($source.Contains("Ui.Care.MAPS") -or $source.Contains("Ui.Home.MAPS") -or
    $source.Contains("ClickEvent.openUrl") -or $source.Contains("127.0.0.1:810")) {
    throw "LemonOS backend source still contains removed BlueMap/Cubee map-link surface."
}

$templateConfig = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\config.yml")
if ($templateConfig.Contains("maps:") -or $templateConfig.Contains("127.0.0.1:810")) {
    throw "LemonOS runtime template still contains removed BlueMap map config."
}

if (-not $backupOperationSource.Contains("catch (AtomicMoveNotSupportedException") -or
    -not $backupOperationSource.Contains("StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE") -or
    -not $backupOperationSource.Contains("Files.move(tmpPath, targetPath, StandardCopyOption.REPLACE_EXISTING)")) {
    throw "LemonOS backup archive replacement must keep a non-atomic move fallback."
}

if (-not $source.Contains('this.setMissing(this.config, "stayed-close.title", "Stayclose")')) {
    throw "LemonOS Stayed Close title default must be migrated into config."
}

if (-not $source.Contains('this.stayedCloseDisplayService.model(this.backendDisplayConfig(), arrayList)')) {
    throw "LemonOS Stayed Close display must render through the backend display model."
}

$displayServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendStayedCloseDisplayService.java"
$displayService = Get-Content -Raw -LiteralPath $displayServicePath
if (-not $displayService.Contains('config.stringValue("stayed-close.title", "Stayclose").trim()')) {
    throw "LemonOS Stayed Close display title must render from config."
}

if ($source.Contains('Component.text((String)"Stayed Close", (TextColor)NamedTextColor.WHITE)')) {
    throw "LemonOS Stayed Close display title must not be hardcoded in the renderer."
}

$hudConfigMigrationServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendHudConfigMigrationService.java"
$hudConfigMigrationService = Get-Content -Raw -LiteralPath $hudConfigMigrationServicePath
if (-not $hudConfigMigrationService.Contains("private boolean migrateAutoChainDisplay(FileConfiguration config, Board board, String displayPath)")) {
    throw "LemonOS AutoChain HUD display migration must be kept for old generated configs."
}

if (-not $source.Contains("private void recoverRestStatus()") -or
    -not $source.Contains("private void repairCreativeCubeeSlot(Player player)")) {
    throw "LemonOS backend must preserve rest recovery and Creative Cubee slot repair."
}

if ($openCubeeSource.Contains("player.getUniqueId().equals(uuid)")) {
    throw "LemonOS open-cubee routing must use the payload UUID independently of the message carrier."
}

if (-not $placeStatusSource.Contains('"resting".equals(status)') -or
    -not $proxyPlaceStatusSource.Contains('"resting".equals(status)')) {
    throw "LemonOS backend and proxy must treat resting places as wakeable."
}

if (-not $runtimeProbeSource.Contains('resolve(".honeydock").resolve("launchers")') -or
    $runtimeProbeSource.Contains('resolve(place).resolve("start.bat")')) {
    throw "LemonOS proxy wake must use HoneyDock launchers without per-server start.bat dependency."
}

foreach ($required in @('GameRules.SEND_COMMAND_FEEDBACK', 'GameRules.SPAWN_MOBS', 'GameRule.DO_FIRE_TICK',
    'GameRules.PVP', 'GameRules.FALL_DAMAGE', 'setVerifiedGameRule', 'WorldLoadEvent')) {
    if (-not $source.Contains($required)) { throw "LemonOS typed world policy is missing: $required" }
}

Write-Host "LemonOS backend source tests passed."
