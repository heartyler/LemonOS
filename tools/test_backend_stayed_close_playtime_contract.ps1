param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendStayedClosePlaytimeService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\playtime.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendStayedClosePlaytimeService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendStayedClosePlaytimeService",
    "List<Rank> top(File playtimeFile, int limit)",
    "if (!playtimeFile.isFile())",
    "YamlConfiguration.loadConfiguration((File)playtimeFile)",
    "ConfigurationSection players = playtime.getConfigurationSection(`"players`")",
    "playerKey.length() > 8 ? playerKey.substring(0, 8) : playerKey",
    "Math.max(0L, players.getLong(playerKey + `".total-seconds`", 0L))",
    "Long.compare(right.totalSeconds, left.totalSeconds)",
    "left.name.compareToIgnoreCase(right.name)",
    "new ArrayList<Rank>(ranks.subList(0, limit))",
    "static final class Rank",
    "String name()",
    "long totalSeconds()"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendStayedClosePlaytimeService missing required playtime read contract text: $required"
    }
}

foreach ($required in @(
    "private BackendStayedClosePlaytimeService stayedClosePlaytimeService",
    "this.stayedClosePlaytimeService = new BackendStayedClosePlaytimeService()",
    "this.runtimeLayout.dataFile(`"playtime.yml`")",
    "this.hudConfig.top(BackendHudConfig.STAYED_CLOSE)",
    "for (BackendStayedClosePlaytimeService.Rank rank : this.stayedClosePlaytimeService.top(file, n))",
    "arrayList.add(new StayedCloseRank(rank.name(), rank.totalSeconds()))"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendStayedClosePlaytimeService: $required"
    }
}

foreach ($forbidden in @(
    "YamlConfiguration.loadConfiguration((File)file)",
    "fileConfiguration.getConfigurationSection(`"players`")",
    "configurationSection.getKeys(false)",
    "configurationSection.getString(string + `".name`"",
    "configurationSection.getLong(string + `".total-seconds`"",
    "arrayList.sort((stayedCloseRank"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns Stayed Close playtime read detail: $forbidden"
    }
}

if ($template -notmatch "(?m)^version:\s*`"1.0`"\s*$" -or
    $template -notmatch "(?m)^updated:\s*0\s*$" -or
    $template -notmatch "(?m)^players:\s*\{\}\s*$") {
    throw "Playtime template no longer preserves empty Stayed Close runtime schema."
}

Write-Host "LemonOS backend Stayed Close playtime contract tests passed."
