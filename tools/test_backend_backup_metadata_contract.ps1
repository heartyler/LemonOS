param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendBackupMetadataService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\backup.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendBackupMetadataService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendBackupMetadataService",
    "String lastCopy(FileConfiguration backups, String serverProxyName)",
    "void setLastCopy(FileConfiguration backups, String serverProxyName, String timestamp)",
    "String value = backups == null ? null : backups.getString(this.lastCopyPath(serverProxyName))",
    "return value == null || value.isBlank() ? null : value",
    "backups.set(this.lastCopyPath(serverProxyName), (Object)timestamp)",
    "return `"last_copy.`" + serverProxyName"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendBackupMetadataService missing required backup metadata contract text: $required"
    }
}

foreach ($required in @(
    "private BackendBackupMetadataService backupMetadataService",
    "this.backupMetadataService = new BackendBackupMetadataService()",
    "return this.backupMetadataService.lastCopy(this.backups, serverId.proxyName)",
    "this.backupMetadataService.setLastCopy(this.backups, serverId.proxyName, string)",
    "this.reloadBackups()",
    "this.loadBackups()",
    "this.saveBackups()",
    "this.setLastBackupTimestamp(serverId, timestamp)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendBackupMetadataService: $required"
    }
}

foreach ($forbidden in @(
    "this.backups.getString(`"last_copy.`" + serverId.proxyName)",
    "this.backups.set(`"last_copy.`" + serverId.proxyName",
    "String string = this.backups == null ? null : this.backups.getString"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns backup metadata persistence detail: $forbidden"
    }
}

if ($template -notmatch "(?m)^last_copy:\s*\{\}\s*$") {
    throw "Backup template no longer preserves empty last_copy schema."
}

Write-Host "LemonOS backend backup metadata contract tests passed."
