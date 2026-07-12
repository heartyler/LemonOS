param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $PluginPath -PathType Leaf)) {
    throw "LemonOSPlugin.java is missing."
}

$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredPluginSnippets = @(
    "private void addBedrockAdminKeyTakeConfirmButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, String accessName, Ui.ButtonSpec buttonSpec, int confirmSlot)",
    "BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(",
    "case CONFIRM -> this.bedrockButton(builder, actions, buttonSpec, () -> {",
    "case CANCEL -> this.bedrockButton(builder, actions, buttonSpec, () -> {",
    "private void addBedrockAdminResetButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, String token, Ui.ButtonSpec buttonSpec)",
    "BackendAdminResetClickService.AdminResetAction action = this.adminResetClickService.action(",
    "case ALLOW -> this.bedrockButton(builder, actions, buttonSpec, () -> this.allowResetRequest(player, token));",
    "case DENY -> this.bedrockButton(builder, actions, buttonSpec, () -> this.denyResetRequest(player, token));",
    "private void addBedrockCareSelfClearConfirmButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, int pageIndex, Ui.ButtonSpec buttonSpec)",
    "BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(",
    "case CONFIRM -> this.bedrockButton(builder, actions, buttonSpec, () -> this.clearCareSelfInventory(player));",
    "this.openCareSelf(player, pageIndex);",
    "private void addBedrockAdminPlayerClearConfirmButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, int pageIndex, Ui.ButtonSpec buttonSpec)",
    "BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(",
    "this.clearAdminTargetInventory(player, currentTarget);",
    "this.openAdminPlayerControlIfCurrent(player, target.getUniqueId(), pageIndex);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "Bedrock admin confirm parity missing required snippet: $snippet"
    }
}

Write-Host "Backend admin Bedrock confirm parity contract OK"
