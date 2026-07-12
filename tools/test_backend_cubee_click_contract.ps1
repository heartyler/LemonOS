param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendCubeeClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendCubeeClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendCubeeClickService",
    "HomeAction homeAction(",
    "int clickedSlot",
    "int lookSlot",
    "int careSlot",
    "boolean lookEnabled",
    "boolean admin",
    "boolean currentServerLobby",
    "boolean currentServerSurvival",
    "boolean peopleShortcutPublic",
    "boolean sandboxAvailable",
    "if (clickedSlot == lookSlot && lookEnabled)",
    "return HomeAction.LOOK;",
    "if (clickedSlot == careSlot && admin)",
    "return HomeAction.CARE;",
    "if (clickedSlot == 12 && !currentServerLobby && peopleShortcutPublic)",
    "return HomeAction.PEOPLE;",
    "if (clickedSlot == 13)",
    "return HomeAction.PLACES;",
    "if (clickedSlot == 14 && currentServerSurvival)",
    "return HomeAction.SURVIVAL_HOME;",
    "if (clickedSlot == 14 && sandboxAvailable)",
    "return HomeAction.SANDBOX;",
    "return HomeAction.NONE;",
    "boolean isNavBack(int clickedSlot, int backSlot)",
    "return clickedSlot == backSlot;",
    "enum HomeAction"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendCubeeClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendCubeeClickService cubeeClickService;",
    "new BackendCubeeClickService()",
    "this.handleHomePageClick(player, n);",
    "private void handleHomePageClick(Player player, int slot)",
    "this.cubeeClickService.homeAction(",
    "Ui.Home.LOOK.slot()",
    "Ui.Home.CARE.slot()",
    "this.lookEnabled()",
    "this.isAdmin(player)",
    "this.currentServer == ServerId.LOBBY",
    "this.currentServer == ServerId.SURVIVAL",
    "this.peopleShortcutPublic(player)",
    "this.sandboxAvailable(player)",
    "case LOOK -> this.startLookInput(player);",
    "case CARE ->",
    "this.switchCubeeRoot(player, CubeeRoot.CARE);",
    "this.openAdmin(player);",
    "case PEOPLE -> this.openNextTick(() -> this.openLastPeople(player));",
    "case PLACES -> this.openGo(player);",
    "case SURVIVAL_HOME -> this.returnSurvivalHome(player);",
    "case SANDBOX -> this.openDrawing(player);",
    "private void addBedrockHomeButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, String lore)",
    "this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.PEOPLE, this.homePeopleStatus(player));",
    "this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.PLACES, null);",
    "this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.HOME, location == null ? `"not ready yet.`" : Ui.Home.HOME.lore());",
    "this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.SANDBOX, null);",
    "this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.CARE, null);",
    "boolean homeButton = buttonSpec == Ui.Home.HOME;",
    "boolean sandboxButton = buttonSpec == Ui.Home.SANDBOX;",
    "false,",
    "homeButton || this.currentServer == ServerId.SURVIVAL && !sandboxButton",
    "sandboxButton || this.sandboxAvailable(player) && !homeButton",
    "case PEOPLE -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> this.openLastPeople(player));",
    "case PLACES -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openGo(player));",
    "case SURVIVAL_HOME -> this.bedrockButton(builder, actions, buttonSpec, lore, () -> {",
    "case SANDBOX -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openDrawing(player));",
    "case CARE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdmin(player));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendCubeeClickService: $snippet"
    }
}

Write-Host "Backend Cubee click contract OK"
