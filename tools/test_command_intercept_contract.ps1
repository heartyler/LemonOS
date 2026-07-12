param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyCommandInterceptService.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing proxy ProxyCommandInterceptService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$proxy = Get-Content -Raw -LiteralPath $proxyPath

foreach ($required in @(
    "final class ProxyCommandInterceptService",
    "void handle(CommandExecuteEvent commandExecuteEvent)",
    "command.startsWith(`"/`") ? command.substring(1) : command",
    "commandWithoutSlash.split(`"\\s+`", 2)",
    "`"cubee`".equalsIgnoreCase(commandName)",
    "`"help`".equalsIgnoreCase(commandName)",
    "CommandExecuteEvent.CommandResult.denied()",
    "AdminProtocol.OPEN_CUBEE",
    "dataOutputStream.writeUTF(command)",
    "dataOutputStream.writeUTF(player.getUniqueId().toString())",
    "dataOutputStream.writeBoolean(recovery)",
    "Unable to send LemonOS open request."
)) {
    if (-not $service.Contains($required)) {
        throw "ProxyCommandInterceptService missing required command intercept contract text: $required"
    }
}

foreach ($forbidden in @(
    "private void sendOpenCubee(",
    "private void sendOpenLemonOSCommand(",
    "AdminProtocol.OPEN_CUBEE",
    "Unable to send LemonOS open request.",
    "`"pad`".equalsIgnoreCase",
    "`"help`".equalsIgnoreCase"
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns command intercept detail: $forbidden"
    }
}

if ($service.Contains("OPEN_HOMEPAD") -or
    $service.Contains("OPEN_HONEYPAD") -or
    $service.Contains("OPEN_PAD") -or
    $service.Contains('"open-homepad"') -or
    $service.Contains('"open-honeypad"') -or
    $service.Contains('"open-pad"')) {
    throw "ProxyCommandInterceptService still carries a legacy Cubee protocol alias."
}

if ($proxy -notmatch "new ProxyCommandInterceptService\(this\.logger, this\.adminChannel\)" -or
    $proxy -notmatch "this\.commandInterceptService\.handle\(commandExecuteEvent\)") {
    throw "LemonOSProxyPlugin is not wired through ProxyCommandInterceptService."
}

Write-Host "LemonOS command intercept contract tests passed."
