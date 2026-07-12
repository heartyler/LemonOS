param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendMainConfigDefaultService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendMainConfigDefaultService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendMainConfigDefaultService",
    "boolean applyCoreDefaults(FileConfiguration config)",
    'this.setMissing(config, "package.name", "Honey")',
    'this.setMissing(config, "package.version", "26.2")',
    'this.setMissing(config, "lemonos.version", "1.0")',
    'this.setMissing(config, "server", "auto")',
    "boolean applyCubeeDefaults(FileConfiguration config)",
    'this.setMissing(config, "cubee.slot", 8)',
    'this.setMissing(config, "auth.bedrock-trusted", true)',
    'this.setMissing(config, "auth.session-minutes", 10)',
    'this.setMissing(config, "ui.hidden-command-suggestions", true)',
    "boolean applyTabDefaults(FileConfiguration config)",
    'config.getInt("tab.update-ticks") == 6',
    'config.set("tab.update-ticks", 1200)',
    'config.getStringList("tab.footer.lines").equals(List.of("", "<gray>%server%"))',
    'this.setMissing(config, "tab.update-ticks", 1200)',
    'this.setMissing(config, "tab.time.zone", "Asia/Bangkok")',
    'this.setMissing(config, "tab.time.format", "EEEdd HH:mm")',
    'this.setMissing(config, "tab.header.lines", List.of("<#FAF9F6>Honey", "<#C9D8B6>green tea", ""))',
    'this.setMissing(config, "tab.footer.lines", List.of("", "<gray>%server%", "<gray>%time%"))',
    "boolean applyRestDefaults(FileConfiguration config)",
    'this.setMissing(config, "rest.idle-minutes", 5)',
    'this.setMissing(config, "rest.status.waking-up", "waking up.")',
    'this.setMissing(config, "rest.suspend.care-world-status", true)',
    "return this.configMigrationService.setMissing(config, path, value);"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendMainConfigDefaultService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendMainConfigDefaultService mainConfigDefaultService;",
    "this.mainConfigDefaultService = new BackendMainConfigDefaultService(this.configMigrationService);",
    "this.mainConfigDefaultService.applyCoreDefaults(this.config)",
    "this.mainConfigDefaultService.applyCubeeDefaults(this.config)",
    "this.mainConfigDefaultService.applyTabDefaults(this.config)",
    "this.mainConfigDefaultService.applyRestDefaults(this.config)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendMainConfigDefaultService: $snippet"
    }
}

$migrationStart = $Plugin.IndexOf("private void migrateLemonOSConfig()")
$migrationEnd = $Plugin.IndexOf("private boolean setMissing", $migrationStart)
if ($migrationStart -lt 0 -or $migrationEnd -lt 0) {
    throw "Could not isolate LemonOSPlugin migrateLemonOSConfig method."
}
$migration = $Plugin.Substring($migrationStart, $migrationEnd - $migrationStart)

$forbiddenPaths = @(
    '"package.',
    '"lemonos.version"',
    '"server"',
    '"cubee.',
    '"look.',
    '"auth.',
    '"ui.',
    '"tab.',
    '"rest.'
)

foreach ($path in $forbiddenPaths) {
    if ($migration.Contains("this.setMissing(this.config, $path")) {
        throw "LemonOSPlugin still owns extracted main config defaults: $path"
    }
}

Write-Host "Backend main config default contract OK"
