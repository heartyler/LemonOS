param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$layout = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendPasscodeLayout.java")
$display = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendPasscodeDisplayService.java")
$input = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendPasscodeInputService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

foreach ($required in @(
    'DIGIT_SLOTS = new int[]{2, 3, 4, 11, 12, 13, 20, 21, 22, 23}',
    'DIGIT_LABELS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"}',
    'return Integer.parseInt(DIGIT_LABELS[i])',
    'return null;'
)) { if (-not $layout.Contains($required)) { throw "Passcode layout parity missing: $required" } }

if (-not $display.Contains('this.layout.digitButtons()')) { throw "Passcode display does not use shared layout." }
if (-not $input.Contains('return this.layout.digit(slot);')) { throw "Passcode action does not use shared layout." }
if (-not $plugin.Contains('new BackendPasscodeInputService(backendPasscodeLayout)') -or -not $plugin.Contains('new BackendPasscodeDisplayService(backendPasscodeLayout)')) {
    throw "Passcode display and action do not share the same layout instance."
}
foreach ($legacy in @('case 5 -> 3;', 'case 14 -> 6;', 'case 24 -> 0;')) {
    if ($input.Contains($legacy)) { throw "Hidden legacy numpad action remains: $legacy" }
}
Write-Host "Backend passcode layout parity contract OK"
