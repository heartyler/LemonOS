param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$RuntimeRoot = "C:\Users\heartylr\Desktop\Honey\honey-26.2",
    [string]$JdkRoot = "C:\Program Files\Java\jdk-26.0.1"
)
$ErrorActionPreference = "Stop"
$Classes = Join-Path $Root "build\reset-session-contract-classes"
if (Test-Path -LiteralPath $Classes) {
    Remove-Item -LiteralPath $Classes -Recurse -Force
}
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
$cp = @(Get-ChildItem (Join-Path $RuntimeRoot "lobby\libraries") -Recurse -Filter "*.jar" | ForEach-Object FullName)
$cp += Get-ChildItem (Join-Path $RuntimeRoot "lobby\plugins") -Recurse -Filter "*.jar" | ForEach-Object FullName
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -cp ($cp -join ";") -d $Classes `
    (Join-Path $Root "src\main\java\dev\lemonos\BackendIdentityResetService.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendResetSessionHarness.java")
if ($LASTEXITCODE -ne 0) { throw "Reset session harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp ((@($Classes) + $cp) -join ";") dev.lemonos.BackendResetSessionHarness
if ($LASTEXITCODE -ne 0) { throw "Reset session harness failed." }
Write-Host "Backend Reset session contract OK"
