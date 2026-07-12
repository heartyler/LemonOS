param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$TemplatePath = Join-Path $Root "templates\runtime\LemonOS\config.yml"
$Plugin = Get-Content -Raw -LiteralPath $PluginPath
$Template = Get-Content -Raw -LiteralPath $TemplatePath

foreach ($snippet in @(
    'ZoneId.of("Asia/Bangkok")',
    'DateTimeFormatter.ofPattern("EEEdd HH:mm", Locale.ENGLISH)',
    '.replace("%time%", this.tabTimeFormatter.format(ZonedDateTime.now(this.tabTimeZone)))',
    '60000L - Math.floorMod(System.currentTimeMillis(), 60000L)',
    'this.updateTab(player);',
    'this.scheduleNextTabUpdate();'
)) {
    if (-not $Plugin.Contains($snippet)) {
        throw "TAB time implementation missing: $snippet"
    }
}

foreach ($snippet in @(
    'update-ticks: 1200',
    'zone: "Asia/Bangkok"',
    'format: "EEEdd HH:mm"',
    '"<gray>%server%"',
    '"<gray>%time%"'
)) {
    if (-not $Template.Contains($snippet)) {
        throw "TAB time preset missing: $snippet"
    }
}

Write-Host "Backend TAB time contract OK"
