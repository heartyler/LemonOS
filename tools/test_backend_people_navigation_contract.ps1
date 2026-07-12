param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPeopleNavigationService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendPeopleNavigationService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendPeopleNavigationService",
    "Map<UUID, Integer> pageIndexes",
    "Predicate<Player> sociallyBusy",
    "Bukkit.getOnlinePlayers()",
    "candidate.getUniqueId().equals(viewer.getUniqueId())",
    "Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER)",
    "int pageIndex(int requestedPage, int totalItems)",
    "boolean hasAnyNextPage(int totalItems)",
    "int nextLoopPage(int currentPage, int totalItems)",
    "List<SlotTarget> pageTargets",
    'return this.sociallyBusy.test(player) ? "busy." : "meet up.";',
    "boolean canOpenPlayer(Player player)",
    "static final class SlotTarget"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendPeopleNavigationService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendPeopleNavigationService peopleNavigationService;",
    "new BackendPeopleNavigationService(this.peoplePageIndexes, this::isSociallyBusy, PEOPLE_SLOTS.length)",
    "this.peopleNavigationService.currentPageIndex(player)",
    "this.peopleNavigationService.rememberPageIndex(player, n)",
    "this.peopleNavigationService.listPeople(player)",
    "this.peopleNavigationService.pageIndex(n, list.size())",
    "this.peopleNavigationService.pageTargets(list, cubeeHolder.pageIndex, PEOPLE_SLOTS)",
    "this.peopleNavigationService.itemStatus(player2)",
    "this.peopleNavigationService.hasAnyNextPage(list.size())",
    "this.peopleNavigationService.nextLoopPage",
    "this.peopleNavigationService.canOpenPlayer"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendPeopleNavigationService: $snippet"
    }
}

if ($Plugin -match "private\s+List<Player>\s+peopleList\s*\(") {
    throw "LemonOSPlugin still owns peopleList; People navigation should be grouped in BackendPeopleNavigationService."
}

Write-Host "Backend people navigation contract OK"
