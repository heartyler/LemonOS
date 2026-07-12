param(
    [Parameter(Mandatory = $true)][string]$Root
)

$ErrorActionPreference = "Stop"
$Root = [System.IO.Path]::GetFullPath($Root)

function Get-Sha256 {
    param([string]$Path)
    return (Get-FileHash -LiteralPath $Path -Algorithm SHA256).Hash
}

$files = @()
foreach ($relative in @("constitution", "src", "src_proxy", "templates", "release")) {
    $path = Join-Path $Root $relative
    if (Test-Path -LiteralPath $path) {
        $files += Get-ChildItem -LiteralPath $path -Recurse -File
    }
}
$files += Get-ChildItem -LiteralPath $Root -File -Filter "*.ps1"
$toolsPath = Join-Path $Root "tools"
if (Test-Path -LiteralPath $toolsPath) {
    $files += Get-ChildItem -LiteralPath $toolsPath -Recurse -File | Where-Object Extension -NE ".jar"
}

$paths = [string[]]@($files | ForEach-Object FullName)
[System.Array]::Sort($paths, [System.StringComparer]::OrdinalIgnoreCase)
$lines = $paths | ForEach-Object {
    $relative = $_.Substring($Root.Length).TrimStart("\").Replace("\", "/")
    "$relative=$(Get-Sha256 $_)"
}

$bytes = [System.Text.Encoding]::UTF8.GetBytes(($lines -join "`n"))
$sha = [System.Security.Cryptography.SHA256]::Create()
try {
    ([System.BitConverter]::ToString($sha.ComputeHash($bytes))).Replace("-", "")
} finally {
    $sha.Dispose()
}
