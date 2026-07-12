param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$Classes = Join-Path $Root "build\test-backend-yaml-store"
$Merge = Join-Path $Root "src\main\java\dev\lemonos\storage\BackendYamlDeltaMerge.java"
$Store = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\storage\BackendYamlStore.java")
$Harness = Join-Path $Root "tools\java\dev\lemonos\storage\BackendYamlStoreHarness.java"
if (Test-Path -LiteralPath $Classes) { Remove-Item -LiteralPath $Classes -Recurse -Force }
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -d $Classes $Merge $Harness
if ($LASTEXITCODE -ne 0) { throw "Backend YAML store harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp $Classes dev.lemonos.storage.BackendYamlStoreHarness
if ($LASTEXITCODE -ne 0) { throw "Backend YAML store behavioral contract failed." }
foreach ($snippet in @(
    'FileLock ignored = channel.lock()',
    'BackendYamlDeltaMerge.merge(',
    'loadStrict(target)',
    'Refusing to overwrite invalid YAML',
    '".tmp-" + UUID.randomUUID()',
    'Files.deleteIfExists(temporary)'
)) {
    if (-not $Store.Contains($snippet)) { throw "Backend YAML store safety missing: $snippet" }
}
Write-Host "Backend YAML store behavioral contract passed."
