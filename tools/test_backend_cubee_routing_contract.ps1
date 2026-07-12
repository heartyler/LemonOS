param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$service = Join-Path $Root "src\main\java\dev\lemonos\BackendCubeeRoutingService.java"
$navigation = Join-Path $Root "src\main\java\dev\lemonos\BackendCubeeNavigationService.java"
$harness = Join-Path $Root "tools\java\dev\lemonos\BackendCubeeRoutingHarness.java"
$classes = Join-Path $Root "build\cubee-routing-contract-classes"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
if (Test-Path -LiteralPath $classes) { Remove-Item -LiteralPath $classes -Recurse -Force }
New-Item -ItemType Directory -Path $classes | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -d $classes $navigation $service $harness
if ($LASTEXITCODE -ne 0) { throw "Cubee routing harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp $classes dev.lemonos.BackendCubeeRoutingHarness
if ($LASTEXITCODE -ne 0) { throw "Cubee routing harness failed." }

foreach ($required in @(
    "private BackendCubeeRoutingService cubeeRoutingService;",
    "new BackendCubeeRoutingService()",
    "this.cubeeRoutingService.homeAction(",
    "this.cubeeRoutingService.rememberedSurfacePlan(",
    "this.cubeeRoutingService.defaultLaunchPlan(",
    "BackendCubeeRoutingService.LaunchPlan launchPlan"
)) {
    if (-not $plugin.Contains($required)) { throw "Cubee routing wiring missing: $required" }
}
foreach ($removed in @("BackendCubeeClickService", "BackendCubeeLaunchService")) {
    if ($plugin.Contains($removed)) { throw "Removed Cubee routing service remains wired: $removed" }
}
Write-Host "Backend Cubee routing contract OK"
