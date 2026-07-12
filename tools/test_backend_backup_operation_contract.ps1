param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendBackupOperationService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendBackupOperationService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendBackupOperationService",
    "BackupPlan plan(Path honeyRoot, Path runtimeRoot, String serverName, String timestamp)",
    'honey_" + serverName + "_" + timestamp + ".zip"',
    '"honey_lemonos-data_" + timestamp + ".zip"',
    'runtimeRoot.resolve("world")',
    "boolean requiredPathsExist(BackupPlan plan)",
    "Files.isDirectory(plan.worldPath",
    "Files.isDirectory(plan.lemonosDataRoot",
    "Files.isDirectory(plan.lemonOsRoot",
    "void write(BackupPlan plan) throws IOException",
    "void write(BackupPlan plan, BackendOperationCancellation cancellation) throws IOException",
    "this.writeServerBackup(plan, serverTmp, cancellation)",
    "this.writeLemonosDataBackup(plan, dataTmp, cancellation)",
    'this.addPathToZip(zipOutputStream, plan.worldPath, plan.serverName + "/world", cancellation)',
    'this.addPathToZip(zipOutputStream, plan.lemonosDataRoot, "lemonos-data", cancellation)',
    'this.addPathToZip(zipOutputStream, plan.lemonOsRoot, "LemonOS", cancellation)',
    "addNetworkConfigSnapshot",
    'List.of("velocity", "lobby", "survival", "creative")',
    '"velocity.toml", "server.properties", "bukkit.yml", "spigot.yml", "commands.yml", "permissions.yml", "whitelist.json", "ops.json"',
    '"configs/" + serverName + "/plugins/SkinsRestorer"',
    '"configs/" + serverName + "/plugins/floodgate"',
    '"configs/" + serverName + "/plugins/Geyser-Spigot"',
    '"configs/" + serverName + "/plugins/Geyser-Velocity"',
    "replaceBackupArchive",
    "StandardCopyOption.ATOMIC_MOVE",
    'name.equals("logs")',
    'name.equals("cache")',
    'name.equals("caches")',
    'name.equals("backups")',
    'name.equals("crash-reports")',
    'name.equals("libraries")',
    'name.equals("versions")',
    'name.equals(".git")',
    'fileName.endsWith(".jar")',
    'fileName.endsWith(".log")',
    'fileName.endsWith(".tmp")',
    'fileName.endsWith(".lock")',
    'fileName.endsWith(".zip")',
    'fileName.endsWith(".7z")',
    "static final class BackupPlan"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendBackupOperationService missing required behavior snippet: $snippet"
    }
}

if ($Service.Contains('plugins/GSit')) {
    throw "Backend backup operation still carries excluded GSit configuration."
}

$requiredPluginSnippets = @(
    "private BackendBackupOperationService backupOperationService;",
    "new BackendBackupOperationService()",
    "BackendBackupOperationService.BackupPlan backupPlan = this.backupOperationService.plan(",
    "this.backupOperationService.requiredPathsExist(backupPlan)",
    "this.backupOperationService.write(backupPlan, operation.cancellation)",
    "backupPlan.serverBackup.getFileName()",
    "backupPlan.lemonosDataBackup.getFileName()",
    "this.prepareLiveBackupSnapshot();",
    "this.saveIdentities();",
    "this.savePlaces();",
    "this.saveSkins();",
    "this.saveBackups();"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendBackupOperationService: $snippet"
    }
}

$removedPluginSnippets = @(
    "private void writeServerBackup(",
    "private void writeLemonosDataBackup(",
    "private void addNetworkConfigSnapshot(",
    "private void addPathToZip(",
    "private void addFileToZip(",
    "private boolean skipBackupPath("
)

foreach ($snippet in $removedPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still contains backup operation helper: $snippet"
    }
}

Write-Host "Backend backup operation contract OK"
