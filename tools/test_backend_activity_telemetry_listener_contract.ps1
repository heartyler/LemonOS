param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$dependencyRoot = Join-Path $Root "third_party\runtime"
$classpath = @(Get-ChildItem -LiteralPath $dependencyRoot -File -Filter "*.jar" | ForEach-Object FullName)
if ($classpath.Count -eq 0) { throw "Activity telemetry dependencies missing. Run tools\restore_test_dependencies.ps1." }
$classes = Join-Path $Root "build\activity-telemetry-contract-classes"
if (Test-Path -LiteralPath $classes) { Remove-Item -LiteralPath $classes -Recurse -Force }
New-Item -ItemType Directory -Path $classes | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -cp ($classpath -join ";") -d $classes `
    (Join-Path $Root "src\main\java\dev\lemonos\BackendActivityTelemetryListener.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendActivityTelemetryHarness.java")
if ($LASTEXITCODE -ne 0) { throw "Activity telemetry harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp ((@($classes) + $classpath) -join ";") dev.lemonos.BackendActivityTelemetryHarness
if ($LASTEXITCODE -ne 0) { throw "Activity telemetry harness failed." }

$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    "registerEvents(new BackendActivityTelemetryListener(",
    "this.chainBreaks::contains, this::monotonicMillis, this::recordActivity",
    "public void onSurvivalBlockPlace(BlockPlaceEvent",
    "public void onSurvivalItemBreak(PlayerItemBreakEvent"
)) {
    if (-not $plugin.Contains($required)) { throw "Activity telemetry wiring contract missing: $required" }
}
foreach ($removed in @("onActivityBlockBreak", "onActivityBlockPlace", "onActivityPickup", "onActivityCraft")) {
    if ($plugin -match "public void $removed\(") { throw "Activity telemetry handler remains in LemonOSPlugin: $removed" }
}
Write-Host "Backend Activity telemetry listener contract OK"
