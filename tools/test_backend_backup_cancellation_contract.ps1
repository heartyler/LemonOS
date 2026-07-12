param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$Source = Join-Path $Root "src\main\java\dev\lemonos"
$Classes = Join-Path $Root "build\backup-cancellation-contract-classes"
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -d $Classes `
    (Join-Path $Source "BackendOperationCancellation.java") `
    (Join-Path $Source "BackendBackupOperationService.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendBackupCancellationHarness.java")
if ($LASTEXITCODE -ne 0) { throw "Backup cancellation harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp $Classes dev.lemonos.BackendBackupCancellationHarness
if ($LASTEXITCODE -ne 0) { throw "Backup cancellation harness failed." }
Write-Host "Backend backup cancellation contract OK"
