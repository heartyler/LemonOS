param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendStayedCloseDisplayService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\LemonOS\boards.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendStayedCloseDisplayService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendStayedCloseDisplayService",
    "BackendDisplayModel model(BackendDisplayConfig config, List<Rank> ranks)",
    "ArrayList<BackendDisplayModel.Entry> entries = new ArrayList<BackendDisplayModel.Entry>()",
    "config.intValue(`"stayed-close.top`", 5, 1, 10)",
    "config.doubleValue(`"stayed-close.display.subtitle-offset-y`", -0.10, -4.0, 4.0)",
    "config.doubleValue(`"stayed-close.display.row-start-offset-y`", -0.34, -4.0, 4.0)",
    "config.doubleValue(`"stayed-close.display.row-gap`", -0.13, -2.0, 2.0)",
    "config.doubleValue(`"stayed-close.display.name-offset-z`", -0.32, -8.0, 8.0)",
    "config.doubleValue(`"stayed-close.display.value-offset-z`", `"stayed-close.display.time-offset-z`", 0.48, -8.0, 8.0)",
    "config.booleanValue(`"stayed-close.display.bedrock.enabled`", true)",
    "config.stringValue(`"stayed-close.title`", `"Stayclose`").trim()",
    "config.stringValue(`"stayed-close.subtitle`", `"where small steps stay.`").trim()",
    "config.stringValue(`"stayed-close.bottom-line`", `"time spent here.`").trim()",
    "`"stayed_close_title`"",
    "`"stayed_close_subtitle`"",
    "`"stayed_close_bottom`"",
    "`"stayed_close_name_`" + (i + 1)",
    "`"stayed_close_time_`" + (i + 1)",
    "`"stayed_close_bedrock_title`"",
    "`"stayed_close_bedrock_subtitle`"",
    "`"stayed_close_bedrock_bottom`"",
    "`"stayed_close_bedrock_row_`" + (i + 1)",
    "BackendDisplayText.fitName(rank.name(), config.intValue(`"stayed-close.name-width`", 12, 4, 16))",
    "formatTime(rank.totalSeconds())",
    "BackendDisplayText.fitName(rank.name(), bedrockNameWidth) + `" `" + formatTime(rank.totalSeconds())",
    "String formatTime(long totalSeconds)",
    "new BackendDisplayModel(bedrockEnabled, entries)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendStayedCloseDisplayService missing required display contract text: $required"
    }
}

foreach ($required in @(
    "private BackendStayedCloseDisplayService stayedCloseDisplayService",
    "this.stayedCloseDisplayService = new BackendStayedCloseDisplayService()",
    "BackendDisplayModel displayModel = this.stayedCloseDisplayService.model(this.backendDisplayConfig(), arrayList)",
    "if (!displayModel.bedrockEnabled())",
    "this.clearStayedCloseBedrockDisplays()",
    "for (BackendDisplayModel.Entry entry : displayModel.entries())",
    "this.updateStayedCloseDisplayRole(world, this.stayedCloseLocation(location, entry.offsetX(), entry.offsetY(), entry.offsetZ()), entry.role(), this.backendDisplayComponent(entry), this.backendDisplayAlignment(entry.alignment()))",
    "private BackendDisplayConfig backendDisplayConfig()",
    "return this.boardConfig;",
    "private Component backendDisplayComponent(BackendDisplayModel.Entry entry)",
    "private TextDisplay.TextAlignment backendDisplayAlignment(BackendDisplayModel.Alignment alignment)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendStayedCloseDisplayService: $required"
    }
}

foreach ($forbidden in @(
    "String string = this.configString(`"stayed-close.title`", `"Stayclose`").trim()",
    "Component component2 = Component.text((String)(stayedCloseRank == null ? `"`" : this.formatStayedCloseTime(stayedCloseRank.totalSeconds))",
    "Component component3 = Component.text((String)(stayedCloseRank == null ? `"`" : this.fitStayedCloseName(stayedCloseRank.name, n2) + `" `" + this.formatStayedCloseTime(stayedCloseRank.totalSeconds))",
    "this.updateStayedCloseDisplayRole(world, this.stayedCloseLocation(location, d22, d23, d24), STAYED_CLOSE_TITLE_DISPLAY"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns Stayed Close display model detail: $forbidden"
    }
}

foreach ($requiredTemplate in @(
    '(?m)^\s*title:\s*["'']?Stayclose["'']?\s*$',
    '(?m)^\s*subtitle:\s*["'']?where small steps stay\.["'']?\s*$',
    '(?m)^\s*bottom-line:\s*["'']?time spent here\.["'']?\s*$',
    '(?m)^\s*value-offset-z:\s*0\.46\s*$',
    '(?m)^\s*bedrock:\s*$',
    '(?m)^\s*enabled:\s*false\s*$'
)) {
    if ($template -notmatch $requiredTemplate) {
        throw "Stayed Close template missing display contract text: $requiredTemplate"
    }
}

Write-Host "LemonOS backend Stayed Close display contract tests passed."
