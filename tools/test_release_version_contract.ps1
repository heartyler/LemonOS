param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$version = (Get-Content -Raw -LiteralPath (Join-Path $Root "VERSION")).Trim()
if ($version -notmatch '^\d+\.\d+\.\d+(?:-[0-9A-Za-z.-]+)?$') {
    throw "VERSION is not a supported semantic version: $version"
}
$backendDescriptor = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\resources\plugin.yml")
$proxyDescriptor = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\velocity-plugin.json")
$proxyConstants = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\dev\lemonos\common\LemonOS.java")
if (-not $backendDescriptor.Contains('${LEMONOS_VERSION}')) { throw "Paper descriptor does not use the version token." }
if (-not $proxyDescriptor.Contains('${LEMONOS_VERSION}')) { throw "Velocity descriptor does not use the version token." }
if (-not $proxyConstants.Contains('LemonOSBuildVersion.VERSION')) { throw "Proxy Java metadata does not use the generated version." }
$versionWriter = Get-Content -Raw -LiteralPath (Join-Path $Root "tools\write_version_source.ps1")
if (-not $versionWriter.Contains('Get-Content -Raw -LiteralPath (Join-Path $Root "VERSION")')) {
    throw "Generated Java version does not originate from VERSION."
}
Write-Host "LemonOS release version contract OK: $version"
