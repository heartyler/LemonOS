param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxConfirmClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxConfirmClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxConfirmClickService",
    "ConfirmAction action(int clickedSlot, int confirmSlot, int cancelSlot)",
    "if (clickedSlot == confirmSlot)",
    "return ConfirmAction.CONFIRM;",
    "if (clickedSlot == cancelSlot)",
    "return ConfirmAction.CANCEL;",
    "return ConfirmAction.NONE;",
    "enum ConfirmAction",
    "NONE,",
    "CONFIRM,",
    "CANCEL;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxConfirmClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxConfirmClickService sandboxConfirmClickService;",
    "new BackendSandboxConfirmClickService()",
    "this.handleSandboxConfirmClick(player, n, () -> this.placeClone(player), () -> this.cancelClonePreview(player.getUniqueId()));",
    "this.handleSandboxConfirmClick(player, n, () -> this.placeClear(player), () -> this.cancelClearPreview(player.getUniqueId()));",
    "this.handleSandboxConfirmClick(player, n, () -> this.placeRotate(player), () -> this.cancelRotatePreview(player.getUniqueId()));",
    "this.handleSandboxConfirmClick(player, n, () -> this.placeFlip(player), () -> this.cancelFlipPreview(player.getUniqueId()));",
    "private void handleSandboxConfirmClick(Player player, int slot, Runnable confirmAction, Runnable cancelAction)",
    "this.sandboxConfirmClickService.action(slot, 14, 12)",
    "case CONFIRM -> confirmAction.run();",
    "case CANCEL ->",
    "cancelAction.run();",
    "player.closeInventory();",
    'player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));',
    "private void addBedrockSandboxConfirmButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, Ui.ButtonSpec confirmSpec, Runnable confirmAction, Runnable cancelAction)",
    "this.sandboxConfirmClickService.action(",
    "confirmSpec.slot()",
    "Ui.Confirm.CANCEL.slot()",
    "this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.PLACE, Ui.Confirm.PLACE, () -> this.placeClone(player), () -> this.cancelClonePreview(player.getUniqueId()));",
    "this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.CLEAR, Ui.Confirm.CLEAR, () -> this.placeClear(player), () -> this.cancelClearPreview(player.getUniqueId()));",
    "this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.PLACE, Ui.Confirm.PLACE, () -> this.placeRotate(player), () -> this.cancelRotatePreview(player.getUniqueId()));",
    "this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.PLACE, Ui.Confirm.PLACE, () -> this.placeFlip(player), () -> this.cancelFlipPreview(player.getUniqueId()));",
    "case CONFIRM -> this.bedrockButton(builder, actions, buttonSpec, confirmAction);",
    "case CANCEL -> this.bedrockButton(builder, actions, buttonSpec, () -> {",
    "FloodgateApi.getInstance().closeForm(player.getUniqueId());"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxConfirmClickService: $snippet"
    }
}

Write-Host "Backend Sandbox confirm click contract OK"
