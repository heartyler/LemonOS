param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

foreach ($snippet in @(
    "private final BackendOperationRegistry<UUID, AdminSendOperation> adminSendOperations",
    "private final AtomicLong adminSendGenerationCounter",
    "BackendOperationToken token = this.nextAdminSendToken();",
    "this.adminSendOperations.beginIfAbsent(actor.getUniqueId(), token, pending)",
    "pending.taskSlot.replace(wakeTask);",
    "pending.taskSlot.replace(resultTimeoutTask);",
    "pending.statusLease.publish(Component.text(`"waiting`", NamedTextColor.GRAY));",
    "this.adminSendOperations.removeIfCurrent(actorId, pending.token)",
    "this.adminSendOperations.removeIfCurrent(pending.actorId, pending.token)",
    "this.adminSendOperations.isCurrent(pending.actorId, pending.token)",
    "operation.taskSlot.cancel();",
    "operation.statusLease.close();",
    "this.adminSendOperations.clear(operation -> this.cleanupAdminSendOperation(operation, false));"
)) {
    if (-not $Plugin.Contains($snippet)) { throw "Admin Send operation migration missing: $snippet" }
}

foreach ($forbidden in @(
    "Map<UUID, PendingAdminSend>",
    "private static final class PendingAdminSend",
    "pending.task =",
    "pending.operationId"
)) {
    if ($Plugin.Contains($forbidden)) { throw "Legacy Admin Send state remains: $forbidden" }
}

foreach ($message in @('"waiting"', '"sent"', '"try again"', '"out of range"', '"nothing changed"')) {
    if (-not $Plugin.Contains($message)) { throw "Admin Send message contract missing: $message" }
}

Write-Host "Backend Admin Send operation contract OK"
