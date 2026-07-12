param(
    [string]$ManifestPath = (Join-Path (Split-Path -Parent $PSScriptRoot) "third_party\runtime-artifacts.json")
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$ManifestPath = (Resolve-Path -LiteralPath $ManifestPath).Path
$Manifest = Get-Content -Raw -LiteralPath $ManifestPath | ConvertFrom-Json
$TargetRoot = Join-Path $Root "third_party\runtime"
New-Item -ItemType Directory -Path $TargetRoot -Force | Out-Null

foreach ($product in @("Paper API", "Velocity", "Floodgate Spigot", "JOML", "BungeeCord Chat")) {
    $artifact = @($Manifest.artifacts | Where-Object product -eq $product)
    if ($artifact.Count -ne 1) { throw "Pinned artifact is missing or ambiguous: $product" }
    $target = Join-Path $TargetRoot $artifact[0].runtimeFile
    if (Test-Path -LiteralPath $target -PathType Leaf) {
        $currentHash = (Get-FileHash -LiteralPath $target -Algorithm SHA256).Hash
        if ($currentHash -eq [string]$artifact[0].sha256) {
            Write-Host "$product dependency already verified."
            continue
        }
        Remove-Item -LiteralPath $target -Force
    }
    $temporary = "$target.download-$([Guid]::NewGuid().ToString('N')).tmp"
    try {
        Invoke-WebRequest -Uri $artifact[0].url -OutFile $temporary -UseBasicParsing
        $downloadHash = (Get-FileHash -LiteralPath $temporary -Algorithm SHA256).Hash
        if ($downloadHash -ne [string]$artifact[0].sha256) {
            throw "$product dependency hash mismatch."
        }
        Move-Item -LiteralPath $temporary -Destination $target -Force
    }
    finally {
        Remove-Item -LiteralPath $temporary -Force -ErrorAction SilentlyContinue
    }
    Write-Host "$product dependency restored and verified."
}
