param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Source = Join-Path $Root "src\main\java\dev\lemonos"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Source "LemonOSPlugin.java")
$Definition = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendBoardDefinition.java")
$Boards = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendBoardOrchestrationService.java")
$Atmosphere = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendAtmosphereOrchestrationService.java")

foreach ($snippet in @(
    "record BackendBoardDefinition(",
    "servers = Set.copyOf(servers)",
    'return "boards." + this.dataKey',
    "boolean enabledOn(String server)"
)) {
    if (-not $Definition.Contains($snippet)) { throw "Board definition boundary missing: $snippet" }
}

foreach ($snippet in @(
    "record Plan(boolean active, long periodTicks, boolean clearStayedClose, List<String> disabledRolePrefixes)",
    'boolean lobby = "lobby".equals(server)',
    "config.enabled(BackendBoardConfig.STAYED_CLOSE)",
    "refreshMinutes = Math.min(refreshMinutes, config.refreshMinutes(definition.dataKey()))",
    "1200L * Math.max(1, refreshMinutes)",
    "List.copyOf(disabledRolePrefixes)"
)) {
    if (-not $Boards.Contains($snippet)) { throw "Board orchestration missing: $snippet" }
}

foreach ($snippet in @(
    "record Schedule(boolean active, long initialDelayTicks, long periodTicks)",
    "survival && config.enabled()",
    "new Schedule(true, 40L, 20L)",
    "config.enabled() && !(resting && suspendWhileResting)"
)) {
    if (-not $Atmosphere.Contains($snippet)) { throw "Atmosphere orchestration missing: $snippet" }
}

foreach ($snippet in @(
    "this.boardOrchestrationService.plan(this.currentServer.proxyName, this.boardConfig, BOARD_DEFINITIONS)",
    "if (plan.clearStayedClose()) this.clearStayedCloseDisplays();",
    "for (String rolePrefix : plan.disabledRolePrefixes()) this.clearBoardDisplayPrefix(rolePrefix);",
    "this.atmosphereOrchestrationService.schedule(this.currentServer == ServerId.SURVIVAL, this.atmosphereConfig)",
    "this.atmosphereOrchestrationService.shouldTick(this.atmosphereConfig, this.resting(), this.restSuspendAtmosphere())"
)) {
    if (-not $Plugin.Contains($snippet)) { throw "Plugin orchestration wiring missing: $snippet" }
}

if ($Plugin.Contains("private static final class BoardDefinition")) { throw "Nested BoardDefinition remains in LemonOSPlugin." }

Write-Host "Backend feature orchestration contract OK"
