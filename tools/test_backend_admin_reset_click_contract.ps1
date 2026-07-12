param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminResetClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminResetClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminResetClickService",
    "AdminResetAction action(int clickedSlot, int allowSlot, int denySlot)",
    "return AdminResetAction.ALLOW;",
    "return AdminResetAction.DENY;",
    "return AdminResetAction.NONE;",
    "enum AdminResetAction",
    "ALLOW,",
    "DENY;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminResetClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminResetClickService adminResetClickService;",
    "new BackendAdminResetClickService()",
    "this.handleAdminResetClick(player, n);",
    "private void handleAdminResetClick(Player player, int slot)",
    "String token = cubeeHolder == null ? null : cubeeHolder.resetToken;",
    "if (!this.adminResetActionService.canOpen(token))",
    "this.openNextTick(() -> this.openAdminRequests(player, 0));",
    "this.adminResetClickService.action(",
    "case ALLOW -> this.allowResetRequest(player, token);",
    "case DENY -> this.denyResetRequest(player, token);",
    "private void addBedrockAdminResetButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, String token, Ui.ButtonSpec buttonSpec)",
    "Ui.Reset.ALLOW.slot()",
    "Ui.Reset.NOT_NOW.slot()",
    "case ALLOW -> this.bedrockButton(builder, actions, buttonSpec, () -> this.allowResetRequest(player, token));",
    "case DENY -> this.bedrockButton(builder, actions, buttonSpec, () -> this.denyResetRequest(player, token));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminResetClickService: $snippet"
    }
}

Write-Host "Backend admin reset click contract OK"
