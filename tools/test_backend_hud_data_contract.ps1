param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendHudDataService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\hud.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendHudDataService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendHudDataService",
    "boolean ensureDefaults(FileConfiguration hudData, List<String> boardKeys)",
    "List<Rank> top(FileConfiguration hudData, String boardKey, int limit)",
    "void recordStat(FileConfiguration hudData, String boardKey, Player player, long scoreDelta, String extraKey, long extraDelta)",
    "void recordSandboxAction(FileConfiguration hudData, String boardKey, Player player, long blocksChanged, boolean trackBlocksChanged)",
    "hudData.set(`"version`", (Object)`"1.0`")",
    "hudData.set(`"updated`", (Object)0L)",
    "hudData.createSection(boardKey + `".players`")",
    "ConfigurationSection players = hudData.getConfigurationSection(boardKey + `".players`")",
    "playerKey.length() > 8 ? playerKey.substring(0, 8) : playerKey",
    "Math.max(0L, players.getLong(playerKey + `".score`", 0L))",
    "Long.compare(right.score, left.score)",
    "left.name.compareToIgnoreCase(right.name)",
    "new ArrayList<Rank>(ranks.subList(0, limit))",
    "String path = boardKey + `".players.`" + player.getUniqueId()",
    "hudData.set(path + `".name`", (Object)player.getName())",
    "hudData.set(path + `".score`", (Object)Math.max(0L, hudData.getLong(path + `".score`", 0L) + scoreDelta))",
    "hudData.set(path + `".`" + extraKey, (Object)Math.max(0L, hudData.getLong(path + `".`" + extraKey, 0L) + Math.max(0L, extraDelta)))",
    "hudData.set(path + `".sandbox-actions`", (Object)Math.max(0L, hudData.getLong(path + `".sandbox-actions`", 0L) + 1L))",
    "hudData.set(path + `".blocks-changed`", (Object)Math.max(0L, hudData.getLong(path + `".blocks-changed`", 0L) + blocksChanged))",
    "hudData.set(`"updated`", (Object)System.currentTimeMillis())",
    "static final class Rank",
    "String name()",
    "long score()"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendHudDataService missing required HUD data contract text: $required"
    }
}

foreach ($required in @(
    "private BackendHudDataService hudDataService",
    "this.hudDataService = new BackendHudDataService()",
    "return this.hudDataService.ensureDefaults(this.hudData, this.boardDataKeys())",
    "private List<String> boardDataKeys()",
    "arrayList.add(boardDefinition.dataKey())",
    "for (BackendHudDataService.Rank rank : this.hudDataService.top(this.hudData, string, n))",
    "arrayList.add(new HudRank(rank.name(), rank.score()))",
    "this.hudDataService.recordStat(this.hudData, string, player, l, string3, l2)",
    "this.hudDataService.recordSandboxAction(this.hudData, boardDefinition.dataKey(), player, l, boardDefinition.trackBlocksChanged() && this.boardConfig.trackBlocksChanged(boardDefinition.dataKey()))",
    "this.saveHudData()",
    "this.updateMetricBoards()"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendHudDataService: $required"
    }
}

foreach ($forbidden in @(
    "this.hudData.set(`"version`"",
    "this.hudData.set(`"updated`"",
    "this.hudData.createSection(hudBoardDefinition.dataKey + `".players`")",
    "this.hudData.getConfigurationSection(string + `".players`")",
    "this.hudData.set(string2 + `".name`"",
    "this.hudData.set(string2 + `".score`"",
    "this.hudData.set(string2 + `".sandbox-actions`"",
    "this.hudData.set(string2 + `".blocks-changed`""
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend HUD data persistence detail: $forbidden"
    }
}

foreach ($board in @("made-room", "grew-here", "auto-chain")) {
    if ($template -notmatch "(?ms)^$([regex]::Escape($board)):\s*\r?\n\s{2}players:\s*\{\}\s*$") {
        throw "HUD template no longer preserves players map for $board."
    }
}
if ($template -notmatch "(?m)^version:\s*`"1.0`"\s*$" -or $template -notmatch "(?m)^updated:\s*0\s*$") {
    throw "HUD template no longer preserves version/update defaults."
}

Write-Host "LemonOS backend HUD data contract tests passed."
