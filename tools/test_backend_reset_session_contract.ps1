param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$RuntimeRoot,
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$Classes = Join-Path $Root "build\reset-session-contract-classes"
if (Test-Path -LiteralPath $Classes) {
    Remove-Item -LiteralPath $Classes -Recurse -Force
}
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
if ([string]::IsNullOrWhiteSpace($RuntimeRoot)) {
    $DependencyRoot = Join-Path $Root "third_party\runtime"
    $cp = @(Get-ChildItem $DependencyRoot -Filter "*.jar" | ForEach-Object FullName)
} else {
    $cp = @(Get-ChildItem (Join-Path $RuntimeRoot "lobby\libraries") -Recurse -Filter "*.jar" | ForEach-Object FullName)
    $cp += Get-ChildItem (Join-Path $RuntimeRoot "lobby\plugins") -Recurse -Filter "*.jar" | ForEach-Object FullName
}
if ($cp.Count -eq 0) { throw "No reset-session contract dependencies found. Run tools\restore_test_dependencies.ps1 first." }
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -cp ($cp -join ";") -d $Classes `
    (Join-Path $Root "src\main\java\dev\lemonos\BackendIdentityResetService.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendResetSessionHarness.java")
if ($LASTEXITCODE -ne 0) { throw "Reset session harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp ((@($Classes) + $cp) -join ";") dev.lemonos.BackendResetSessionHarness
if ($LASTEXITCODE -ne 0) { throw "Reset session harness failed." }
Write-Host "Backend Reset session contract OK"
