param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$repositoryPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaytimeRepository.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"
$runtimeStatePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyRuntimeStateService.java"

if (-not (Test-Path -LiteralPath $repositoryPath -PathType Leaf)) {
    throw "Missing proxy PlaytimeRepository."
}

$repository = Get-Content -Raw -LiteralPath $repositoryPath
$proxy = Get-Content -Raw -LiteralPath $proxyPath
$runtimeState = Get-Content -Raw -LiteralPath $runtimeStatePath

foreach ($required in @(
    "final class PlaytimeRepository",
    "static String defaultFile()",
    "synchronized void load()",
    "synchronized void sync(List<PlayerSnapshot> onlinePlayers)",
    "synchronized void save() throws IOException",
    "synchronized void clear() throws IOException",
    "synchronized String content()",
    "writeStringAtomic",
    'version: \"1.0\"',
    "players:",
    "total-seconds",
    "last-seen"
)) {
    if (-not $repository.Contains($required)) {
        throw "PlaytimeRepository missing required storage contract text: $required"
    }
}

foreach ($forbidden in @(
    "private final Map<UUID, PlaytimeEntry> playtime",
    "private final Map<UUID, Long> activePlaytimeSinceMillis",
    "private static final class PlaytimeEntry"
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns playtime storage state: $forbidden"
    }
}

if ($repository.Contains("com.velocitypowered.api.proxy.Player")) {
    throw "PlaytimeRepository must not depend directly on Velocity Player objects."
}

if ($proxy -notmatch "new PlaytimeRepository\(this\.playtimeFile, this\.logger\)" -or
    $proxy -notmatch "new ProxyRuntimeStateService\(this\.server, this\.logger, this\.onlineFile, this\.playtimeFile, this\.placesFile, this\.onlinePlayersRepository, this\.playtimeRepository" -or
    $runtimeState -notmatch "this\.playtimeRepository\.load\(\)" -or
    $runtimeState -notmatch "this\.playtimeRepository\.sync\(list\)" -or
    $runtimeState -notmatch "this\.playtimeRepository\.save\(\)" -or
    $runtimeState -notmatch "this\.playtimeRepository\.clear\(\)") {
    throw "LemonOSProxyPlugin is not wired through PlaytimeRepository."
}

Write-Host "LemonOS playtime storage contract tests passed."
