param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendHudDisplayService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\LemonOS\boards.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendHudDisplayService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendHudDisplayService",
    "BackendDisplayModel model(BackendDisplayConfig config, Board board, List<Rank> ranks)",
    "ArrayList<BackendDisplayModel.Entry> entries = new ArrayList<BackendDisplayModel.Entry>()",
    "String configPath = board.configPath()",
    "String rolePrefix = board.rolePrefix()",
    "config.intValue(configPath + `".top`", 5, 1, 10)",
    "config.doubleValue(configPath + `".display.subtitle-offset-y`", -0.10, -4.0, 4.0)",
    "config.doubleValue(configPath + `".display.row-start-offset-y`", -0.34, -4.0, 4.0)",
    "config.doubleValue(configPath + `".display.row-gap`", -0.13, -2.0, 2.0)",
    "config.doubleValue(configPath + `".display.name-offset-z`", -0.32, -8.0, 8.0)",
    "config.doubleValue(configPath + `".display.value-offset-z`", configPath + `".display.score-offset-z`", 0.48, -8.0, 8.0)",
    "config.booleanValue(configPath + `".display.bedrock.enabled`", false)",
    "config.stringValue(configPath + `".title`", board.defaultTitle())",
    "config.stringValue(configPath + `".subtitle`", board.defaultSubtitle())",
    "config.stringValue(configPath + `".bottom-line`", board.defaultBottomLine())",
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
    "static final class Board",
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
    "BackendHudDisplayService.Board board = new BackendHudDisplayService.Board(string5, string2, boardDefinition.defaultTitle(), boardDefinition.defaultSubtitle(), boardDefinition.defaultBottomLine())",
    "BackendDisplayModel displayModel = this.hudDisplayService.model(this.backendDisplayConfig(), board, arrayList)",
    "for (BackendDisplayModel.Entry entry : displayModel.entries())",
    "this.updateBoardDisplayRole(world, this.stayedCloseLocation(location, entry.offsetX(), entry.offsetY(), entry.offsetZ()), entry.role(), string5, this.backendDisplayComponent(entry), this.backendDisplayAlignment(entry.alignment()))",
    "private BackendDisplayConfig backendDisplayConfig()",
    "return this.boardConfig;",
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

foreach ($board in @("made-room", "grew-here", "auto-chain")) {
    if ($template -notmatch "(?ms)^\s{2}$([regex]::Escape($board)):\s*\r?\n(?:.*?\r?\n)*?\s{4}display:\s*$") {
        throw "HUD template missing display section for $board."
    }
}

Write-Host "LemonOS backend HUD display contract tests passed."
