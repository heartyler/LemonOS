param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendChunkSettingsService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\chunks.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendChunkSettingsService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendChunkSettingsService",
    "boolean ensureDefaults(FileConfiguration chunks, String serverProxyName, String defaultDimension, int defaultSize, String defaultStatus)",
    "String dimension(FileConfiguration chunks, String serverProxyName, String defaultDimension)",
    "int size(FileConfiguration chunks, String serverProxyName, int defaultSize)",
    "String status(FileConfiguration chunks, String serverProxyName)",
    "void setStatus(FileConfiguration chunks, String serverProxyName, String status)",
    "void setStatusAndDimension(FileConfiguration chunks, String serverProxyName, String status, String dimension)",
    "void setDimension(FileConfiguration chunks, String serverProxyName, String dimension)",
    "void setSize(FileConfiguration chunks, String serverProxyName, int size)",
    "void setCenter(FileConfiguration chunks, String serverProxyName, String dimension, int x, int z)",
    "boolean hasCenter(FileConfiguration chunks, String serverProxyName, String dimension)",
    "int centerX(FileConfiguration chunks, String serverProxyName, String dimension)",
    "int centerZ(FileConfiguration chunks, String serverProxyName, String dimension)",
    "chunks.set(path + `".dimension`", (Object)defaultDimension)",
    "chunks.set(path + `".size`", (Object)defaultSize)",
    "chunks.set(path + `".status`", (Object)defaultStatus)",
    "`"not started.`".equalsIgnoreCase(status)",
    "`"unavailable.`".equalsIgnoreCase(status)",
    "String.valueOf(status).matches(`"[0-9]{1,3}% ready`")",
    "return this.chunkPath(serverProxyName) + `".centers.`" + dimension",
    "return `"chunks.`" + serverProxyName"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendChunkSettingsService missing required chunk settings contract text: $required"
    }
}

foreach ($required in @(
    "private BackendChunkSettingsService chunkSettingsService",
    "this.chunkSettingsService = new BackendChunkSettingsService()",
    "this.chunkSettingsService.ensureDefaults(this.chunks, serverId.proxyName, ChunkDimension.WORLD.key, 3000, `"idle.`")",
    "ChunkDimension.fromKey(this.chunkSettingsService.dimension(this.chunks, this.currentServer.proxyName, ChunkDimension.WORLD.key))",
    "this.chunkSettingsService.size(this.chunks, this.currentServer.proxyName, 3000)",
    "return this.chunkSettingsService.status(this.chunks, this.currentServer.proxyName)",
    "this.chunkSettingsService.setStatus(this.chunks, this.currentServer.proxyName, string)",
    "this.chunkSettingsService.setStatusAndDimension(this.chunks, this.currentServer.proxyName, string2, chunkDimension.key)",
    "this.chunkSettingsService.setDimension(this.chunks, this.currentServer.proxyName, chunkDimension.key)",
    "this.chunkSettingsService.setSize(this.chunks, this.currentServer.proxyName, n)",
    "this.chunkSettingsService.setCenter(this.chunks, this.currentServer.proxyName, chunkDimension.key, location.getBlockX(), location.getBlockZ())",
    "this.chunkSettingsService.hasCenter(this.chunks, this.currentServer.proxyName, chunkDimension.key)",
    "this.chunkSettingsService.centerX(this.chunks, this.currentServer.proxyName, chunkDimension.key)",
    "this.chunkSettingsService.centerZ(this.chunks, this.currentServer.proxyName, chunkDimension.key)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendChunkSettingsService: $required"
    }
}

foreach ($forbidden in @(
    "private String chunkPath(",
    "this.chunks.set(this.chunkPath(this.currentServer)",
    "this.chunks.getString(this.chunkPath(this.currentServer)",
    "this.chunks.getInt(this.chunkPath(this.currentServer)",
    "this.chunks.isString(string + `".dimension`")",
    "this.chunks.isInt(string + `".size`")",
    "this.chunks.isString(string + `".status`")"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backend chunk settings persistence detail: $forbidden"
    }
}

foreach ($server in @("survival", "creative")) {
    if ($template -notmatch "(?ms)^chunks:.*?^\s{2}${server}:\s*\r?\n\s{4}dimension:\s*world\s*\r?\n\s{4}size:\s*3000\s*\r?\n\s{4}status:\s*idle\.\s*$") {
        throw "Chunks template no longer preserves default settings for $server."
    }
}

Write-Host "LemonOS backend chunk settings contract tests passed."
