param(
    [Parameter(Mandatory = $true)][string]$RuntimeRoot
)

$ErrorActionPreference = "Stop"
$RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path
$configRoot = Join-Path $RuntimeRoot "LemonOS"
$dataRoot = Join-Path $RuntimeRoot "lemonos-data"
$expectedConfig = @("config.yml", "messages.yml", "places.yml", "sandbox.yml", "survival.yml", "hud.yml", "atmosphere.yml", "recipes.yml")
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
$hud = Get-Content -Raw -LiteralPath (Join-Path $configRoot "hud.yml")
$recipes = Get-Content -Raw -LiteralPath (Join-Path $configRoot "recipes.yml")
$survival = Get-Content -Raw -LiteralPath (Join-Path $configRoot "survival.yml")
$access = Get-Content -Raw -LiteralPath (Join-Path $dataRoot "access.yml")
if ($config -match '(?m)^(stayed-close|hud|atmosphere):\s*$') {
    throw "Core config contains a legacy feature root."
}
if ($hud -notmatch '(?ms)^hud:\s+  stayed-close:\s+    enabled:\s*(?:true|false)') {
    throw "hud.yml does not own hud.stayed-close.enabled."
}
if (Test-Path -LiteralPath (Join-Path $configRoot "boards.yml") -PathType Leaf) {
    throw "Legacy boards.yml remains after HUD config migration."
}
if ($recipes -notmatch '(?ms)^recipe-book:\s+  unlock-all:\s+    survival:\s*(?:true|false)\s+    creative:\s*(?:true|false)') {
    throw "recipes.yml does not own Survival and Creative Recipe Book policy."
}
if ($survival -match '(?m)^  recipe-book:\s*$') {
    throw "survival.yml still owns the legacy Recipe Book policy."
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
