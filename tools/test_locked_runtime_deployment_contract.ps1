param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$deploy = Get-Content -Raw (Join-Path (Split-Path -Parent $Root) "tools\deploy_honey_26_2.ps1")
foreach ($required in @(
    "difficulty = 'peaceful'; gamemode = 'adventure'; 'force-gamemode' = 'true'; 'generate-structures' = 'false'; 'view-distance' = '8'; 'simulation-distance' = '4'",
    "difficulty = 'easy'; gamemode = 'survival'; 'force-gamemode' = 'true'; 'generate-structures' = 'true'; 'view-distance' = '16'; 'simulation-distance' = '8'",
    "difficulty = 'peaceful'; gamemode = 'creative'; 'force-gamemode' = 'true'; 'generate-structures' = 'true'; 'view-distance' = '16'; 'simulation-distance' = '8'",
    '[Parameter(Mandatory = $true)][string]$RuntimeRoot',
    'runtime = Split-Path -Leaf $RuntimeRoot')) {
    if (-not $deploy.Contains($required)) { throw "Locked runtime deployment missing: $required" }
}
Write-Host "Honey locked runtime deployment contract tests passed."
