param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

foreach ($required in @(
    'private String homeTitle()',
    'return "Home";',
    'private Component homeTitleComponent()',
    'Component.text((String)"Home", (TextColor)HoneyPalette.DEFAULT_WHITE)',
    'private String careTitle()',
    'return "Care";',
    'private Component careTitleComponent()',
    'Component.text((String)"Care", (TextColor)HoneyPalette.DEFAULT_WHITE)',
    'SimpleForm.builder().title(this.homeTitle())',
    'SimpleForm.builder().title(this.careTitle())',
    'this.homeTitleComponent()',
    'this.careTitleComponent()'
)) { if (-not $plugin.Contains($required)) { throw "Cubee root title contract missing: $required" } }

foreach ($forbidden in @(
    'identityPageTitle',
    'identityPageTitleComponent',
    'identityLabel(',
    'homeTitle(Player',
    'careTitle(Player',
    'homeTitleComponent(Player',
    'careTitleComponent(Player'
)) { if ($plugin.Contains($forbidden)) { throw "Cubee root title still carries identity/status behavior: $forbidden" } }

Write-Host "Backend Cubee root title contract OK"
