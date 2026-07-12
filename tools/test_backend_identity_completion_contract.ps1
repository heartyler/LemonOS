param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendIdentityCompletionService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendIdentityCompletionService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendIdentityCompletionService",
    "CompletionPlan loginCompletion(boolean notify)",
    "new CompletionPlan(commonCleanup(), SurfaceAction.LOGIN_SURFACE, notify)",
    "CompletionPlan trustedCompletion(boolean delaySurface)",
    "delaySurface ? SurfaceAction.TRUSTED_SURFACE_DELAYED : SurfaceAction.TRUSTED_SURFACE_NOW",
    "private List<CleanupAction> commonCleanup()",
    "CleanupAction.CLEAR_RESET_REQUEST",
    "CleanupAction.CLEAR_PENDING_INPUT",
    "CleanupAction.CLEAR_PENDING_PASSCODE",
    "CleanupAction.CLEAR_GUIDANCE",
    "CleanupAction.CLEAR_PASSCODE_FEEDBACK",
    "CleanupAction.UNLOCK_AUTH",
    "CleanupAction.MARK_AUTHENTICATED",
    "CleanupAction.SYNC_ACCESS",
    "CleanupAction.CLEAR_RESET_INPUT_COUNT",
    "CleanupAction.SAVE_SESSION",
    "record CompletionPlan(List<CleanupAction> cleanupActions, SurfaceAction surfaceAction, boolean notifyPlayer)",
    "enum CleanupAction",
    "enum SurfaceAction",
    "LOGIN_SURFACE",
    "TRUSTED_SURFACE_NOW",
    "TRUSTED_SURFACE_DELAYED"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendIdentityCompletionService missing required behavior snippet: $snippet"
    }
}

$cleanupOrder = @(
    "CleanupAction.CLEAR_RESET_REQUEST",
    "CleanupAction.CLEAR_PENDING_INPUT",
    "CleanupAction.CLEAR_PENDING_PASSCODE",
    "CleanupAction.CLEAR_GUIDANCE",
    "CleanupAction.CLEAR_PASSCODE_FEEDBACK",
    "CleanupAction.UNLOCK_AUTH",
    "CleanupAction.MARK_AUTHENTICATED",
    "CleanupAction.SYNC_ACCESS",
    "CleanupAction.CLEAR_RESET_INPUT_COUNT",
    "CleanupAction.SAVE_SESSION"
)
$lastIndex = -1
foreach ($item in $cleanupOrder) {
    $index = $Service.IndexOf($item)
    if ($index -lt 0 -or $index -le $lastIndex) {
        throw "BackendIdentityCompletionService must preserve completion cleanup order: $item"
    }
    $lastIndex = $index
}

$requiredPluginSnippets = @(
    "private BackendIdentityCompletionService identityCompletionService;",
    "this.identityCompletionService = new BackendIdentityCompletionService();",
    "this.applyIdentityCompletionCleanup(player, this.identityCompletionService.loginCompletion(bl));",
    "BackendIdentityCompletionService.CompletionPlan completionPlan = this.identityCompletionService.trustedCompletion(bl);",
    "private void applyIdentityCompletionCleanup(Player player, BackendIdentityCompletionService.CompletionPlan completionPlan)",
    "case CLEAR_RESET_REQUEST -> this.clearResetRequest(this.identityKey(player));",
    "case CLEAR_PENDING_INPUT -> this.pendingIdentityInputs.remove(player.getUniqueId());",
    "case CLEAR_PENDING_PASSCODE -> this.pendingPasscodes.remove(player.getUniqueId());",
    "case CLEAR_GUIDANCE -> this.passcodeGuidanceShown.remove(player.getUniqueId());",
    "case CLEAR_PASSCODE_FEEDBACK -> this.clearPasscodeFeedback(player.getUniqueId());",
    "case UNLOCK_AUTH -> this.authLocked.remove(player.getUniqueId());",
    "case MARK_AUTHENTICATED -> this.authenticatedIdentities.add(player.getUniqueId());",
    "case SYNC_ACCESS -> this.syncAccessState(player);",
    "case CLEAR_RESET_INPUT_COUNT -> this.resetInputCounts.remove(player.getUniqueId());",
    "case SAVE_SESSION -> this.saveIdentitySession(player);",
    "completionPlan.surfaceAction() == BackendIdentityCompletionService.SurfaceAction.TRUSTED_SURFACE_DELAYED",
    "Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.finishTrustedSurface(player), 2L);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendIdentityCompletionService: $snippet"
    }
}

Write-Host "Backend identity completion contract OK"
