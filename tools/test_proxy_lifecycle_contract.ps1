param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyLifecycleService.java"
$layoutPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\runtime\ProxyRuntimeLayout.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing proxy ProxyLifecycleService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$layout = Get-Content -Raw -LiteralPath $layoutPath
$proxy = Get-Content -Raw -LiteralPath $proxyPath

foreach ($required in @(
    "final class ProxyLifecycleService",
    "void ensureSharedDataFiles(ProxyRuntimeLayout runtimePaths)",
    "boolean stayedCloseCollectionEnabled(Path boardsConfigFile)",
    "String buildSourceSnapshot(Class<?> ownerClass)",
    "Files.createDirectories(runtimePaths.sharedDataFolder",
    "AccessRepository.defaultFile()",
    "PlaceStatusRepository.defaultFile()",
    "PlaytimeRepository.defaultFile()",
    "Unable to prepare LemonOS shared data.",
    "Unable to read Stayed Close collection gate.",
    "AccessRepository.cleanScalar",
    "lemonos-build.properties",
    "sourceSnapshotSha256"
)) {
    if (-not $service.Contains($required)) {
        throw "ProxyLifecycleService missing required lifecycle contract text: $required"
    }
}

foreach ($required in @(
    "public final class ProxyRuntimeLayout",
    "static ProxyRuntimeLayout resolve()",
    "this.runtimeRoot.resolve(`"lemonos-data`")",
    "this.runtimeRoot.resolve(`"LemonOS`").resolve(`"boards.yml`")",
    "System.getProperty(`"lemonos.runtimeRoot`")",
    "System.getenv(`"LEMONOS_RUNTIME_ROOT`")"
)) { if (-not $layout.Contains($required)) { throw "ProxyRuntimeLayout missing: $required" } }

foreach ($forbidden in @(
    "private Path resolveSharedDataFolder(",
    "private void ensureSharedDataFiles(",
    "private String defaultAccessFile(",
    "private String defaultPlaytimeFile(",
    "private String defaultPlacesFile(",
    "private String buildSourceSnapshot(",
    "private String cleanScalar(",
    "Files.createDirectories(this.sharedDataFolder",
    "Files.writeString(this.accessFile",
    "Files.writeString(this.playtimeFile",
    "Unable to prepare LemonOS shared data.",
    "Unable to read Stayed Close collection gate."
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns lifecycle setup detail: $forbidden"
    }
}

if ($proxy -notmatch "new ProxyLifecycleService\(this\.logger\)" -or
    $proxy -notmatch "ProxyRuntimeLayout runtimePaths = ProxyRuntimeLayout\.resolve\(\)" -or
    $proxy -notmatch "this\.lifecycleService\.ensureSharedDataFiles\(runtimePaths\)" -or
    $proxy -notmatch "this\.lifecycleService\.stayedCloseCollectionEnabled\(this\.boardsConfigFile\)" -or
    $proxy -notmatch "this\.lifecycleService\.buildSourceSnapshot\(LemonOSProxyPlugin\.class\)") {
    throw "LemonOSProxyPlugin is not wired through ProxyLifecycleService."
}

Write-Host "LemonOS proxy lifecycle contract tests passed."
