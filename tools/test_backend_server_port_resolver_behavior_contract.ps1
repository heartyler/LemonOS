param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$Javac = Join-Path $JdkRoot "bin\javac.exe"
$Java = Join-Path $JdkRoot "bin\java.exe"
foreach ($executable in @($Javac, $Java)) {
    if (-not (Test-Path -LiteralPath $executable -PathType Leaf)) { throw "JDK executable is missing: $executable" }
}
$Classes = Join-Path $Root "build\server-port-resolver-contract-classes"
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
& $Javac -encoding UTF-8 -d $Classes `
    (Join-Path $Root "src\main\java\dev\lemonos\BackendServerPortResolver.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendServerPortResolverHarness.java")
if ($LASTEXITCODE -ne 0) { throw "Backend server port resolver harness compilation failed." }
& $Java -cp $Classes dev.lemonos.BackendServerPortResolverHarness
if ($LASTEXITCODE -ne 0) { throw "Backend server port resolver harness failed." }
Write-Host "Backend server port resolver behavior contract OK"
