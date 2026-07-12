param(
    [Parameter(Mandatory = $true)][string]$RuntimeRoot
)

$ErrorActionPreference = "Stop"
$RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path
$configRoot = Join-Path $RuntimeRoot "LemonOS"
$dataRoot = Join-Path $RuntimeRoot "lemonos-data"
$expectedConfig = @("config.yml", "messages.yml", "places.yml", "sandbox.yml", "survival.yml", "boards.yml", "atmosphere.yml")
$expectedData = @("access.yml", "backup.yml", "chunks.yml", "hud.yml", "identity.yml", "online.yml", "places.yml", "playtime.yml", "skins.yml")

foreach ($name in $expectedConfig) {
    if (-not (Test-Path -LiteralPath (Join-Path $configRoot $name) -PathType Leaf)) {
        throw "Missing LemonOS config: $name"
    }
}
foreach ($name in $expectedData) {
    if (-not (Test-Path -LiteralPath (Join-Path $dataRoot $name) -PathType Leaf)) {
        throw "Missing LemonOS data schema: $name"
    }
}

$config = Get-Content -Raw -LiteralPath (Join-Path $configRoot "config.yml")
$boards = Get-Content -Raw -LiteralPath (Join-Path $configRoot "boards.yml")
$access = Get-Content -Raw -LiteralPath (Join-Path $dataRoot "access.yml")
if ($config -match '(?m)^(stayed-close|hud|atmosphere):\s*$') {
    throw "Core config contains a legacy feature root."
}
if ($boards -notmatch '(?ms)^boards:\s+  stayed-close:\s+    enabled:\s*(?:true|false)') {
    throw "boards.yml does not own boards.stayed-close.enabled."
}
if ($access -notmatch '(?m)^version:\s*["'']?3\.0["'']?\s*$') {
    throw "access.yml is not schema 3.0."
}

[pscustomobject]@{
    RuntimeRoot = $RuntimeRoot
    ConfigFiles = $expectedConfig.Count
    DataFiles = $expectedData.Count
    AccessSchema = "3.0"
    LegacyCoreFeatureRoots = $false
}
