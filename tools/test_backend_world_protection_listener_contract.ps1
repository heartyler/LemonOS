param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)
$ErrorActionPreference = "Stop"
$dependencyRoot = Join-Path $Root "third_party\runtime"
$classpath = @(Get-ChildItem -LiteralPath $dependencyRoot -File -Filter "*.jar" | ForEach-Object FullName)
if ($classpath.Count -eq 0) { throw "World protection dependencies missing. Run tools\restore_test_dependencies.ps1." }
$classes = Join-Path $Root "build\world-protection-contract-classes"
if (Test-Path -LiteralPath $classes) { Remove-Item -LiteralPath $classes -Recurse -Force }
New-Item -ItemType Directory -Path $classes | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -cp ($classpath -join ";") -d $classes `
    (Join-Path $Root "src\main\java\dev\lemonos\BackendWorldPolicy.java") `
    (Join-Path $Root "src\main\java\dev\lemonos\BackendWorldProtectionListener.java") `
    (Join-Path $Root "tools\java\dev\lemonos\BackendWorldProtectionHarness.java")
if ($LASTEXITCODE -ne 0) { throw "World protection harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp ((@($classes) + $classpath) -join ";") dev.lemonos.BackendWorldProtectionHarness
if ($LASTEXITCODE -ne 0) { throw "World protection harness failed." }

$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
if (-not $plugin.Contains("registerEvents(new BackendWorldProtectionListener(this.worldPolicy)")) {
    throw "World protection listener is not registered."
}
foreach ($removedHandler in @("onCreatureSpawn", "onBlockBurn", "onBlockSpread", "onBlockFromTo", "onBlockExplode", "onEntityExplode")) {
    if ($plugin -match "public void $removedHandler\(") { throw "World protection handler remains in LemonOSPlugin: $removedHandler" }
}
foreach ($preservedHandler in @("public void onDamage(EntityDamageEvent", "public void onDamageByEntity(EntityDamageByEntityEvent")) {
    if (-not $plugin.Contains($preservedHandler)) { throw "Coupled damage handler was moved unexpectedly: $preservedHandler" }
}
Write-Host "Backend World protection listener contract OK"
