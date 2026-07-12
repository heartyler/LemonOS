param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendIdentityFlowService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendIdentityFlowService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendIdentityFlowService",
    "BeginAction bedrockBeginAction(boolean bedrockTrusted, BooleanSupplier transferAccepted)",
    "if (!bedrockTrusted)",
    "return BeginAction.KICK_UNAVAILABLE;",
    "transferAccepted.getAsBoolean() ? BeginAction.TRUSTED_TRANSFERRED : BeginAction.TRUSTED",
    "BeginAction javaBeginAction(",
    "boolean javaLoginEnabled",
    "BooleanSupplier resetGrantExists",
    "BooleanSupplier transferAccepted",
    "BooleanSupplier sessionAccepted",
    "BooleanSupplier registered",
    "if (!javaLoginEnabled)",
    "if (resetGrantExists.getAsBoolean())",
    "if (transferAccepted.getAsBoolean())",
    "if (sessionAccepted.getAsBoolean())",
    "registered.getAsBoolean() ? BeginAction.PASSCODE_LOGIN : BeginAction.PASSCODE_CREATE",
    "enum BeginAction",
    "KICK_UNAVAILABLE",
    "TRUSTED",
    "TRUSTED_TRANSFERRED",
    "FORCE_PASSCODE_RESET",
    "PASSCODE_LOGIN",
    "PASSCODE_CREATE"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendIdentityFlowService missing required behavior snippet: $snippet"
    }
}

$javaLoginIndex = $Service.IndexOf("if (!javaLoginEnabled)")
$resetIndex = $Service.IndexOf("if (resetGrantExists.getAsBoolean())")
$transferIndex = $Service.IndexOf("if (transferAccepted.getAsBoolean())")
$sessionIndex = $Service.IndexOf("if (sessionAccepted.getAsBoolean())")
$registeredIndex = $Service.IndexOf("registered.getAsBoolean() ? BeginAction.PASSCODE_LOGIN : BeginAction.PASSCODE_CREATE")
if ($javaLoginIndex -lt 0 -or $resetIndex -lt 0 -or $transferIndex -lt 0 -or $sessionIndex -lt 0 -or $registeredIndex -lt 0 -or -not ($javaLoginIndex -lt $resetIndex -and $resetIndex -lt $transferIndex -and $transferIndex -lt $sessionIndex -and $sessionIndex -lt $registeredIndex)) {
    throw "BackendIdentityFlowService must preserve Java identity begin order: login gate, reset grant, transfer, session, registered passcode state."
}

$requiredPluginSnippets = @(
    "private BackendIdentityFlowService identityFlowService;",
    "this.identityFlowService = new BackendIdentityFlowService();",
    "private void beginIdentity(Player player)",
    "this.openBedrockIdentityBeginAction(player);",
    "this.identityFlowService.javaBeginAction(",
    "this.javaLoginEnabled()",
    "() -> this.identityResetGrantExists(this.identityKey(player))",
    "() -> this.acceptIdentityTransfer(player)",
    "() -> this.acceptIdentitySession(player)",
    "return this.identityRegistered(this.identityKey(player));",
    "private void openBedrockIdentityBeginAction(Player player)",
    "this.identityFlowService.bedrockBeginAction(",
    "this.bedrockTrusted()",
    "private void openJavaIdentityBeginAction(Player player, BackendIdentityFlowService.BeginAction action)",
    "case TRUSTED -> this.finishTrustedIdentity(player, true);",
    "case TRUSTED_TRANSFERRED ->",
    "case FORCE_PASSCODE_RESET -> this.forceIdentityPasscodeReset(player);",
    "case PASSCODE_LOGIN -> this.openIdentityPasscodeStart(player, IdentityInput.LOGIN);",
    "case PASSCODE_CREATE -> this.openIdentityPasscodeStart(player, IdentityInput.CREATE_PASSCODE);",
    "private void openIdentityPasscodeStart(Player player, IdentityInput identityInput)",
    "this.hideAuthItems(player);",
    "this.pendingIdentityInputs.put(player.getUniqueId(), identityInput);",
    "this.authLocked.add(player.getUniqueId());",
    "this.tellIdentityPrompt(player);",
    "Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.openLogin(player), 40L);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendIdentityFlowService: $snippet"
    }
}

Write-Host "Backend identity flow contract OK"
