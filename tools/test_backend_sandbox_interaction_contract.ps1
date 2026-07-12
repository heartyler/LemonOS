param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$service = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxInteractionService.java"
$harness = Join-Path $Root "tools\java\dev\lemonos\BackendSandboxInteractionHarness.java"
$classes = Join-Path $Root "build\sandbox-interaction-contract-classes"
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
if (Test-Path -LiteralPath $classes) { Remove-Item -LiteralPath $classes -Recurse -Force }
New-Item -ItemType Directory -Path $classes | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -d $classes $service $harness
if ($LASTEXITCODE -ne 0) { throw "Sandbox interaction harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp $classes dev.lemonos.BackendSandboxInteractionHarness
if ($LASTEXITCODE -ne 0) { throw "Sandbox interaction harness failed." }

foreach ($required in @(
    "private BackendSandboxInteractionService sandboxInteractionService;",
    "new BackendSandboxInteractionService()",
    "this.sandboxInteractionService.action(n)",
    "this.sandboxInteractionService.action(buttonSpec.slot())",
    "this.sandboxInteractionService.confirmAction(slot, 14, 12)",
    "this.sandboxInteractionService.confirmAction("
)) {
    if (-not $plugin.Contains($required)) { throw "Sandbox interaction wiring missing: $required" }
}
foreach ($removed in @("BackendSandboxClickService", "BackendSandboxConfirmClickService")) {
    if ($plugin.Contains($removed)) { throw "Removed Sandbox interaction service remains wired: $removed" }
}
Write-Host "Backend Sandbox interaction contract OK"
