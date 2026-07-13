param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$Service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendAdminSendService.java")

foreach ($snippet in @(
    "BackendOperationRegistry<UUID, SendOperation<T>> operations",
    "private final AtomicLong generationCounter",
    "token = this.nextToken();",
    "this.operations.beginIfAbsent(actorId, token, pending)",
    "pending.taskSlot.replace(wakeTask);",
    "pending.taskSlot.replace(timeoutTask);",
    "pending.statusLease.publish(Component.text(`"waiting`", NamedTextColor.GRAY));",
    "this.operations.removeIfCurrent(actorId, pending.token)",
    "this.operations.removeIfCurrent(pending.actorId, pending.token)",
    "this.operations.isCurrent(pending.actorId, pending.token)",
    "operation.taskSlot.cancel();",
    "operation.statusLease.close();",
    "this.operations.clear(operation -> this.cleanup(operation, false));"
)) {
    if (-not $Service.Contains($snippet)) { throw "Admin Send operation service missing: $snippet" }
}

foreach ($forbidden in @(
    "Map<UUID, PendingAdminSend>",
    "private static final class PendingAdminSend",
    "pending.task =",
    "pending.operationId"
)) {
    if ($Plugin.Contains($forbidden) -or $Service.Contains($forbidden)) { throw "Legacy Admin Send state remains: $forbidden" }
}

foreach ($message in @('"waiting"', '"sent"', '"try again"', '"out of range"', '"nothing changed"')) {
    if (-not $Service.Contains($message)) { throw "Admin Send message contract missing: $message" }
}

if (-not $Plugin.Contains("private BackendAdminSendService<ServerId> adminSendService;") -or
    -not $Plugin.Contains("this.adminSendService.clear();")) {
    throw "LemonOSPlugin is not wired through BackendAdminSendService."
}

Write-Host "Backend Admin Send operation contract OK"
