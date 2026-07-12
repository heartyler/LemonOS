param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$sourceRoot = Join-Path $Root "src\main\java\dev\lemonos"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $sourceRoot "LemonOSPlugin.java")
$meet = Get-Content -Raw -LiteralPath (Join-Path $sourceRoot "BackendMeetRequestService.java")

foreach ($required in @(
    'private void sendWaitingStatus(Player player)',
    'this.publishActionBar(player, BackendActionBarCoordinator.Owner.PENDING, Component.text((String)"waiting", (TextColor)NamedTextColor.GRAY));',
    '"done.".equals(string) || "try again.".equals(string) || "too large.".equals(string)',
    'string.substring(0, string.length() - 1)',
    'this.notifyActionBar(player, BackendActionBarCoordinator.Owner.SANDBOX_NOTIFICATION, (Component)textComponent, 3000);',
    'player.sendMessage((Component)Component.text((String)string, (TextColor)namedTextColor));'
)) {
    if (-not $plugin.Contains($required)) {
        throw "LemonOSPlugin missing approved status-surface behavior: $required"
    }
}

if (-not $meet.Contains('this.pendingStatus.accept(sender);')) {
    throw "Meet request waiting status is not on Action Bar."
}

$waitingChat = Get-ChildItem -LiteralPath $sourceRoot -Filter "*.java" -File |
    Select-String -Pattern 'sendMessage\([^\r\n]*"waiting\."'
if ($waitingChat.Count -gt 0) {
    throw "A waiting operation status still writes to Chat: $($waitingChat[0].Path):$($waitingChat[0].LineNumber)"
}

Write-Host "Backend status surface contract OK"
