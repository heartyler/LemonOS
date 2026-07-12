param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminAccessActionService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminAccessActionService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminAccessActionService",
    "Supplier<List<String>> accessHolderNames",
    "Supplier<List<String>> candidateNames",
    "Function<String, String> normalizeAccessName",
    "Predicate<String> safeAdminName",
    "String normalize(String name)",
    "boolean isHolder(String name)",
    "this.accessHolderNames.get().contains(this.normalize(name))",
    "boolean canGive(String name)",
    "this.safeAdminName.test(normalized)",
    "!this.isHolder(normalized)",
    "this.candidateNames.get().contains(normalized)",
    "boolean isSelfHolder(Player player, String name)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminAccessActionService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminAccessActionService adminAccessActionService;",
    "new BackendAdminAccessActionService(this::accessHolderNames, () -> this.adminAccessNavigationService.candidateNames(), this::normalizeAccessName, this::safeAdminName)",
    "this.adminAccessActionService.isHolder(string)",
    "this.adminAccessActionService.isSelfHolder(player, string)",
    "this.adminAccessActionService.normalize(string)",
    "this.adminAccessActionService.canGive(string2)",
    "this.adminAccessActionService.canGive(accessName)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminAccessActionService: $snippet"
    }
}

Write-Host "Backend admin access action contract OK"
