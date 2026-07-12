param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$Source = Join-Path $Root "src\main\java\dev\lemonos"
$TokenPath = Join-Path $Source "BackendOperationToken.java"
$RegistryPath = Join-Path $Source "BackendOperationRegistry.java"
$TaskSlot = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendOperationTaskSlot.java")
$StatusLease = Get-Content -Raw -LiteralPath (Join-Path $Source "BackendOperationStatusLease.java")
$HarnessPath = Join-Path $Root "tools\java\dev\lemonos\BackendOperationRegistryHarness.java"
$Classes = Join-Path $Root "build\operation-contract-classes"
New-Item -ItemType Directory -Path $Classes -Force | Out-Null

& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -d $Classes $TokenPath $RegistryPath $HarnessPath
if ($LASTEXITCODE -ne 0) { throw "Operation registry harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp $Classes dev.lemonos.BackendOperationRegistryHarness
if ($LASTEXITCODE -ne 0) { throw "Operation registry harness failed." }

foreach ($snippet in @(
    "synchronized void replace(BukkitTask next)",
    "synchronized boolean clearIfCurrent(BukkitTask expected)",
    "if (expected == null || this.task != expected) return false;",
    "expected.cancel();",
    "synchronized void cancel()"
)) {
    if (-not $TaskSlot.Contains($snippet)) { throw "Operation task slot missing: $snippet" }
}

foreach ($snippet in @(
    "implements AutoCloseable",
    "synchronized boolean publish(Component component)",
    "public synchronized void close()",
    "if (!this.open) return;",
    "this.coordinator.clear(this.participant, this.owner)"
)) {
    if (-not $StatusLease.Contains($snippet)) { throw "Operation status lease missing: $snippet" }
}

Write-Host "Backend operation primitives contract OK"
