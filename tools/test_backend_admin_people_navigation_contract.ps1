param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminPeopleNavigationService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminPeopleNavigationService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminPeopleNavigationService",
    "Map<UUID, Integer> pageIndexes",
    "Bukkit.getPlayer((UUID)viewer.getUniqueId())",
    "Bukkit.getOnlinePlayers()",
    "Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER)",
    "people.add(0, self)",
    "int pageIndex(int requestedPage, int totalItems)",
    "boolean hasAnyNextPage(int totalItems)",
    "int nextLoopPage(int currentPage, int totalItems)",
    "List<SlotTarget> pageTargets",
    'return this.isSelf(viewer, target) ? "you." : null;',
    'return this.isSelf(viewer, target) ? "for you." : null;',
    "static final class SlotTarget"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminPeopleNavigationService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminPeopleNavigationService adminPeopleNavigationService;",
    "new BackendAdminPeopleNavigationService(this.adminPeoplePageIndexes, PEOPLE_SLOTS.length)",
    "this.adminPeopleNavigationService.currentPageIndex(player)",
    "this.adminPeopleNavigationService.rememberPageIndex(player, n)",
    "this.adminPeopleNavigationService.listPeople(player)",
    "this.adminPeopleNavigationService.pageIndex(n, list.size())",
    "this.adminPeopleNavigationService.pageTargets(list, cubeeHolder.pageIndex, PEOPLE_SLOTS)",
    "this.adminPeopleNavigationService.inventoryStatus(player, player2)",
    "this.adminPeopleNavigationService.bedrockStatus(player, player2)",
    "this.adminPeopleNavigationService.hasAnyNextPage(list.size())",
    "this.adminPeopleNavigationService.nextLoopPage"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminPeopleNavigationService: $snippet"
    }
}

if ($Plugin -match "private\s+List<Player>\s+adminPeopleList\s*\(") {
    throw "LemonOSPlugin still owns adminPeopleList; Admin People navigation should be grouped in BackendAdminPeopleNavigationService."
}

Write-Host "Backend admin people navigation contract OK"
