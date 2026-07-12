param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$access = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendAccessLegacyService.java")

foreach ($required in @(
    'private final Set<UUID> pendingActionBarStatuses = ConcurrentHashMap.newKeySet()',
    'private BukkitTask pendingActionBarStatusTask',
    'this.startPendingActionBarStatusTask();',
    'this.pendingActionBarStatusTask.cancel();',
    'this.pendingActionBarStatuses.add(player.getUniqueId());',
    'runTaskTimer((Plugin)this',
    'this.pendingActionBarStatusActive(player)',
    'BackendActionBarCoordinator.Owner.PENDING',
    'this.pendingIdentityInputs.get(uuid) == IdentityInput.RESET_WAITING',
    'this.identityResetService.requestExistsForIdentity(this.identities, identity)',
    'boolean resetRequestExists = this.resetRequestExistsForIdentity(identityKey)',
    'this.resetRequestExistsByToken(string)',
    'this.pendingIdentityInputs.put(uuid, IdentityInput.LOGIN)',
    'this.openPasscode(player, false)',
    'this.activeSkinChanges.contains(uuid)',
    'this.meetRequestService.hasActive(uuid)',
    'this.accessLegacyService.hasPendingActor(uuid)',
    'this.chunkParticipant(uuid)',
    'this.backupParticipant(uuid)',
    'this.clearActionBar(uuid, BackendActionBarCoordinator.Owner.PENDING)',
    'this.actionBarCoordinator.remove(uUID)'
)) { if (-not $plugin.Contains($required)) { throw "Pending Action Bar lifecycle missing: $required" } }

$pendingMethod = [regex]::Match($plugin, '(?s)private boolean pendingActionBarStatusActive\(Player player\) \{(.*?)\n    \}').Groups[1].Value
foreach ($forbidden in @('reloadIdentities', 'openPasscode', 'pendingIdentityInputs.put', 'clearPasscodeFeedback')) {
    if ($pendingMethod.Contains($forbidden)) { throw "Pending Action Bar renderer still has a side effect: $forbidden" }
}

if (-not $access.Contains('boolean hasPendingActor(UUID actorId)')) {
    throw "Access waiting status is not tied to pending acknowledgement state."
}
Write-Host "Backend pending Action Bar lifecycle contract OK"
