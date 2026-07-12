param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$defaults = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendConfigDefaultGroupService.java")
$messages = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\messages.yml")

foreach ($required in @(
    'this.sendSandboxGuidance(player, string, "First corner.");',
    'this.promptText("use-more-tool", "Use wooden hoe.")',
    'this.promptText("use-basic-tool", "Use wooden axe.")',
    'this.sendSandboxGuidance(player, toolPrompt, "Second corner.");',
    'this.sendSandboxGuidance(player, this.promptText("use-more-tool", "Use wooden hoe."), "Center.");',
    'Component.text((String)string, (TextColor)HoneyPalette.DEFAULT_WHITE)',
    'Component.text((String)string2, (TextColor)NamedTextColor.GRAY)',
    'this.publishActionBar(player, BackendActionBarCoordinator.Owner.SANDBOX, component)'
)) {
    if (-not $plugin.Contains($required)) {
        throw "Sandbox tool-first Action Bar contract missing: $required"
    }
}

foreach ($forbidden in @(
    'private void sendSandboxGuidance(Player player, String string, String string2, String string3)',
    'sendSandboxGuidance(player, "First corner.", "Start the shape."',
    'sendSandboxGuidance(player, "Second corner.", "Finish the shape.")',
    'sendSandboxGuidance(player, "Center.", "Pick the middle point."',
    '"use wooden axe."',
    '"use wooden hoe."'
)) {
    if ($plugin.Contains($forbidden)) {
        throw "Legacy Sandbox tool Chat/guidance remains: $forbidden"
    }
}

foreach ($required in @(
    '"prompts.use-basic-tool", "Use wooden axe."',
    '"prompts.use-more-tool", "Use wooden hoe."',
    '"prompts.use-basic-tool", "use wooden axe.", "Use wooden axe."',
    '"prompts.use-more-tool", "use wooden hoe.", "Use wooden hoe."'
)) {
    if (-not $defaults.Contains($required)) {
        throw "Sandbox prompt default/migration missing: $required"
    }
}
if ($messages -notmatch '(?m)^  use-basic-tool: "Use wooden axe\."\r?$' -or
    $messages -notmatch '(?m)^  use-more-tool: "Use wooden hoe\."\r?$') {
    throw "Canonical Sandbox prompt template is missing."
}

Write-Host "Backend Sandbox tool guidance contract OK"
