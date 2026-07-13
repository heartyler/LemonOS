param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendHudDisplayService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\LemonOS\hud.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendHudDisplayService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendHudDisplayService",
    "BackendDisplayModel model(BackendDisplayConfig config, Hud hud, List<Rank> ranks)",
    "ArrayList<BackendDisplayModel.Entry> entries = new ArrayList<BackendDisplayModel.Entry>()",
    "String configPath = hud.configPath()",
    "String rolePrefix = hud.rolePrefix()",
    "config.intValue(configPath + `".top`", 5, 1, 10)",
    "config.doubleValue(configPath + `".display.subtitle-offset-y`", -0.10, -4.0, 4.0)",
    "config.doubleValue(configPath + `".display.row-start-offset-y`", -0.34, -4.0, 4.0)",
    "config.doubleValue(configPath + `".display.row-gap`", -0.13, -2.0, 2.0)",
    "config.doubleValue(configPath + `".display.name-offset-z`", -0.32, -8.0, 8.0)",
    "config.doubleValue(configPath + `".display.value-offset-z`", configPath + `".display.score-offset-z`", 0.48, -8.0, 8.0)",
    "config.booleanValue(configPath + `".display.bedrock.enabled`", false)",
    "config.stringValue(configPath + `".title`", hud.defaultTitle())",
    "config.stringValue(configPath + `".subtitle`", hud.defaultSubtitle())",
    "config.stringValue(configPath + `".bottom-line`", hud.defaultBottomLine())",
    "rolePrefix + `"title`"",
    "rolePrefix + `"subtitle`"",
    "rolePrefix + `"bottom`"",
    "rolePrefix + `"name_`" + (i + 1)",
    "rolePrefix + `"score_`" + (i + 1)",
    "rolePrefix + `"bedrock_row_`" + (i + 1)",
    "rolePrefix + `"bedrock_bottom`"",
    "BackendDisplayText.fitName(rank.name(), config.intValue(configPath + `".name-width`", 12, 4, 16))",
    "Long.toString(rank.score())",
    "BackendDisplayText.fitName(rank.name(), bedrockNameWidth) + `" `" + rank.score()",
    "if (i == top - 1)",
    "static final class Hud",
    "static final class Rank",
    "new BackendDisplayModel(bedrockEnabled, entries)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendHudDisplayService missing required HUD display contract text: $required"
    }
}

foreach ($required in @(
    "private BackendHudDisplayService hudDisplayService",
    "this.hudDisplayService = new BackendHudDisplayService()",
    "ArrayList<BackendHudDisplayService.Rank> arrayList = new ArrayList<BackendHudDisplayService.Rank>()",
    "arrayList.add(new BackendHudDisplayService.Rank(hudRank.name, hudRank.score))",
    "BackendHudDisplayService.Hud hud = new BackendHudDisplayService.Hud(string5, string2, hudDefinition.defaultTitle(), hudDefinition.defaultSubtitle(), hudDefinition.defaultBottomLine())",
    "BackendDisplayModel displayModel = this.hudDisplayService.model(this.backendDisplayConfig(), hud, arrayList)",
    "for (BackendDisplayModel.Entry entry : displayModel.entries())",
    "this.updateBoardDisplayRole(world, this.stayedCloseLocation(location, entry.offsetX(), entry.offsetY(), entry.offsetZ()), entry.role(), string5, this.backendDisplayComponent(entry), this.backendDisplayAlignment(entry.alignment()))",
    "private BackendDisplayConfig backendDisplayConfig()",
    "return this.hudConfig;",
    "private Component backendDisplayComponent(BackendDisplayModel.Entry entry)",
    "private TextDisplay.TextAlignment backendDisplayAlignment(BackendDisplayModel.Alignment alignment)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendHudDisplayService: $required"
    }
}

foreach ($forbidden in @(
    "this.updateHudDisplayRole(world, this.stayedCloseLocation(location, d22, d23, d24), string2 + `"title`"",
    "Component component = Component.text((String)(hudRank == null ? `"`" : this.fitStayedCloseName(hudRank.name",
    "Component component2 = Component.text((String)(hudRank == null ? `"`" : Long.toString(hudRank.score))",
    "Component component3 = Component.text((String)(hudRank == null ? `"`" : this.fitStayedCloseName(hudRank.name, n2) + `" `" + hudRank.score)"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns HUD display model detail: $forbidden"
    }
}

foreach ($hudKey in @("stayed-close", "made-room", "grew-here", "auto-chain")) {
    if ($template -notmatch "(?ms)^\s{2}$([regex]::Escape($hudKey)):\s*\r?\n(?:.*?\r?\n)*?\s{4}display:\s*$") {
        throw "HUD template missing display section for $hudKey."
    }
}

Write-Host "LemonOS backend HUD display contract tests passed."
