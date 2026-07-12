param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminAccessNavigationService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminAccessNavigationService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminAccessNavigationService",
    "Supplier<List<String>> accessHolderNames",
    "Supplier<List<String>> networkOnlineNames",
    "Function<String, String> normalizeAccessName",
    "Predicate<String> safeAdminName",
    "List<String> holderNames(Player viewer)",
    "holders.add(0, self)",
    "List<String> candidateNames()",
    "Bukkit.getOnlinePlayers()",
    "this.safeAdminName.test(name)",
    "filter(name -> !holders.contains(this.normalizeAccessName.apply(name)))",
    "int pageIndex(int requestedPage, int totalItems)",
    "boolean hasAnyNextPage(int totalItems)",
    "int nextLoopPage(int currentPage, int totalItems)",
    "List<SlotKey> pageKeys",
    "static final class SlotKey"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminAccessNavigationService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminAccessNavigationService adminAccessNavigationService;",
    "new BackendAdminAccessNavigationService(this::accessHolderNames, this::networkOnlineNames, this::normalizeAccessName, this::safeAdminName, PEOPLE_SLOTS.length)",
    "this.adminAccessNavigationService.holderNames(player)",
    "this.adminAccessNavigationService.candidateNames()",
    "this.adminAccessNavigationService.pageIndex(n, list.size())",
    "this.adminAccessNavigationService.pageKeys(list, cubeeHolder.pageIndex, PEOPLE_SLOTS)",
    "this.adminAccessNavigationService.hasAnyNextPage(list.size())",
    "this.adminAccessNavigationService.nextLoopPage"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminAccessNavigationService: $snippet"
    }
}

if ($Plugin -match "private\s+List<String>\s+keyCandidateNames\s*\(") {
    throw "LemonOSPlugin still owns keyCandidateNames; Admin Access navigation should own candidate listing."
}

if ($Plugin -match "private\s+List<String>\s+accessHolderNames\s*\(\s*Player\s+player\s*\)") {
    throw "LemonOSPlugin still owns player-scoped accessHolderNames; Admin Access navigation should own holder ordering."
}

Write-Host "Backend admin access navigation contract OK"
