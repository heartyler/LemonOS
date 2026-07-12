param(
    [Parameter(Mandatory = $true)][string]$RuntimeRoot,
    [string]$JdkRoot = $env:JAVA_HOME
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path
if ([string]::IsNullOrWhiteSpace($JdkRoot)) { throw "JdkRoot is required." }
$Classes = Join-Path $Root "build\classes"
if (-not (Test-Path -LiteralPath (Join-Path $Classes "dev\lemonos\BackendConfigMigrationService.class"))) {
    throw "Backend classes must be built before the migration staging harness."
}
$HarnessClasses = Join-Path $Root "build\test-config-migration"
if (Test-Path -LiteralPath $HarnessClasses) { Remove-Item -LiteralPath $HarnessClasses -Recurse -Force }
New-Item -ItemType Directory -Path $HarnessClasses -Force | Out-Null
$Classpath = @($Classes)
$Classpath += Get-ChildItem -Path (Join-Path $RuntimeRoot "lobby\libraries") -Recurse -Filter "*.jar" | ForEach-Object FullName
$Classpath += Get-ChildItem -Path (Join-Path $RuntimeRoot "lobby\plugins") -Recurse -Filter "*.jar" | Where-Object Name -NotLike "lemonos*.jar" | ForEach-Object FullName
$Classpath += Get-ChildItem -Path (Join-Path $RuntimeRoot "velocity\plugins") -Recurse -Filter "*.jar" | Where-Object Name -NotLike "lemonos*.jar" | ForEach-Object FullName
$ClasspathText = $Classpath -join ";"
$Harness = Join-Path $Root "tools\java\dev\lemonos\BackendConfigMigrationStagingHarness.java"
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -cp $ClasspathText -d $HarnessClasses $Harness
if ($LASTEXITCODE -ne 0) { throw "Config migration staging harness compilation failed." }
$Output = Join-Path $HarnessClasses "output"
& (Join-Path $JdkRoot "bin\java.exe") -cp "$HarnessClasses;$ClasspathText" dev.lemonos.BackendConfigMigrationStagingHarness `
    (Join-Path $Root "tools\fixtures\legacy-config.yml") `
    (Join-Path $Root "templates\runtime\LemonOS") `
    $Output
if ($LASTEXITCODE -ne 0) { throw "Config migration staging harness failed." }
