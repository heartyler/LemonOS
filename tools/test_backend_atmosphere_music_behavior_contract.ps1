param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = "C:\Program Files\Java\jdk-26.0.1"
)
$ErrorActionPreference = "Stop"
$Source = Join-Path $Root "src\main\java\dev\lemonos"
$Classes = Join-Path $Root "build\atmosphere-music-contract-classes"
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -d $Classes `
    (Join-Path $Source "BackendAtmosphereMusicService.java") `
    (Join-Path $Source "BackendAtmosphereMusicOrchestrationService.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendAtmosphereMusicHarness.java")
if ($LASTEXITCODE -ne 0) { throw "Atmosphere music behavior harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp $Classes dev.lemonos.BackendAtmosphereMusicHarness
if ($LASTEXITCODE -ne 0) { throw "Atmosphere music behavior harness failed." }
Write-Host "Backend Atmosphere music behavior contract OK"
