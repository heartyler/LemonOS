param(
    [string]$Root = (Split-Path -Parent $PSScriptRoot),
    [string]$RuntimeRoot,
    [switch]$KeepRuntime
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$ownsRuntime = [string]::IsNullOrWhiteSpace($RuntimeRoot)
if ($ownsRuntime) {
    $RuntimeRoot = Join-Path $Root "build\runtime-integration-$([Guid]::NewGuid().ToString('N'))"
}
$RuntimeRoot = [System.IO.Path]::GetFullPath($RuntimeRoot)

$initialize = Join-Path $Root "tools\initialize_lemonos_runtime.ps1"
$deploy = Join-Path $Root "tools\deploy_lemonos_artifacts.ps1"
$verify = Join-Path $Root "tools\verify_lemonos_runtime.ps1"
$backend = Join-Path $Root "build\libs\lemonos.jar"
$proxy = Join-Path $Root "build\libs\lemonos_proxy.jar"
foreach ($required in @($initialize, $deploy, $verify, $backend, $proxy)) {
    if (-not (Test-Path -LiteralPath $required -PathType Leaf)) {
        throw "Runtime integration prerequisite missing: $required"
    }
}

function Assert-Equal([object]$Actual, [object]$Expected, [string]$Message) {
    if ($Actual -ne $Expected) {
        throw "$Message Expected '$Expected', received '$Actual'."
    }
}

# Keep this disposable test independent from any real Honey runtime that may be
# running on the developer machine. Production scripts retain their port guard.
function Get-NetTCPConnection {
    param([string]$State, [object]$ErrorAction)
    return @()
}

try {
    New-Item -ItemType Directory -Path $RuntimeRoot -Force | Out-Null
    foreach ($component in @("velocity", "lobby", "survival", "creative")) {
        New-Item -ItemType Directory -Path (Join-Path $RuntimeRoot "$component\plugins") -Force | Out-Null
    }
    $manifestPath = Join-Path $RuntimeRoot "deployment.json"
    @"
servers:
  velocity:
    port: 25575
  lobby:
    port: 30036
    rcon_port: 31036
  survival:
    port: 30037
    rcon_port: 31037
  creative:
    port: 30038
    rcon_port: 31038
"@ | Set-Content -LiteralPath (Join-Path $RuntimeRoot "honey.yml") -Encoding ASCII
    @{
        product = "Honey"
        artifacts = @{
            lemonos = ""
            lemonosProxy = ""
        }
    } | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $manifestPath -Encoding UTF8

    $oldBackend = [System.Text.Encoding]::UTF8.GetBytes("previous-backend")
    $oldProxy = [System.Text.Encoding]::UTF8.GetBytes("previous-proxy")
    [System.IO.File]::WriteAllBytes((Join-Path $RuntimeRoot "lobby\plugins\lemonos.jar"), $oldBackend)
    [System.IO.File]::WriteAllBytes((Join-Path $RuntimeRoot "velocity\plugins\lemonos_proxy.jar"), $oldProxy)

    $reset = & $initialize -RuntimeRoot $RuntimeRoot -ResetConfig -ResetData
    Assert-Equal $reset.Mode "applied" "Runtime reset mode mismatch."
    Assert-Equal @($reset.Operations).Count 2 "Runtime reset operation count mismatch."

    $verification = & $verify -RuntimeRoot $RuntimeRoot
    Assert-Equal $verification.ConfigFiles 8 "Runtime config file count mismatch."
    Assert-Equal $verification.DataFiles 9 "Runtime data file count mismatch."
    Assert-Equal $verification.AccessSchema "3.0" "Runtime access schema mismatch."

    $deployment = & $deploy -RuntimeRoot $RuntimeRoot
    $backendHash = (Get-FileHash -LiteralPath $backend -Algorithm SHA256).Hash
    $proxyHash = (Get-FileHash -LiteralPath $proxy -Algorithm SHA256).Hash
    foreach ($server in @("lobby", "survival", "creative")) {
        Assert-Equal (Get-FileHash -LiteralPath (Join-Path $RuntimeRoot "$server\plugins\lemonos.jar") -Algorithm SHA256).Hash `
            $backendHash "Backend artifact hash mismatch for $server."
    }
    Assert-Equal (Get-FileHash -LiteralPath (Join-Path $RuntimeRoot "velocity\plugins\lemonos_proxy.jar") -Algorithm SHA256).Hash `
        $proxyHash "Proxy artifact hash mismatch."

    $deployedManifest = Get-Content -Raw -LiteralPath $manifestPath | ConvertFrom-Json
    Assert-Equal $deployedManifest.lemonosDeployment "artifact-deploy" "Deployment mode was not recorded."
    Assert-Equal $deployedManifest.artifacts.lemonos $backendHash "Backend manifest hash mismatch."
    Assert-Equal $deployedManifest.artifacts.lemonosProxy $proxyHash "Proxy manifest hash mismatch."
    if ([string]::IsNullOrWhiteSpace([string]$deployedManifest.sourceSnapshotSha256)) {
        throw "Deployment source snapshot hash was not recorded."
    }

    $backupBackend = Join-Path $deployment.BackupRoot "lobby\plugins\lemonos.jar"
    $backupProxy = Join-Path $deployment.BackupRoot "velocity\plugins\lemonos_proxy.jar"
    Assert-Equal ([System.Text.Encoding]::UTF8.GetString([System.IO.File]::ReadAllBytes($backupBackend))) `
        "previous-backend" "Previous backend artifact was not backed up."
    Assert-Equal ([System.Text.Encoding]::UTF8.GetString([System.IO.File]::ReadAllBytes($backupProxy))) `
        "previous-proxy" "Previous proxy artifact was not backed up."

    $deployedManifest.product = "NotHoney"
    $deployedManifest | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath $manifestPath -Encoding UTF8
    $rejected = $false
    try {
        & $initialize -RuntimeRoot $RuntimeRoot -ResetConfig -Plan | Out-Null
    } catch {
        $rejected = $_.Exception.Message -like "*deployment product is not Honey*"
    }
    if (-not $rejected) { throw "Runtime reset accepted a non-Honey deployment manifest." }

    Write-Host "LemonOS runtime integration tests passed."
} finally {
    if ($ownsRuntime -and -not $KeepRuntime -and (Test-Path -LiteralPath $RuntimeRoot)) {
        Remove-Item -LiteralPath $RuntimeRoot -Recurse -Force
    }
}
