param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

foreach ($producer in @('pending', 'chain', 'sandbox', 'care-world', 'atmosphere')) {
    if (-not $plugin.Contains("runActionBarProducer(`"$producer`"")) {
        throw "Action Bar producer is not exception-isolated: $producer"
    }
}
foreach ($required in @(
    'catch (RuntimeException exception)',
    'this.logActionBarFailure(player, exception)',
    'this.actionBarProducerErrorLogNanos',
    'last == 0L || now - last >= 60_000_000_000L'
)) { if (-not $plugin.Contains($required)) { throw "Action Bar isolation contract missing: $required" } }

$pending = [regex]::Match($plugin, '(?s)private boolean pendingActionBarStatusActive\(Player player\) \{(.*?)\n    \}').Groups[1].Value
foreach ($forbidden in @('reloadIdentities', 'save', 'openPasscode', 'pendingIdentityInputs.put', 'clearPasscodeFeedback')) {
    if ($pending.Contains($forbidden)) { throw "Pending renderer contains side effect: $forbidden" }
}
foreach ($required in @('private void reconcileResetWaitingPlayers()', 'this.reconcileResetWaitingPlayers();')) {
    if (-not $plugin.Contains($required)) { throw "Reset reconciliation contract missing: $required" }
}

Write-Host "Backend Action Bar isolation contract OK"
