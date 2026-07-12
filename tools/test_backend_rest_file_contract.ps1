param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendRestFileService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendRestFileService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendRestFileService",
    "void writeRestingSince(File honeyRoot, String serverProxyName, long restSinceMillis) throws IOException",
    "void clearRestingSince(File honeyRoot, String serverProxyName) throws IOException",
    "private Path restDirectory(File honeyRoot)",
    "private Path restFile(Path restDirectory, String serverProxyName)",
    "honeyRoot.toPath().resolve(`"lemonos-data`").resolve(`"rest`")",
    "Files.createDirectories(directory, new FileAttribute[0])",
    "Files.writeString(this.restFile(directory, serverProxyName), String.valueOf(restSinceMillis), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)",
    "Files.deleteIfExists(this.restFile(this.restDirectory(honeyRoot), serverProxyName))",
    "serverProxyName + `".resting`""
)) {
    if (-not $service.Contains($required)) {
        throw "BackendRestFileService missing required rest file contract text: $required"
    }
}

foreach ($required in @(
    "private BackendRestFileService restFileService",
    "this.restFileService = new BackendRestFileService()",
    "this.restFileService.writeRestingSince(this.honeyRoot(), this.currentServer.proxyName, this.restStateService.restSinceMillis(System.currentTimeMillis()))",
    "this.restFileService.clearRestingSince(this.honeyRoot(), this.currentServer.proxyName)",
    "Unable to write LemonOS rest timestamp for ",
    "Unable to clear LemonOS rest timestamp for "
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendRestFileService: $required"
    }
}

foreach ($forbidden in @(
    "Path path = this.honeyRoot().toPath().resolve(`"lemonos-data`").resolve(`"rest`")",
    "Files.writeString(path.resolve(this.currentServer.proxyName + `".resting`")",
    "Files.deleteIfExists(this.honeyRoot().toPath().resolve(`"lemonos-data`").resolve(`"rest`").resolve(this.currentServer.proxyName + `".resting`"))"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend rest timestamp file detail: $forbidden"
    }
}

Write-Host "LemonOS backend rest file contract tests passed."
