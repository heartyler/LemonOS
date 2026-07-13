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
$Dependencies = @(Get-ChildItem -LiteralPath (Join-Path $Root "third_party\runtime") -Filter "*.jar" | ForEach-Object FullName)
if ($Dependencies.Count -eq 0) { throw "Runtime test dependencies are missing." }
$Classpath = $Dependencies -join ";"
$Classes = Join-Path $Root "build\recipe-book-contract-classes"
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
& $Javac -encoding UTF-8 -cp $Classpath -d $Classes `
    (Join-Path $Root "src\main\java\dev\lemonos\BackendRecipeBookConfig.java") `
    (Join-Path $Root "src\main\java\dev\lemonos\BackendRecipeUnlockService.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendRecipeBookHarness.java")
if ($LASTEXITCODE -ne 0) { throw "Backend Recipe Book harness compilation failed." }
& $Java -cp "$Classes;$Classpath" dev.lemonos.BackendRecipeBookHarness
if ($LASTEXITCODE -ne 0) { throw "Backend Recipe Book harness failed." }
Write-Host "Backend Recipe Book behavior contract OK"
