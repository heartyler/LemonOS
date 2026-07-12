param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPeopleClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendPeopleClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendPeopleClickService",
    "PeoplePageClick peoplePageAction(int clickedSlot, int backSlot, int nextSlot, Map<Integer, UUID> slotTargets)",
    "return new PeoplePageClick(PeoplePageAction.BACK, null);",
    "return new PeoplePageClick(PeoplePageAction.NEXT_PAGE, null);",
    "UUID target = slotTargets == null ? null : slotTargets.get(clickedSlot);",
    "return new PeoplePageClick(PeoplePageAction.OPEN_PLAYER, target);",
    "return new PeoplePageClick(PeoplePageAction.NONE, null);",
    "PlayerPageAction playerPageAction(int clickedSlot, int backSlot, int messageSlot, int visitSlot, int inviteSlot)",
    "return PlayerPageAction.BACK;",
    "return PlayerPageAction.MESSAGE;",
    "return PlayerPageAction.VISIT;",
    "return PlayerPageAction.INVITE;",
    "return PlayerPageAction.NONE;",
    "record PeoplePageClick(PeoplePageAction action, UUID target)",
    "enum PeoplePageAction",
    "enum PlayerPageAction"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendPeopleClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendPeopleClickService peopleClickService;",
    "new BackendPeopleClickService()",
    "this.handlePeoplePageClick(player, n);",
    "this.handlePlayerPageClick(player, n);",
    "private void handlePeoplePageClick(Player player, int slot)",
    "this.peopleClickService.peoplePageAction(",
    "Ui.Shared.NAV_BACK.slot()",
    "Ui.People.FIND.slot()",
    "cubeeHolder == null ? null : cubeeHolder.slotTargets",
    "case BACK ->",
    "this.switchCubeeSurface(player, CubeeSurface.HOME);",
    "this.openNextTick(() -> this.openCubee(player));",
    "case NEXT_PAGE -> this.openNextTick(() -> this.openPeople(player, this.peopleNavigationService.nextLoopPage(pageIndex, this.peopleNavigationService.listPeople(player).size())));",
    "Player target = Bukkit.getPlayer((UUID)peopleClick.target());",
    "this.peopleNavigationService.canOpenPlayer(target)",
    "this.openNextTick(() -> this.openPlayer(player, target, pageIndex));",
    "this.openNextTick(() -> this.openPeople(player, pageIndex));",
    "private void handlePlayerPageClick(Player player, int slot)",
    "this.peopleClickService.playerPageAction(",
    "Ui.People.MESSAGE.slot()",
    "Ui.People.VISIT.slot()",
    "Ui.People.INVITE.slot()",
    "case BACK -> this.openNextTick(() -> this.openPeople(player, pageIndex));",
    "case MESSAGE -> this.startPrivateNote(player, target);",
    "case VISIT -> this.createMeetRequestOrReturn(player, target, pageIndex, RequestKind.VISIT);",
    "case INVITE -> this.createMeetRequestOrReturn(player, target, pageIndex, RequestKind.INVITE);",
    "private void createMeetRequestOrReturn(Player player, Player target, int pageIndex, RequestKind requestKind)",
    "this.peopleActionService.canCreateMeetRequest(player, target)",
    "this.createRequest(player, target, requestKind);",
    "private void addBedrockPeopleListButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, int pageIndex, Ui.ButtonSpec buttonSpec, UUID targetId, String lore)",
    "int virtualSlot = 1000;",
    "UUID targetId = this.peopleNavigationService.canOpenPlayer(player2) ? player2.getUniqueId() : null;",
    "this.addBedrockPeopleListButton(builder, arrayList, player, n, new Ui.ButtonSpec(virtualSlot++, Material.PLAYER_HEAD, player2.getName(), null), targetId, this.peopleNavigationService.itemStatus(player2));",
    "this.addBedrockPeopleListButton(builder, arrayList, player, n, Ui.Shared.FORM_BACK, null, null);",
    "Map<Integer, UUID> slotTargets = new HashMap<>();",
    "slotTargets.put(buttonSpec.slot(), targetId);",
    "Ui.Shared.FORM_BACK.slot()",
    "Ui.People.FIND.slot()",
    "case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> {",
    "case OPEN_PLAYER -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> {",
    "this.peopleNavigationService.canOpenPlayer(target)",
    "this.openPlayer(player, target);",
    "this.openPeople(player, pageIndex);",
    "case NONE ->",
    "this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> {});",
    "private void addBedrockPlayerPageButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, Ui.ButtonSpec buttonSpec)",
    "this.addBedrockPlayerPageButton(builder, arrayList, player, player2, Ui.People.VISIT);",
    "this.addBedrockPlayerPageButton(builder, arrayList, player, player2, Ui.People.INVITE);",
    "this.addBedrockPlayerPageButton(builder, arrayList, player, player2, Ui.People.MESSAGE);",
    "this.addBedrockPlayerPageButton(builder, arrayList, player, player2, Ui.Shared.FORM_BACK);",
    "Ui.Shared.FORM_BACK.slot()",
    "case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openLastPeople(player));",
    "case MESSAGE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startPrivateNoteIfCurrent(player, target.getUniqueId()));",
    "case VISIT -> this.bedrockButton(builder, actions, buttonSpec, () -> this.createBedrockMeetRequestOrReturn(player, target.getUniqueId(), RequestKind.VISIT));",
    "case INVITE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.createBedrockMeetRequestOrReturn(player, target.getUniqueId(), RequestKind.INVITE));",
    "private void createBedrockMeetRequestOrReturn(Player player, UUID targetId, RequestKind requestKind)",
    "Player target = Bukkit.getPlayer((UUID)targetId);",
    "this.openLastPeople(player);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendPeopleClickService: $snippet"
    }
}

Write-Host "Backend People click contract OK"
