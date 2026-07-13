param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminSendService.java"
$Service = Get-Content -Raw -LiteralPath $ServicePath
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
    "private BackendAdminSendService<ServerId> adminSendService;",
    "new BackendAdminSendService<ServerId>(",
    "this.adminSendService.start(actor, target, destination);",
    "this.adminSendService.sendToCurrentSpawn(actor, target);",
    "this.adminSendService.cancel(actorId, notify);",
    "if (this.adminSendService != null)",
    "this.adminSendService.clear();"
)
foreach ($snippet in $requiredPlugin) { if (-not $Plugin.Contains($snippet)) { throw "Admin Send flow missing: $snippet" } }

foreach ($forbidden in @(
    "private static final class AdminSendOperation",
    "BackendOperationRegistry<UUID, AdminSendOperation>",
    "private void dispatchAdminSend(",
    "private void failAdminSend(",
    "private AdminSendOperation currentAdminSend("
)) {
    if ($Plugin.Contains($forbidden)) { throw "LemonOSPlugin still owns Admin Send lifecycle: $forbidden" }
}

foreach ($snippet in @(
    "final class BackendAdminSendService<T>",
    "BackendOperationRegistry<UUID, SendOperation<T>>",
    "void start(Player actor, Player target, T destination)",
    "void sendToCurrentSpawn(Player actor, Player target)",
    "void finishResult(UUID actorId, UUID targetId, String place, UUID operationId, String result)",
    "void cancel(UUID actorId, boolean notify)",
    "void clear()",
    "this.operations.removeIfCurrent(actorId, pending.token)",
    "this.operations.isCurrent(pending.actorId, pending.token)",
    "WAKE_TIMEOUT_MILLIS",
    "RESULT_TIMEOUT_TICKS",
    "saturatingAdd",
    "FailureReporter"
)) {
    if (-not $Service.Contains($snippet)) { throw "BackendAdminSendService missing lifecycle contract: $snippet" }
}

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

$Classes = Join-Path $Root "build\test-admin-send"
$DependencyRoot = Join-Path $Root "third_party\runtime"
$Classpath = @(Get-ChildItem -LiteralPath $DependencyRoot -File -Filter "*.jar" | ForEach-Object FullName)
if ($Classpath.Count -eq 0) { throw "Backend test dependencies missing." }
if (Test-Path -LiteralPath $Classes) { Remove-Item -LiteralPath $Classes -Recurse -Force }
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
$Source = Join-Path $Root "src\main\java\dev\lemonos"
& (Join-Path $JdkRoot "bin\javac.exe") -Xlint:deprecation -Xlint:unchecked -Werror -encoding UTF-8 -cp ($Classpath -join ";") -d $Classes `
    $ServicePath `
    (Join-Path $Source "BackendAdminProtocol.java") `
    (Join-Path $Source "BackendActionBarCoordinator.java") `
    (Join-Path $Source "BackendOperationRegistry.java") `
    (Join-Path $Source "BackendOperationToken.java") `
    (Join-Path $Source "BackendOperationTaskSlot.java") `
    (Join-Path $Source "BackendOperationStatusLease.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendAdminSendHarness.java")
if ($LASTEXITCODE -ne 0) { throw "BackendAdminSend harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp ((@($Classes) + $Classpath) -join ";") dev.lemonos.BackendAdminSendHarness
if ($LASTEXITCODE -ne 0) { throw "BackendAdminSend behavior harness failed." }

Write-Host "Backend admin Send / Places contract OK"
