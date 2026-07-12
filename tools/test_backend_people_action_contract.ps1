param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPeopleActionService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendPeopleActionService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendPeopleActionService",
    "Predicate<UUID> busy",
    "Predicate<Player> authLocked",
    "Predicate<Player> sociallyBusy",
    "boolean isCurrentOnline(Player target)",
    "return target != null && target.isOnline();",
    "boolean canCreateMeetRequest(Player actor, Player target)",
    "!this.busy.test(actor.getUniqueId()) && !this.sociallyBusy.test(target)",
    "boolean canStartPrivateNote(Player actor, Player target)",
    "!this.authLocked.test(target) && !this.busy.test(actor.getUniqueId()) && !this.sociallyBusy.test(target)",
    "boolean canDeliverPrivateNote(Player target)",
    "!this.authLocked.test(target) && !this.sociallyBusy.test(target)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendPeopleActionService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendPeopleActionService peopleActionService;",
    "new BackendPeopleActionService(this::isBusy, this::isAuthLocked, this::isSociallyBusy)",
    "this.peopleActionService.canStartPrivateNote(player, player2)",
    "this.peopleActionService.canDeliverPrivateNote(player2)",
    "this.peopleActionService.canCreateMeetRequest(player, target)",
    "this.peopleActionService.isCurrentOnline(player2)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendPeopleActionService: $snippet"
    }
}

$meetRequestBody = [regex]::Match($Service, "boolean canCreateMeetRequest\(Player actor, Player target\) \{(?<body>[\s\S]*?)\n    \}").Groups["body"].Value
if ($meetRequestBody.Contains("authLocked.test")) {
    throw "Meet request guard must not add target auth-lock checks; original visit/invite behavior did not include that guard."
}

Write-Host "Backend people action contract OK"
