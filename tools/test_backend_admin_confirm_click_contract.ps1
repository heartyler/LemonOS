param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\admin\BackendAdminConfirmClickService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @(
    "public final class BackendAdminConfirmClickService",
    "ConfirmAction action(int clickedSlot, int confirmSlot, int cancelSlot)",
    "ConfirmAction action(int clickedSlot, int confirmSlot, int cancelSlot, boolean confirmationAvailable)",
    "enum ConfirmAction { NONE, CONFIRM, CANCEL }"
)) { if (-not $service.Contains($required)) { throw "Admin confirm contract missing: $required" } }
if ($plugin -notmatch 'private BackendAdminConfirmClickService adminConfirmClickService' -or
    ([regex]::Matches($plugin, 'this\.adminConfirmClickService\.action\(').Count -lt 10)) {
    throw "Admin confirm flows are not consolidated."
}
foreach ($removed in @("BackendAdminChunksConfirmClickService", "BackendAdminBackupConfirmClickService")) {
    if ($plugin.Contains($removed)) { throw "Redundant admin confirm service remains wired: $removed" }
}
foreach ($handler in @("handleAdminChunksConfirmClick", "handleAdminBackupConfirmClick")) {
    if ($plugin -notmatch "(?s)private void $handler.*?BackendAdminConfirmClickService\.ConfirmAction") {
        throw "Admin handler does not use the shared confirm service: $handler"
    }
}
$pagedService = Join-Path $Root "src\main\java\dev\lemonos\admin\BackendAdminPagedSelectionClickService.java"
$confirmService = Join-Path $Root "src\main\java\dev\lemonos\admin\BackendAdminConfirmClickService.java"
$harness = Join-Path $Root "tools\java\dev\lemonos\admin\BackendAdminClickServicesHarness.java"
$classes = Join-Path $Root "build\test-backend-admin-click-services"
$jdkRoot = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" }
if (Test-Path -LiteralPath $classes) { Remove-Item -LiteralPath $classes -Recurse -Force }
New-Item -ItemType Directory -Path $classes -Force | Out-Null
& (Join-Path $jdkRoot "bin\javac.exe") -encoding UTF-8 -d $classes $confirmService $pagedService $harness
if ($LASTEXITCODE -ne 0) { throw "Consolidated admin click harness compilation failed." }
& (Join-Path $jdkRoot "bin\java.exe") -cp $classes dev.lemonos.admin.BackendAdminClickServicesHarness
if ($LASTEXITCODE -ne 0) { throw "Consolidated admin click behavioral contract failed." }
Write-Host "Backend consolidated admin confirm contract OK"
