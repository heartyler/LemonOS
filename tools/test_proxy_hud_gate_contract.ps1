param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$Classes = Join-Path $Root "build\test-proxy-hud-gate"
$VelocityCandidates = @(
    (Join-Path $Root "third_party\runtime\velocity.jar"),
    (Join-Path (Split-Path -Parent $Root) "third_party\runtime\velocity.jar")
)
if ($env:LEMONOS_RUNTIME_ROOT) { $VelocityCandidates += Join-Path $env:LEMONOS_RUNTIME_ROOT "velocity\velocity.jar" }
$VelocityJar = $VelocityCandidates | Where-Object { Test-Path -LiteralPath $_ -PathType Leaf } | Select-Object -First 1
if (-not $VelocityJar) { throw "Velocity test dependency missing. Run tools\restore_test_dependencies.ps1." }
$Harness = Join-Path $Root "tools\java\dev\lemonos\proxy\ProxyHudGateHarness.java"
$GeneratedVersionSource = Join-Path $Classes "generated\dev\lemonos\common\LemonOSBuildVersion.java"
$Sources = @(
    $GeneratedVersionSource,
    (Join-Path $Root "src_proxy\dev\lemonos\common\LemonOS.java"),
    (Join-Path $Root "src_proxy\dev\lemonos\common\AdminProtocol.java"),
    (Join-Path $Root "src_proxy\dev\lemonos\proxy\AccessRepository.java"),
    (Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaceStatusRepository.java"),
    (Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaytimeRepository.java"),
    (Join-Path $Root "src_proxy\dev\lemonos\proxy\runtime\ProxyRuntimeLayout.java"),
    (Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyLifecycleService.java"),
    $Harness
)

if (Test-Path -LiteralPath $Classes) { Remove-Item -LiteralPath $Classes -Recurse -Force }
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
& (Join-Path $Root "tools\write_version_source.ps1") -Root $Root -OutputPath $GeneratedVersionSource | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -cp $VelocityJar -d $Classes $Sources
if ($LASTEXITCODE -ne 0) { throw "Proxy HUD gate harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp "$Classes;$VelocityJar" dev.lemonos.proxy.ProxyHudGateHarness
if ($LASTEXITCODE -ne 0) { throw "Proxy HUD gate behavioral contract failed." }
Write-Host "Proxy HUD canonical gate contract passed."
