param(
    [string]$RuntimeRoot,
    [string]$JdkRoot
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not [string]::IsNullOrWhiteSpace($RuntimeRoot)) { $RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path }
if (-not [string]::IsNullOrWhiteSpace($JdkRoot)) {
    $JdkRoot = (Resolve-Path -LiteralPath $JdkRoot).Path
    if (-not (Test-Path -LiteralPath (Join-Path $JdkRoot "bin\javac.exe") -PathType Leaf) -or
        -not (Test-Path -LiteralPath (Join-Path $JdkRoot "bin\jar.exe") -PathType Leaf)) {
        throw "JdkRoot does not contain javac.exe and jar.exe: $JdkRoot"
    }
} else {
    $candidates = New-Object System.Collections.Generic.List[string]
    if (-not [string]::IsNullOrWhiteSpace($env:JAVA_HOME)) { $candidates.Add($env:JAVA_HOME) }
    $javac = Get-Command javac -ErrorAction SilentlyContinue
    if ($null -ne $javac) { $candidates.Add((Split-Path -Parent (Split-Path -Parent $javac.Source))) }
    foreach ($parent in @(
        (Join-Path $env:ProgramFiles "Java"),
        (Join-Path $env:ProgramFiles "Eclipse Adoptium")
    )) {
        if (Test-Path -LiteralPath $parent -PathType Container) {
            Get-ChildItem -LiteralPath $parent -Directory -Filter "jdk*" |
                Sort-Object Name -Descending |
                ForEach-Object { $candidates.Add($_.FullName) }
        }
    }
    $resolvedJdkRoots = @($candidates | Where-Object {
        (Test-Path -LiteralPath (Join-Path $_ "bin\javac.exe") -PathType Leaf) -and
        (Test-Path -LiteralPath (Join-Path $_ "bin\jar.exe") -PathType Leaf)
    } | Select-Object -First 1)
    if ($resolvedJdkRoots.Count -eq 0) { throw "JDK unavailable. Set JAVA_HOME or pass -JdkRoot." }
    $JdkRoot = (Resolve-Path -LiteralPath $resolvedJdkRoots[0]).Path
}

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
