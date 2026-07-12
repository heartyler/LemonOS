param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\admin\BackendCubeePageAccessService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

foreach ($snippet in @(
    'pageName == null || pageName.isBlank()',
    '"ADMIN_CHUNKS_DIMENSION".equals(pageName)',
    'pageName.startsWith("ADMIN")',
    'return AccessPolicy.ADMIN_CHUNK_EDIT;',
    'return AccessPolicy.ADMIN;',
    'return AccessPolicy.PUBLIC;'
)) {
    if (-not $Service.Contains($snippet)) {
        throw "Cubee page access service missing policy: $snippet"
    }
}

$Classes = Join-Path $Root "build\test-backend-cubee-page-access"
$Harness = Join-Path $Root "tools\java\dev\lemonos\admin\BackendCubeePageAccessHarness.java"
if (Test-Path -LiteralPath $Classes) { Remove-Item -LiteralPath $Classes -Recurse -Force }
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
$JdkRoot = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" }
& (Join-Path $JdkRoot "bin\javac.exe") -encoding UTF-8 -d $Classes $ServicePath $Harness
if ($LASTEXITCODE -ne 0) { throw "Cubee page access harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp $Classes dev.lemonos.admin.BackendCubeePageAccessHarness
if ($LASTEXITCODE -ne 0) { throw "Cubee page access behavioral contract failed." }

foreach ($snippet in @(
    'this.cubeePageAccessService.policy(cubeePage.name())',
    'accessPolicy.adminRequired() && !this.requireAdmin(player)',
    'accessPolicy.chunkEditRequired() && !this.adminChunkActionService.canEditChunks(this.chunksRunning())'
)) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to centralized Cubee page access: $snippet"
    }
}

$routeStart = $Plugin.IndexOf('private void handleCubeeClick')
$routeEnd = $Plugin.IndexOf('private void handleHomePageClick', $routeStart)
$route = $Plugin.Substring($routeStart, $routeEnd - $routeStart)
if ([regex]::Matches($route, '!this.requireAdmin\(player\)').Count -ne 1) {
    throw "Cubee routing must enforce admin access through one centralized guard."
}
if ([regex]::Matches($route, 'canEditChunks\(this.chunksRunning\(\)\)').Count -ne 1) {
    throw "Cubee routing must enforce chunk edit access through one centralized guard."
}

Write-Host "Backend Cubee page access contract OK"
