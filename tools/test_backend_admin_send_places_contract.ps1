param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$Protocol = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendAdminSendProtocolService.java")
$Proxy = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\dev\lemonos\proxy\PeopleService.java")

$requiredPlugin = @(
    'new ButtonSpec(14, Material.COMPASS, "Send", "send them on.")',
    "ADMIN_PLAYER_SEND_PLACES",
    'this.subpageTitle("Send", "Places")',
    'SimpleForm.builder().title("Send / Places")',
    "ServerId targetServer = this.adminTargetServer(target);",
    "this.placeLogicalSlot(destination), Ui.Shared.FORM_BACK.slot(), targetServer",
    'this.bedrockButton(builder, actions, this.placeName(destination), "current.", action);',
    "case RETURN_SPAWN -> this.sendAdminTargetToCurrentSpawn(actor, target);",
    "case TRAVEL -> this.startAdminSend(actor, target, click.target());",
    "existing.targetId.equals(target.getUniqueId()) && existing.destination == destination",
    "this.cancelAdminSend(actor.getUniqueId(), true);",
    'this.notifyAdminSend(actor, "out of range", NamedTextColor.DARK_GRAY);',
    'Component.text("waiting", NamedTextColor.GRAY)',
    "this.sendWakePlaceRequest(actor, destination);",
    "this.saveIdentityTransfer(target, pending.destination);",
    "this.adminSendProtocolService.send(actor, pending.targetId, pending.destination.proxyName, pending.token.operationId());",
    "pending.token.operationId().equals(operationId)",
    'this.notifyAdminSend(actor, "sent", NamedTextColor.GRAY);',
    'this.notifyAdminSend(Bukkit.getPlayer((UUID)actorId), "nothing changed", NamedTextColor.DARK_GRAY);',
    "BackendActionBarCoordinator.Owner.ADMIN_SEND",
    "private static final class AdminSendOperation",
    "BackendOperationRegistry<UUID, AdminSendOperation>",
    "BackendOperationTaskSlot taskSlot",
    "BackendOperationStatusLease statusLease",
    "this.adminSendOperations.removeIfCurrent(actorId, pending.token)",
    "this.adminSendOperations.isCurrent(pending.actorId, pending.token)"
)
foreach ($snippet in $requiredPlugin) { if (-not $Plugin.Contains($snippet)) { throw "Admin Send flow missing: $snippet" } }

$requiredProtocol = @("SEND_PLAYER_PLACE", "SEND_PLAYER_RESULT", "targetId", "operationId", "resultHandler.handle")
foreach ($snippet in $requiredProtocol) { if (-not $Protocol.Contains($snippet)) { throw "Admin Send backend protocol missing: $snippet" } }

$requiredProxy = @(
    "void sendPlayerPlace(ServerConnection serverConnection, UUID actorId, UUID targetId, String place, UUID operationId)",
    "this.playerOnSourceServer(serverConnection, actorId)",
    "this.playerOnSourceServer(serverConnection, targetId)",
    "target.get().createConnectionRequest(destination.get()).connect()",
    "AdminProtocol.SEND_RESULT_SENT",
    "AdminProtocol.SEND_RESULT_TRY_AGAIN"
)
foreach ($snippet in $requiredProxy) { if (-not $Proxy.Contains($snippet)) { throw "Admin Send proxy boundary missing: $snippet" } }

Write-Host "Backend admin Send / Places contract OK"
