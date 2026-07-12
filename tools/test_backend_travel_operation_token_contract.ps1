param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Source = Join-Path $Root "src\main\java\dev\lemonos"
$State = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendTravelStateService.java")
$Start = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendTravelStartService.java")
$Wake = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendWakeTravelService.java")
$Local = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendLocalTravelService.java")
$Return = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendReturnTravelService.java")
$Finish = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendTravelFinishService.java")

foreach ($snippet in @(
    "BackendOperationRegistry<UUID, TravelState<T>>",
    "BackendOperationToken begin(",
    "this.travels.beginIfAbsent(uuid, token, state)",
    "this.travels.removeIfCurrent(uuid, token)",
    "this.travels.useIfCurrent(uuid, token",
    "BackendOperationTaskSlot taskSlot",
    "BackendOperationStatusLease statusLease",
    "state.taskSlot.replace(task)",
    "this.travels.useIfCurrent(uuid, token, state -> state.taskSlot.replace(task))",
    "state.statusLease.close()"
)) {
    if (-not $State.Contains($snippet)) { throw "Travel token state missing: $snippet" }
}

foreach ($pair in @(
    @{ Name="normal"; Text=$Start; Required="this.finishTravel.finish(player, target, token)" },
    @{ Name="wake"; Text=$Wake; Required="this.finishTravel.finish(player, target, token)" },
    @{ Name="local"; Text=$Local; Required="this.finish(player, target, token)" },
    @{ Name="return"; Text=$Return; Required="this.finish(player, locationSupplier, token)" }
)) {
    if (-not $pair.Text.Contains($pair.Required)) { throw "$($pair.Name) travel callback lacks token: $($pair.Required)" }
}

foreach ($text in @($Local, $Return)) {
    if (-not $text.Contains("replaceTaskIfCurrent(player.getUniqueId(), token, verificationTask)")) {
        throw "Local/Return verification task is not token-owned."
    }
}

if (-not $Finish.Contains("if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) == null) return;")) {
    throw "Cross-server travel can finish without current-token ownership."
}
if (-not $Wake.Contains("if (this.travelStateService.endIfCurrent(player.getUniqueId(), token) != null)")) {
    throw "Wake timeout can notify from a stale operation."
}

foreach ($text in @($Start, $Wake, $Local, $Return, $Finish)) {
    if ($text.Contains("travelStateService.end(player.getUniqueId())")) { throw "Identity-only Travel end remains." }
}

Write-Host "Backend Travel operation token contract OK"
