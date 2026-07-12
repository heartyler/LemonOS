param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyAccessCommandService.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing proxy ProxyAccessCommandService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$proxy = Get-Content -Raw -LiteralPath $proxyPath

foreach ($required in @(
    "final class ProxyAccessCommandService",
    "BrigadierCommand lemonosAccessCommand()",
    "requires(commandSource -> !(commandSource instanceof Player))",
    "`"add`").then",
    "`"admin`").then",
    "`"remove`").then",
    "`"default`").then",
    "private CompletableFuture<Suggestions> suggestOnlineNames(SuggestionsBuilder suggestionsBuilder)",
    "private CompletableFuture<Suggestions> suggestAdminNames(SuggestionsBuilder suggestionsBuilder)",
    "private void handleConsoleLemonOSCommand(CommandSource commandSource, String command)",
    "private void saveAccessFromCommand(CommandSource commandSource, String action, String name, boolean admin)",
    "private void sendLemonOSHelp(CommandSource commandSource)",
    "AccessRepository.normalizeAccessName",
    "this.accessRepository.adminNameAccess()",
    "this.accessRepository.updateNameAccess(name, admin)",
    "LemonOS access admins: none",
    "LemonOS access admins: ",
    "LemonOS access: invalid name.",
    "this.saveAccessFromCommand(commandSource, `"added`", name, true)",
    "this.saveAccessFromCommand(commandSource, `"removed`", name, false)",
    "LemonOS access `" + action + `": `" + name",
    "LemonOS access: unable to save.",
    "LemonOS access {} by console for {}.",
    "Unable to save LemonOS access file: {}",
    "Usage: lemonos access add <name> | lemonos access remove <name> | lemonos access list"
)) {
    if (-not $service.Contains($required)) {
        throw "ProxyAccessCommandService missing required access command contract text: $required"
    }
}

foreach ($forbidden in @(
    "private BrigadierCommand lemonosAccessCommand(",
    "private CompletableFuture<Suggestions> suggestOnlineNames(",
    "private CompletableFuture<Suggestions> suggestAdminNames(",
    "private void handleConsoleLemonOSCommand(",
    "private void saveAccessFromCommand(",
    "private void sendLemonOSHelp(",
    "LemonOS access admins: none",
    "LemonOS access added: ",
    "Usage: lemonos access add <name>"
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns access command detail: $forbidden"
    }
}

if ($proxy -notmatch "new ProxyAccessCommandService\(this\.server, this\.logger, this\.accessRepository\)" -or
    $proxy -notmatch "this\.accessCommandService\.lemonosAccessCommand\(\)") {
    throw "LemonOSProxyPlugin is not wired through ProxyAccessCommandService."
}

Write-Host "LemonOS access command service contract tests passed."
