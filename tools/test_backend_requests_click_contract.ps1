param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendRequestsClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendRequestsClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendRequestsClickService",
    "RequestClickAction action(int clickedSlot, int declineSlot, int ignoreSlot, int acceptSlot)",
    "if (clickedSlot == ignoreSlot)",
    "return RequestClickAction.IGNORE;",
    "if (clickedSlot == acceptSlot)",
    "return RequestClickAction.ACCEPT;",
    "if (clickedSlot == declineSlot)",
    "return RequestClickAction.DECLINE;",
    "return RequestClickAction.NONE;",
    "enum RequestClickAction",
    "NONE,",
    "IGNORE,",
    "ACCEPT,",
    "DECLINE;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendRequestsClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendRequestsClickService requestsClickService;",
    "new BackendRequestsClickService()",
    "this.handleRequestsClick(player, n);",
    "private void handleRequestsClick(Player player, int slot)",
    "this.requestsClickService.action(slot, 12, 13, 14)",
    "if (requestClickAction == BackendRequestsClickService.RequestClickAction.IGNORE)",
    "if (requestClickAction == BackendRequestsClickService.RequestClickAction.NONE)",
    "BackendMeetRequestService.RequestState<RequestKind> requestState = this.meetRequestService.incoming(player.getUniqueId());",
    "requestState == null || requestState.expired",
    'player.sendMessage((Component)Component.text((String)"too late.", (TextColor)NamedTextColor.DARK_GRAY));',
    "this.clearRequests(player.getUniqueId());",
    "this.requestMatchesHolder(cubeeHolder, requestState)",
    "case ACCEPT -> this.acceptRequest(player, requestState);",
    "case DECLINE ->",
    "this.declineRequest(requestState);",
    "player.closeInventory();",
    'player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));',
    "private void addBedrockRequestButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, BackendMeetRequestService.RequestState<RequestKind> requestState, Ui.ButtonSpec buttonSpec)",
    "Ui.ButtonSpec acceptSpec = requestState.kind == RequestKind.VISIT ? Ui.Requests.SURE_VISIT : Ui.Requests.SURE_INVITE;",
    "this.requestsClickService.action(",
    "Ui.Requests.LATER.slot()",
    "acceptSpec.slot()",
    "this.addBedrockRequestButton(builder, arrayList, player, requestState, Ui.Requests.SURE_VISIT);",
    "this.addBedrockRequestButton(builder, arrayList, player, requestState, Ui.Requests.SURE_INVITE);",
    "this.addBedrockRequestButton(builder, arrayList, player, requestState, Ui.Requests.LATER);",
    "case ACCEPT -> this.bedrockButton(builder, actions, buttonSpec, () -> this.acceptOrExpire(player, requestState));",
    "case DECLINE -> this.bedrockButton(builder, actions, buttonSpec, () -> {",
    "this.declineOrExpire(player, requestState);",
    "FloodgateApi.getInstance().closeForm(player.getUniqueId());"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendRequestsClickService: $snippet"
    }
}

Write-Host "Backend Requests click contract OK"
