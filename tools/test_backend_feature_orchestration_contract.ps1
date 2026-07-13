param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Source = Join-Path $Root "src\main\java\dev\lemonos"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Source "LemonOSPlugin.java")
$Definition = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendHudDefinition.java")
$Hud = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendHudOrchestrationService.java")
$Atmosphere = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendAtmosphereOrchestrationService.java")

foreach ($snippet in @(
    "record BackendHudDefinition(",
    "servers = Set.copyOf(servers)",
    'return "hud." + this.dataKey',
    "boolean enabledOn(String server)",
    "RankingSource rankingSource",
    "boolean usesPlaytime()"
)) {
    if (-not $Definition.Contains($snippet)) { throw "HUD definition boundary missing: $snippet" }
}

foreach ($snippet in @(
    "record Plan(boolean active, long periodTicks, List<String> disabledRolePrefixes)",
    "Plan plan(String server, BackendHudConfig config, List<BackendHudDefinition> definitions)",
    "refreshMinutes = Math.min(refreshMinutes, config.refreshMinutes(definition.dataKey()))",
    "1200L * Math.max(1, refreshMinutes)",
    "List.copyOf(disabledRolePrefixes)"
)) {
    if (-not $Hud.Contains($snippet)) { throw "HUD orchestration missing: $snippet" }
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
    "this.hudOrchestrationService.plan(this.currentServer.proxyName, this.hudConfig, HUD_DEFINITIONS)",
    "for (String rolePrefix : plan.disabledRolePrefixes()) this.clearHudDisplayPrefix(rolePrefix);",
    "this.atmosphereOrchestrationService.schedule(this.currentServer == ServerId.SURVIVAL, this.atmosphereConfig)",
    "this.atmosphereOrchestrationService.shouldTick(this.atmosphereConfig, this.resting(), this.restSuspendAtmosphere())"
)) {
    if (-not $Plugin.Contains($snippet)) { throw "Plugin orchestration wiring missing: $snippet" }
}

if ($Plugin.Contains("private static final class BoardDefinition")) { throw "Nested BoardDefinition remains in LemonOSPlugin." }

Write-Host "Backend feature orchestration contract OK"
