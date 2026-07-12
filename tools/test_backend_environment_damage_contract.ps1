param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$classpath = @(Get-ChildItem (Join-Path $Root "third_party\runtime") -File -Filter "*.jar" | ForEach-Object FullName)
$classes = Join-Path $Root "build\environment-damage-contract-classes"
if (Test-Path -LiteralPath $classes) { Remove-Item -LiteralPath $classes -Recurse -Force }
New-Item -ItemType Directory -Path $classes | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -cp ($classpath -join ";") -d $classes `
    (Join-Path $Root "src\main\java\dev\lemonos\BackendEnvironmentDamageService.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendEnvironmentDamageHarness.java")
if ($LASTEXITCODE -ne 0) { throw "Environment damage harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp ((@($classes) + $classpath) -join ";") dev.lemonos.BackendEnvironmentDamageHarness
if ($LASTEXITCODE -ne 0) { throw "Environment damage harness failed." }
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @("new BackendEnvironmentDamageService()", "event instanceof EntityDamageByBlockEvent", "blockEvent.getDamager().getType()")) {
    if (-not $plugin.Contains($required)) { throw "Environment damage wiring missing: $required" }
}
if ($plugin.Contains("DamageCause.HOT_FLOOR")) { throw "Deprecated HOT_FLOOR handling remains." }
Write-Host "Backend Environment damage contract OK"
