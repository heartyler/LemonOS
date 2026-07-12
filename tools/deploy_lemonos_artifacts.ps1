param(
    [Parameter(Mandatory = $true)][string]$RuntimeRoot
)

$ErrorActionPreference = "Stop"
$LemonOsRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path
$manifestPath = Join-Path $RuntimeRoot "deployment.json"
if (-not (Test-Path -LiteralPath $manifestPath -PathType Leaf)) {
    throw "deployment.json is missing from runtime root."
}
$manifest = Get-Content -Raw -LiteralPath $manifestPath | ConvertFrom-Json
if ([string]$manifest.product -ne "Honey") {
    throw "Refusing deployment: runtime product is not Honey."
}
foreach ($required in @("velocity", "lobby", "survival", "creative")) {
    if (-not (Test-Path -LiteralPath (Join-Path $RuntimeRoot $required) -PathType Container)) {
        throw "Refusing deployment: runtime component missing: $required"
    }
}
if ($null -eq $manifest.artifacts) {
    throw "Refusing deployment: artifact manifest section is missing."
}
$ports = @(25565, 30066, 30067, 30068, 31066, 31067, 31068)
$listeners = @(Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue | Where-Object LocalPort -In $ports)
if ($listeners.Count -gt 0) {
    throw "Refusing LemonOS deployment while Honey runtime ports are active."
}

$backend = Join-Path $LemonOsRoot "build\libs\lemonos.jar"
$proxy = Join-Path $LemonOsRoot "build\libs\lemonos_proxy.jar"
foreach ($artifact in @($backend, $proxy)) {
    if (-not (Test-Path -LiteralPath $artifact -PathType Leaf)) {
        throw "Built LemonOS artifact missing: $artifact"
    }
}

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backupRoot = Join-Path $RuntimeRoot ".deploy-backups\$timestamp-lemonos"
function Deploy-Artifact([string]$Source, [string]$RelativeTarget) {
    $target = [System.IO.Path]::GetFullPath((Join-Path $RuntimeRoot $RelativeTarget))
    if (-not $target.StartsWith($RuntimeRoot + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Artifact target escapes runtime root: $target"
    }
    New-Item -ItemType Directory -Path (Split-Path -Parent $target) -Force | Out-Null
    if (Test-Path -LiteralPath $target -PathType Leaf) {
        $backup = Join-Path $backupRoot $RelativeTarget
        New-Item -ItemType Directory -Path (Split-Path -Parent $backup) -Force | Out-Null
        Copy-Item -LiteralPath $target -Destination $backup -Force
    }
    $temporary = "$target.deploy-$([Guid]::NewGuid().ToString('N')).tmp"
    try {
        Copy-Item -LiteralPath $Source -Destination $temporary -Force
        Move-Item -LiteralPath $temporary -Destination $target -Force
    } finally {
        Remove-Item -LiteralPath $temporary -Force -ErrorAction SilentlyContinue
    }
}

Deploy-Artifact $proxy "velocity\plugins\lemonos_proxy.jar"
foreach ($server in @("lobby", "survival", "creative")) {
    Deploy-Artifact $backend "$server\plugins\lemonos.jar"
}

$sourceSnapshot = ((Get-Content -LiteralPath (Join-Path $LemonOsRoot "build\classes\lemonos-build.properties") | Select-String '^sourceSnapshotSha256=').Line -split '=', 2)[1]
$manifest.deployedAt = (Get-Date).ToString("o")
$manifest.sourceSnapshotSha256 = $sourceSnapshot
$manifest.artifacts.lemonos = (Get-FileHash -LiteralPath $backend -Algorithm SHA256).Hash
$manifest.artifacts.lemonosProxy = (Get-FileHash -LiteralPath $proxy -Algorithm SHA256).Hash
$manifest | Add-Member -NotePropertyName lemonosDeployment -NotePropertyValue "artifact-deploy" -Force
$manifest | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath $manifestPath -Encoding UTF8

[pscustomobject]@{
    RuntimeRoot = $RuntimeRoot
    BackupRoot = $backupRoot
    SourceSnapshotSha256 = $sourceSnapshot
    BackendSha256 = $manifest.artifacts.lemonos
    ProxySha256 = $manifest.artifacts.lemonosProxy
}
