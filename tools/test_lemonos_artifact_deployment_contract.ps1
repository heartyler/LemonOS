param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$deployPath = Join-Path $Root "tools\deploy_lemonos_artifacts.ps1"
$deploy = Get-Content -Raw -LiteralPath $deployPath
foreach ($required in @(
    '[Parameter(Mandatory = $true)][string]$RuntimeRoot',
    'if ([string]$manifest.product -ne "Honey")',
    'foreach ($required in @("velocity", "lobby", "survival", "creative"))',
    'Refusing LemonOS deployment while Honey runtime ports are active.',
    'Deploy-Artifact $proxy "velocity\plugins\lemonos_proxy.jar"',
    'Deploy-Artifact $backend "$server\plugins\lemonos.jar"',
    'lemonosDeployment -NotePropertyValue "artifact-deploy"'
)) {
    if (-not $deploy.Contains($required)) { throw "LemonOS artifact deployment contract missing: $required" }
}
Write-Host "LemonOS artifact deployment contract tests passed."
