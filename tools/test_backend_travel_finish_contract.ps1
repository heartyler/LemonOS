param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendTravelFinishService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendTravelFinishService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendTravelFinishService<T>",
    "private final BackendTravelStateService<T> travelStateService",
    "private final BackendTravelConnectService travelConnectService",
    "private final BiConsumer<Player, T> identityTransferSaver",
    "private final Function<T, String> proxyName",
    "BackendTravelFinishService(BackendTravelStateService<T> travelStateService, BackendTravelConnectService travelConnectService, BiConsumer<Player, T> identityTransferSaver, Function<T, String> proxyName)",
    "void finish(Player player, T target, BackendOperationToken token)",
    "this.travelStateService.endIfCurrent(player.getUniqueId(), token)",
    "if (!player.isOnline())",
    "this.identityTransferSaver.accept(player, target)",
    "this.travelConnectService.connect(player, this.proxyName.apply(target))"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendTravelFinishService missing required travel finish contract text: $required"
    }
}

foreach ($required in @(
    "private BackendTravelFinishService<ServerId> travelFinishService",
    "this.travelFinishService = new BackendTravelFinishService<ServerId>(this.travelStateService, this.travelConnectService, this::saveIdentityTransfer, serverId -> serverId.proxyName)",
    "private void finishTravel(Player player, ServerId serverId, BackendOperationToken token)",
    "this.travelFinishService.finish(player, serverId, token)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendTravelFinishService: $required"
    }
}

foreach ($forbidden in @(
    "this.endTravel(player.getUniqueId());`r`n        if (!player.isOnline())",
    "this.saveIdentityTransfer(player, serverId);`r`n        this.travelConnectService.connect(player, serverId.proxyName)"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend travel finish orchestration detail: $forbidden"
    }
}

Write-Host "LemonOS backend travel finish contract tests passed."
