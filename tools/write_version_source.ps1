param(
    [string]$Root = (Split-Path -Parent $PSScriptRoot),
    [Parameter(Mandatory = $true)][string]$OutputPath
)
$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$version = (Get-Content -Raw -LiteralPath (Join-Path $Root "VERSION")).Trim()
if ($version -notmatch '^\d+\.\d+\.\d+(?:-[0-9A-Za-z.-]+)?$') {
    throw "VERSION is not a supported semantic version: $version"
}
$OutputPath = [System.IO.Path]::GetFullPath($OutputPath)
New-Item -ItemType Directory -Path (Split-Path -Parent $OutputPath) -Force | Out-Null
@"
package dev.lemonos.common;

public final class LemonOSBuildVersion {
    public static final String VERSION = "$version";

    private LemonOSBuildVersion() {
    }
}
"@ | Set-Content -LiteralPath $OutputPath -Encoding ASCII
$OutputPath
