param(
    [Parameter(Mandatory = $true)][string]$RuntimeRoot,
    [switch]$ResetConfig,
    [switch]$ResetData,
    [switch]$Plan
)

$ErrorActionPreference = "Stop"
$LemonOsRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path
$manifestPath = Join-Path $RuntimeRoot "deployment.json"

if (-not $ResetConfig -and -not $ResetData) {
    throw "Choose -ResetConfig, -ResetData, or both."
}
if (-not (Test-Path -LiteralPath $manifestPath -PathType Leaf)) {
    throw "Refusing reset: deployment.json is missing from runtime root."
}
$manifest = Get-Content -Raw -LiteralPath $manifestPath | ConvertFrom-Json
if ([string]$manifest.product -ne "Honey") {
    throw "Refusing reset: deployment product is not Honey."
}
foreach ($required in @("velocity", "lobby", "survival", "creative")) {
    if (-not (Test-Path -LiteralPath (Join-Path $RuntimeRoot $required) -PathType Container)) {
        throw "Refusing reset: runtime component missing: $required"
    }
}

$ports = @(& (Join-Path $PSScriptRoot "get_runtime_ports.ps1") -RuntimeRoot $RuntimeRoot)
$listeners = @(Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue | Where-Object LocalPort -In $ports)
if ($listeners.Count -gt 0) {
    throw "Refusing reset while Honey runtime ports are active."
}

function Assert-ResetTarget([string]$Name) {
    $candidate = [System.IO.Path]::GetFullPath((Join-Path $RuntimeRoot $Name))
    $expected = [System.IO.Path]::GetFullPath((Join-Path $RuntimeRoot $Name))
    if ($candidate -ne $expected -or -not $candidate.StartsWith($RuntimeRoot + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Reset target escapes runtime root: $candidate"
    }
    return $candidate
}

$operations = New-Object System.Collections.Generic.List[string]
function Reset-Area([string]$Name) {
    $target = Assert-ResetTarget $Name
    $source = Join-Path $LemonOsRoot "templates\runtime\$Name"
    if (-not (Test-Path -LiteralPath $source -PathType Container)) {
        throw "Canonical runtime defaults missing: $source"
    }
    $script:operations.Add("reset $target from $source")
    if ($Plan) {
        return
    }
    if (Test-Path -LiteralPath $target) {
        Remove-Item -LiteralPath $target -Recurse -Force
    }
    New-Item -ItemType Directory -Path $target -Force | Out-Null
    Copy-Item -Path (Join-Path $source "*") -Destination $target -Recurse -Force
}

if ($ResetConfig) { Reset-Area "LemonOS" }
if ($ResetData) { Reset-Area "lemonos-data" }

[pscustomobject]@{
    RuntimeRoot = $RuntimeRoot
    Mode = if ($Plan) { "plan" } else { "applied" }
    Operations = @($operations)
}
