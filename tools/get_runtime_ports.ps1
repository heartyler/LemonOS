param([Parameter(Mandatory = $true)][string]$RuntimeRoot)
$ErrorActionPreference = "Stop"
$RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path
$configPath = Join-Path $RuntimeRoot "honey.yml"
if (-not (Test-Path -LiteralPath $configPath -PathType Leaf)) {
    throw "Runtime port configuration is missing: $configPath"
}
$config = Get-Content -Raw -LiteralPath $configPath
$ports = @([regex]::Matches($config, '(?m)^\s+(?:port|rcon_port):\s*(\d+)\s*$') |
    ForEach-Object { [int]$_.Groups[1].Value } |
    Sort-Object -Unique)
if ($ports.Count -eq 0) {
    throw "Runtime port configuration contains no service ports: $configPath"
}
$ports
