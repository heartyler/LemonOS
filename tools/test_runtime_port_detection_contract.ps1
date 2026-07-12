param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$fixture = Join-Path $Root "build\runtime-port-contract"
if (Test-Path -LiteralPath $fixture) { Remove-Item -LiteralPath $fixture -Recurse -Force }
New-Item -ItemType Directory -Path $fixture -Force | Out-Null
try {
    @"
servers:
  velocity:
    port: 25575
  lobby:
    port: 30036
    rcon_port: 31036
  duplicate:
    port: 30036
"@ | Set-Content -LiteralPath (Join-Path $fixture "honey.yml") -Encoding ASCII
    $ports = @(& (Join-Path $Root "tools\get_runtime_ports.ps1") -RuntimeRoot $fixture)
    $expected = @(25575, 30036, 31036)
    if (($ports -join ',') -ne ($expected -join ',')) {
        throw "Runtime ports were not parsed and deduplicated correctly: $($ports -join ',')"
    }
    Set-Content -LiteralPath (Join-Path $fixture "honey.yml") -Value "servers: {}" -Encoding ASCII
    $rejected = $false
    try { & (Join-Path $Root "tools\get_runtime_ports.ps1") -RuntimeRoot $fixture | Out-Null } catch {
        $rejected = $_.Exception.Message -like "*contains no service ports*"
    }
    if (-not $rejected) { throw "Empty runtime port configuration was accepted." }
    Write-Host "Runtime port detection contract OK"
} finally {
    Remove-Item -LiteralPath $fixture -Recurse -Force -ErrorAction SilentlyContinue
}
