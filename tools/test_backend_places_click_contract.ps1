param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPlacesClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendPlacesClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendPlacesClickService",
    "<T> PlacesClick<T> action(int clickedSlot, int backSlot, T currentServer, T lobby, T survival, T creative)",
    "if (clickedSlot == backSlot)",
    "return new PlacesClick<T>(PlacesClickAction.BACK, null);",
    "T target = this.targetForSlot(clickedSlot, lobby, survival, creative);",
    "return new PlacesClick<T>(PlacesClickAction.NONE, null);",
    "if (target == currentServer || target.equals(currentServer))",
    "return new PlacesClick<T>(PlacesClickAction.RETURN_SPAWN, target);",
    "return new PlacesClick<T>(PlacesClickAction.TRAVEL, target);",
    "private <T> T targetForSlot(int clickedSlot, T lobby, T survival, T creative)",
    "clickedSlot == 12 ? lobby",
    "clickedSlot == 13 ? survival",
    "clickedSlot == 14 ? creative",
    "record PlacesClick<T>(PlacesClickAction action, T target)",
    "enum PlacesClickAction",
    "NONE,",
    "BACK,",
    "RETURN_SPAWN,",
    "TRAVEL;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendPlacesClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendPlacesClickService placesClickService;",
    "new BackendPlacesClickService()",
    "this.handlePlacesClick(player, n);",
    "private void handlePlacesClick(Player player, int slot)",
    "BackendPlacesClickService.PlacesClick<ServerId> placesClick = this.placesClickService.action(",
    "Ui.Shared.NAV_BACK.slot()",
    "this.currentServer",
    "ServerId.LOBBY",
    "ServerId.SURVIVAL",
    "ServerId.CREATIVE",
    "case BACK ->",
    "this.switchCubeeSurface(player, CubeeSurface.HOME);",
    "this.openCubee(player);",
    "case RETURN_SPAWN -> this.returnServerSpawn(player);",
    "case TRAVEL -> this.startTravel(player, placesClick.target());",
    "private void addBedrockPlaceButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, ServerId targetServer)",
    "this.addBedrockPlaceButton(builder, arrayList, player, serverId);",
    "this.placeLogicalSlot(targetServer)",
    "case RETURN_SPAWN -> this.bedrockButton(builder, actions, this.placeName(targetServer), `"current.`", () -> this.returnServerSpawn(player));",
    "case TRAVEL -> this.bedrockButton(builder, actions, this.placeTargetSpec(0, targetServer), () -> {",
    "this.isServerAvailable(targetServer) || this.isPlaceWakeable(targetServer)",
    "this.startTravel(player, targetServer);",
    "this.openBedrockGo(player);",
    "private void addBedrockPlaceBackButton(SimpleForm.Builder builder, List<Runnable> actions, Player player)",
    "placesClick.action() == BackendPlacesClickService.PlacesClickAction.BACK",
    "this.bedrockButton(builder, actions, Ui.Shared.FORM_BACK, () -> {",
    "private int placeLogicalSlot(ServerId serverId)",
    "case LOBBY -> 12;",
    "case SURVIVAL -> 13;",
    "case CREATIVE -> 14;"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendPlacesClickService: $snippet"
    }
}

$removedPluginSnippets = @(
    "private ServerId targetForGoSlot(",
    "this.targetForGoSlot(n)"
)

foreach ($snippet in $removedPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still contains old places click helper: $snippet"
    }
}

Write-Host "Backend Places click contract OK"
