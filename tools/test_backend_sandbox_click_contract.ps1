param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxClickService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

$slots = [ordered]@{ 0='BACK'; 1='UNDO'; 2='REDO'; 3='SET'; 4='WALL'; 5='FLOOR'; 12='REPLACE'; 13='CLONE'; 14='CLEAR'; 21='CIRCLE'; 22='FLIP'; 23='ROTATE' }
foreach ($entry in $slots.GetEnumerator()) {
    $route = "case $($entry.Key) -> SandboxClickAction.$($entry.Value);"
    if (-not $service.Contains($route)) { throw "Sandbox slot route missing: $route" }
    $spec = "new ButtonSpec($($entry.Key),"
    if (-not $plugin.Contains($spec)) { throw "Sandbox UI slot missing: $($entry.Key)" }
}
foreach ($required in @(
    'SandboxClickAction action(int clickedSlot)',
    'this.sandboxClickService.action(n)',
    'Component.text((String)"Sandbox", (TextColor)HoneyPalette.DEFAULT_WHITE)',
    'SimpleForm.builder().title("Sandbox")',
    'private void addBedrockSandboxButton',
    'this.sandboxClickService.action(buttonSpec.slot())'
)) { if (-not $plugin.Contains($required) -and -not $service.Contains($required)) { throw "Unified Sandbox contract missing: $required" } }

foreach ($forbidden in @('DRAWING_MORE', 'SandboxMore', 'SandboxMode', 'openDrawingMore', 'openBedrockDrawingMore', 'basicAction(', 'moreAction(', 'SandboxClickAction.MORE', 'SandboxClickAction.ESSENTIALS')) {
    if ($plugin.Contains($forbidden) -or $service.Contains($forbidden)) { throw "Removed Sandbox page behavior remains: $forbidden" }
}
Write-Host "Backend unified Sandbox click contract OK"
