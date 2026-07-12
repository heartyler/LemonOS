param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$repositoryPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\AccessRepository.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"
$accessCommandPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyAccessCommandService.java"
$accessMessagePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\AccessMessageService.java"

if (-not (Test-Path -LiteralPath $repositoryPath -PathType Leaf)) {
    throw "Missing proxy AccessRepository."
}

$repository = Get-Content -Raw -LiteralPath $repositoryPath
$proxy = Get-Content -Raw -LiteralPath $proxyPath
$accessCommand = Get-Content -Raw -LiteralPath $accessCommandPath
$accessMessage = Get-Content -Raw -LiteralPath $accessMessagePath

foreach ($required in @(
    "final class AccessRepository",
    "static String defaultFile()",
    "void load()",
    "void save() throws IOException",
    "String content()",
    "writeStringAtomic",
    "LemonOS.ACCESS_SCHEMA_VERSION",
    "updateAccess",
    "updateNameAccess",
    "loadStrict",
    "withFileLock",
    "admins:"
)) {
    if (-not $repository.Contains($required)) {
        throw "AccessRepository missing required storage contract text: $required"
    }
}

foreach ($forbidden in @(
    "private final Set<UUID> adminAccess",
    "private final Set<String> adminNameAccess",
    "private final Map<UUID, String> adminNames"
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns access storage state: $forbidden"
    }
}

if ($proxy -notmatch "new AccessRepository\(this\.accessFile, this\.logger\)" -or
    $proxy -notmatch "new ProxyAccessCommandService\(this\.server, this\.logger, this\.accessRepository\)" -or
    $proxy -notmatch "new AccessMessageService\(this\.server, this\.logger, this\.adminChannel, this\.accessRepository\)" -or
    $proxy -notmatch "this\.accessRepository\.load\(\)" -or
    $accessCommand -notmatch "this\.accessRepository\.updateNameAccess\(name, admin\)" -or
    $accessMessage -notmatch "this\.accessRepository\.updateAccess") {
    throw "LemonOSProxyPlugin is not wired through AccessRepository."
}

$classes = Join-Path $Root "build\test-proxy-access-repository"
$velocityCandidates = @(
    (Join-Path $Root "third_party\runtime\velocity.jar"),
    (Join-Path (Split-Path -Parent $Root) "third_party\runtime\velocity.jar")
)
if ($env:LEMONOS_RUNTIME_ROOT) { $velocityCandidates += Join-Path $env:LEMONOS_RUNTIME_ROOT "velocity\velocity.jar" }
$velocityJar = $velocityCandidates | Where-Object { Test-Path -LiteralPath $_ -PathType Leaf } | Select-Object -First 1
if (-not $velocityJar) { throw "Velocity test dependency missing. Run tools\restore_test_dependencies.ps1." }
$harness = Join-Path $Root "tools\java\dev\lemonos\proxy\AccessRepositoryHarness.java"
$jdkRoot = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" }
if (Test-Path -LiteralPath $classes) { Remove-Item -LiteralPath $classes -Recurse -Force }
New-Item -ItemType Directory -Path $classes -Force | Out-Null
& (Join-Path $jdkRoot "bin\javac.exe") -encoding UTF-8 -cp $velocityJar -d $classes `
    (Join-Path $Root "src_proxy\dev\lemonos\common\LemonOS.java") `
    (Join-Path $Root "src_proxy\dev\lemonos\common\AdminProtocol.java") `
    $repositoryPath $harness
if ($LASTEXITCODE -ne 0) { throw "Proxy access repository harness compilation failed." }
& (Join-Path $jdkRoot "bin\java.exe") -cp "$classes;$velocityJar" dev.lemonos.proxy.AccessRepositoryHarness
if ($LASTEXITCODE -ne 0) { throw "Proxy access repository behavioral contract failed." }

Write-Host "LemonOS proxy storage contract tests passed."
