param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminPeopleActionService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminPeopleActionService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminPeopleActionService",
    "Predicate<UUID> busy",
    "Player currentTarget(Player actor, UUID targetId)",
    "Bukkit.getPlayer((UUID)targetId)",
    "boolean canTarget(Player actor, Player target)",
    "!target.getUniqueId().equals(actor.getUniqueId())",
    "boolean canStartMessage(Player target)",
    "!this.busy.test(target.getUniqueId())",
    "String messageLore(Player target, String defaultLore)",
    'return this.canStartMessage(target) ? defaultLore : "busy.";'
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminPeopleActionService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminPeopleActionService adminPeopleActionService;",
    "new BackendAdminPeopleActionService(this::isBusy)",
    "this.adminPeopleActionService.canTarget(player, target)",
    "this.adminPeopleActionService.canTarget(player, player8)",
    "this.adminPeopleActionService.currentTarget(player, uUID)",
    "this.adminPeopleActionService.canStartMessage(target)",
    "this.adminPeopleActionService.messageLore(player2, Ui.CarePlayer.MESSAGE.lore())"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminPeopleActionService: $snippet"
    }
}

Write-Host "Backend admin people action contract OK"
