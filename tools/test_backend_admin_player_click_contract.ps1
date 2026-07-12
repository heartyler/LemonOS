param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminPlayerClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminPlayerClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminPlayerClickService",
    "AdminPlayerAction action(int clickedSlot, int backSlot, int controlSlot, int visitSlot, int inviteSlot, int messageSlot)",
    "return AdminPlayerAction.BACK;",
    "return AdminPlayerAction.CONTROL;",
    "return AdminPlayerAction.VISIT;",
    "return AdminPlayerAction.INVITE;",
    "return AdminPlayerAction.MESSAGE;",
    "return AdminPlayerAction.NONE;",
    "enum AdminPlayerAction",
    "CONTROL,",
    "VISIT,",
    "INVITE,",
    "MESSAGE;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminPlayerClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminPlayerClickService adminPlayerClickService;",
    "new BackendAdminPlayerClickService()",
    "this.handleAdminPlayerClick(player, player6, n23, n);",
    "private void handleAdminPlayerClick(Player player, Player target, int pageIndex, int slot)",
    "this.adminPlayerClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "4,",
    "12,",
    "13,",
    "14);",
    "case BACK -> this.openNextTick(() -> this.openAdminPeople(player, pageIndex));",
    "this.adminPeopleActionService.canTarget(player, target)",
    "this.openNextTick(() -> this.openAdminPlayerControl(player, target, pageIndex));",
    "this.adminVisit(player, target);",
    "this.adminInvite(player, target);",
    "this.adminPeopleActionService.canStartMessage(target)",
    "this.startPrivateNote(player, target);",
    "private void addBedrockAdminPlayerActionButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, Ui.ButtonSpec buttonSpec)",
    "private void addBedrockAdminPlayerActionButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, int slot, String title, String lore)",
    "BackendAdminPlayerClickService.AdminPlayerAction action = this.adminPlayerClickService.action(",
    "this.addBedrockAdminPlayerActionButton(builder, arrayList, player, player2, Ui.Shared.FORM_BACK);",
    "Ui.Shared.FORM_BACK.slot()",
    "case BACK -> this.bedrockButton((Object)builder, actions, title, lore, () -> this.openLastAdminPeople(player));",
    "case CONTROL -> this.bedrockButton((Object)builder, actions, title, lore, () -> this.openAdminPlayerControlIfCurrent(player, target.getUniqueId(), 0));",
    "case VISIT -> this.bedrockButton((Object)builder, actions, title, lore, () -> this.adminVisitIfCurrent(player, target.getUniqueId()));",
    "case INVITE -> this.bedrockButton((Object)builder, actions, title, lore, () -> this.adminInviteIfCurrent(player, target.getUniqueId()));",
    "case MESSAGE ->",
    "this.bedrockButton((Object)builder, actions, title, `"busy.`", () -> {});",
    "this.bedrockButton((Object)builder, actions, title, lore, () -> this.startPrivateNoteIfCurrent(player, target.getUniqueId()));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminPlayerClickService: $snippet"
    }
}

Write-Host "Backend admin player click contract OK"
