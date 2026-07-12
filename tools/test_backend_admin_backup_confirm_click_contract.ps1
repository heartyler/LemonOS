param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminBackupConfirmClickService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminBackupConfirmClickService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminBackupConfirmClickService",
    "AdminBackupConfirmAction action(int clickedSlot, int backupSlot, int cancelSlot)",
    "return AdminBackupConfirmAction.BACKUP;",
    "return AdminBackupConfirmAction.CANCEL;",
    "return AdminBackupConfirmAction.NONE;",
    "enum AdminBackupConfirmAction",
    "BACKUP,",
    "CANCEL;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminBackupConfirmClickService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminBackupConfirmClickService adminBackupConfirmClickService;",
    "new BackendAdminBackupConfirmClickService()",
    "this.handleAdminBackupConfirmClick(player, n);",
    "private void handleAdminBackupConfirmClick(Player player, int slot)",
    "this.adminBackupConfirmClickService.action(",
    "14,",
    "12);",
    "case BACKUP -> this.startManualBackup(player);",
    "case CANCEL ->",
    "player.closeInventory();",
    'player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));'
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminBackupConfirmClickService: $snippet"
    }
}

Write-Host "Backend admin backup confirm click contract OK"
