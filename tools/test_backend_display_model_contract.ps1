param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$configPath = Join-Path $Root "src\main\java\dev\lemonos\BackendDisplayConfig.java"
$modelPath = Join-Path $Root "src\main\java\dev\lemonos\BackendDisplayModel.java"
$textPath = Join-Path $Root "src\main\java\dev\lemonos\BackendDisplayText.java"
$hudPath = Join-Path $Root "src\main\java\dev\lemonos\BackendHudDisplayService.java"
$stayedPath = Join-Path $Root "src\main\java\dev\lemonos\BackendStayedCloseDisplayService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

foreach ($path in @($configPath, $modelPath, $textPath)) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Missing backend display primitive: $path"
    }
}

$config = Get-Content -Raw -LiteralPath $configPath
$model = Get-Content -Raw -LiteralPath $modelPath
$text = Get-Content -Raw -LiteralPath $textPath
$hud = Get-Content -Raw -LiteralPath $hudPath
$stayed = Get-Content -Raw -LiteralPath $stayedPath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "interface BackendDisplayConfig",
    "boolean booleanValue(String path, boolean fallback)",
    "int intValue(String path, int fallback, int minimum, int maximum)",
    "double doubleValue(String path, double fallback, double minimum, double maximum)",
    "double doubleValue(String primaryPath, String legacyPath, double fallback, double minimum, double maximum)",
    "String stringValue(String path, String fallback)"
)) {
    if (-not $config.Contains($required)) {
        throw "BackendDisplayConfig missing required contract text: $required"
    }
}

foreach ($required in @(
    "final class BackendDisplayModel",
    "BackendDisplayModel(boolean bedrockEnabled, List<Entry> entries)",
    "boolean bedrockEnabled()",
    "List<Entry> entries()",
    "static final class Entry",
    "String role()",
    "double offsetX()",
    "double offsetY()",
    "double offsetZ()",
    "String text()",
    "ColorRole colorRole()",
    "Alignment alignment()",
    "enum ColorRole",
    "WHITE",
    "GRAY",
    "enum Alignment",
    "LEFT",
    "CENTER",
    "RIGHT"
)) {
    if (-not $model.Contains($required)) {
        throw "BackendDisplayModel missing required contract text: $required"
    }
}

foreach ($required in @(
    "final class BackendDisplayText",
    "private BackendDisplayText()",
    "static String fitName(String string, int limit)",
    'string == null || string.isBlank() ? "" : string.trim()',
    'return value.substring(0, limit - 1) + "."'
)) {
    if (-not $text.Contains($required)) {
        throw "BackendDisplayText missing required contract text: $required"
    }
}

foreach ($service in @($hud, $stayed)) {
    foreach ($forbidden in @(
        "interface Config",
        "static final class DisplayModel",
        "static final class Entry",
        "enum ColorRole",
        "enum Alignment",
        "String fitName(String string, int limit)"
    )) {
        if ($service.Contains($forbidden)) {
            throw "Display service still duplicates common display primitive: $forbidden"
        }
    }
}

foreach ($required in @(
    "private BackendDisplayConfig backendDisplayConfig()",
    "return this.hudConfig;",
    "private Component backendDisplayComponent(BackendDisplayModel.Entry entry)",
    "entry.colorRole() == BackendDisplayModel.ColorRole.WHITE ? HoneyPalette.DEFAULT_WHITE : NamedTextColor.GRAY",
    "private TextDisplay.TextAlignment backendDisplayAlignment(BackendDisplayModel.Alignment alignment)",
    "alignment == BackendDisplayModel.Alignment.LEFT",
    "alignment == BackendDisplayModel.Alignment.RIGHT"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through common backend display primitives: $required"
    }
}

Write-Host "LemonOS backend display model contract tests passed."
