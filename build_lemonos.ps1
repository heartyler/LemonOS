param(
    [string]$RuntimeRoot,
    [string]$JdkRoot = $env:JAVA_HOME
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not [string]::IsNullOrWhiteSpace($RuntimeRoot)) { $RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path }
if ([string]::IsNullOrWhiteSpace($JdkRoot)) {
    $javac = Get-Command javac -ErrorAction SilentlyContinue
    if ($null -eq $javac) {
        throw "JDK unavailable. Set JAVA_HOME or pass -JdkRoot."
    }
    $JdkRoot = Split-Path -Parent (Split-Path -Parent $javac.Source)
}
$JdkRoot = (Resolve-Path -LiteralPath $JdkRoot).Path

$BuildArguments = @{ JdkRoot = $JdkRoot }
if ($RuntimeRoot) { $BuildArguments.RuntimeRoot = $RuntimeRoot }
& (Join-Path $Root "build_lemonos_backend.ps1") @BuildArguments
if ($LASTEXITCODE -ne 0) { throw "Backend build failed." }
& (Join-Path $Root "tools\run_backend_config_migration_staging.ps1") @BuildArguments
if ($LASTEXITCODE -ne 0) { throw "Backend config migration staging validation failed." }
& (Join-Path $Root "build_lemonos_proxy.ps1") @BuildArguments
if ($LASTEXITCODE -ne 0) { throw "Proxy build failed." }

$backend = Join-Path $Root "build\libs\lemonos.jar"
$proxy = Join-Path $Root "build\libs\lemonos_proxy.jar"
[pscustomobject]@{
    Backend = $backend
    BackendSha256 = (Get-FileHash -LiteralPath $backend -Algorithm SHA256).Hash
    Proxy = $proxy
    ProxySha256 = (Get-FileHash -LiteralPath $proxy -Algorithm SHA256).Hash
}
