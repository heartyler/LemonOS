param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$repositoryPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\OnlinePlayersRepository.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"
$runtimeStatePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyRuntimeStateService.java"

if (-not (Test-Path -LiteralPath $repositoryPath -PathType Leaf)) {
    throw "Missing proxy OnlinePlayersRepository."
}

$repository = Get-Content -Raw -LiteralPath $repositoryPath
$proxy = Get-Content -Raw -LiteralPath $proxyPath
$runtimeState = Get-Content -Raw -LiteralPath $runtimeStatePath

foreach ($required in @(
    "final class OnlinePlayersRepository",
    "void write(List<PlayerSnapshot> onlinePlayers)",
    "updated: ",
    "- name: ",
    "\\.?[a-z0-9_]{1,32}",
    "Comparator.comparing(PlayerSnapshot::name, String.CASE_INSENSITIVE_ORDER)",
    "Files.writeString(this.onlineFile"
)) {
    if (-not $repository.Contains($required)) {
        throw "OnlinePlayersRepository missing required storage contract text: $required"
    }
}

foreach ($forbidden in @(
    "Files.writeString(this.onlineFile",
    "Comparator.comparing(Player::getUsername"
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns online storage write detail: $forbidden"
    }
}

if ($repository.Contains("com.velocitypowered.api.proxy.Player")) {
    throw "OnlinePlayersRepository must not depend directly on Velocity Player objects."
}

if ($proxy -notmatch "new OnlinePlayersRepository\(this\.onlineFile, this\.logger\)" -or
    $proxy -notmatch "new ProxyRuntimeStateService\(this\.server, this\.logger, this\.onlineFile, this\.playtimeFile, this\.placesFile, this\.onlinePlayersRepository" -or
    $runtimeState -notmatch "this\.onlinePlayersRepository\.write\(list\)") {
    throw "LemonOSProxyPlugin is not wired through OnlinePlayersRepository."
}

Write-Host "LemonOS online storage contract tests passed."
