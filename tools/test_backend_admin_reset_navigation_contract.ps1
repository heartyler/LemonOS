param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminResetNavigationService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminResetNavigationService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminResetNavigationService",
    "Supplier<List<String>> resetRequestTokens",
    "Function<String, String> resetRequestName",
    "List<String> tokens()",
    "String displayName(String token)",
    'return count == 0 ? null : count + " waiting.";',
    'return displayName + " wants to reset.";',
    "int pageIndex(int requestedPage, int totalItems)",
    "boolean hasAnyNextPage(int totalItems)",
    "int nextLoopPage(int currentPage, int totalItems)",
    "List<SlotToken> pageTokens",
    "static final class SlotToken"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminResetNavigationService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminResetNavigationService adminResetNavigationService;",
    "new BackendAdminResetNavigationService(this::resetRequestTokens, this::resetRequestName, PEOPLE_SLOTS.length)",
    "this.adminResetNavigationService.tokens()",
    "this.adminResetNavigationService.nextLoopPage",
    "this.adminResetNavigationService.pageIndex(n, list.size())",
    "this.adminResetNavigationService.pageTokens(list, cubeeHolder.pageIndex, PEOPLE_SLOTS)",
    "this.adminResetNavigationService.displayName(slotToken.token)",
    "this.adminResetNavigationService.requestLore(string2)",
    "this.adminResetNavigationService.countLore()",
    "this.adminResetNavigationService.hasAnyNextPage(list.size())"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminResetNavigationService: $snippet"
    }
}

Write-Host "Backend admin reset navigation contract OK"
