param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = [System.IO.Path]::GetFullPath($Root)

$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\resources\plugin.yml")
$proxy = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\velocity-plugin.json")
$identity = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\dev\lemonos\common\LemonOS.java")
$probe = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaceRuntimeProbe.java")
$backendBuild = Get-Content -Raw -LiteralPath (Join-Path $Root "build_lemonos_backend.ps1")
$proxyBuild = Get-Content -Raw -LiteralPath (Join-Path $Root "build_lemonos_proxy.ps1")
$backend = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$palette = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\HoneyPalette.java")
$configTemplate = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\config.yml")
$messagesTemplate = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\LemonOS\messages.yml")

foreach ($required in @(
    "name: lemonos",
    "version: 1.0",
    "main: dev.lemonos.LemonOSPlugin",
    "lemonos.admin:",
    "default: op"
)) {
    if (-not $plugin.Contains($required)) {
        throw "Backend metadata missing: $required"
    }
}

foreach ($required in @(
    '"id": "lemonos_proxy"',
    '"name": "lemonos_proxy"',
    '"version": "1.0"',
    '"main": "dev.lemonos.proxy.LemonOSProxyPlugin"'
)) {
    if (-not $proxy.Contains($required)) {
        throw "Proxy metadata missing: $required"
    }
}

foreach ($required in @(
    'RELEASE_VERSION = "1.0"',
    'PROTOCOL_VERSION = "2.0"',
    'DATA_SCHEMA_VERSION = "2.0"',
    'ACCESS_SCHEMA_VERSION = "3.0"',
    'BACKEND_PLUGIN_NAME = "lemonos"',
    'PROXY_PLUGIN_ID = "lemonos_proxy"',
    'PROXY_PLUGIN_NAME = "lemonos_proxy"',
    'CONFIG_FOLDER_NAME = "LemonOS"',
    'SHARED_DATA_FOLDER_NAME = "lemonos-data"',
    'ADMIN_CHANNEL = "lemonos:admin2"'
)) {
    if (-not $identity.Contains($required)) {
        throw "LemonOS identity constant missing: $required"
    }
}

foreach ($required in @(
    'resolve(".honeydock").resolve("launchers")',
    'resolve("honeydock.bat")',
    'startHoneyDockLauncher',
    'startHoneyDockEntrypoint'
)) {
    if (-not $probe.Contains($required)) {
        throw "HoneyDock wake identity missing: $required"
    }
}

if (-not $backendBuild.Contains('"lemonos.jar"')) {
    throw "Backend build does not own lemonos.jar."
}
if (-not $proxyBuild.Contains('"lemonos_proxy.jar"')) {
    throw "Proxy build does not own lemonos_proxy.jar."
}

foreach ($required in @(
    'DEFAULT_WHITE = TextColor.color(0xFAF9F6)',
    'GREEN_TEA = TextColor.color(0xC9D8B6)'
)) {
    if (-not $palette.Contains($required)) {
        throw "Honey palette identity missing: $required"
    }
}

foreach ($required in @(
    'return List.of("<#FAF9F6>Honey", "<#C9D8B6>green tea", "");',
    'return List.of("", "<gray>%server%", "<gray>%time%");',
    '"working " + l2 + "s"',
    'new ButtonSpec(9, Material.YELLOW_STAINED_GLASS_PANE, "Clear"',
    'new ButtonSpec(16, Material.LIME_STAINED_GLASS_PANE, "Sign in"',
    'new ButtonSpec(25, Material.RED_STAINED_GLASS_PANE, "Reset"',
    'CANCEL_RESET = new ButtonSpec(13, Material.RED_STAINED_GLASS_PANE, "Cancel"'
)) {
    if (-not $backend.Contains($required)) {
        throw "Honey patch identity missing: $required"
    }
}

foreach ($required in @('<#FAF9F6>Honey', '<#C9D8B6>green tea', '<gray>%server%', '<gray>%time%', 'Asia/Bangkok', 'EEEdd HH:mm')) {
    if (-not $configTemplate.Contains($required)) {
        throw "Honey TAB template identity missing: $required"
    }
}

if ($configTemplate.Contains('%player%')) {
    throw "Honey TAB template duplicates the native Minecraft player-list row with a header player placeholder."
}

foreach ($required in @('lore: "green tea"', 'enter: "Sign in"', 'enter-passcode: "Enter Passcode"', 'create-passcode: "Create Passcode"')) {
    if (-not $messagesTemplate.Contains($required)) {
        throw "Honey message identity missing: $required"
    }
}

$normalWhiteMatches = Get-ChildItem -LiteralPath (Join-Path $Root "src\main") , (Join-Path $Root "templates") -Recurse -File |
    Where-Object { $_.Extension -in @(".java", ".yml", ".json") } |
    Select-String -CaseSensitive -Pattern 'NamedTextColor.WHITE', '#ffffff', '#FFFFFF', '<white>'
if ($normalWhiteMatches.Count -gt 0) {
    throw "Normal white remains instead of Honey Default White: $($normalWhiteMatches[0].Path):$($normalWhiteMatches[0].LineNumber)"
}

$legacyMatches = Get-ChildItem -LiteralPath (Join-Path $Root "src") , (Join-Path $Root "src_proxy") -Recurse -File |
    Where-Object { $_.Extension -in @(".java", ".yml", ".json") } |
    Select-String -CaseSensitive -Pattern "HomeOS", "homeos", "HomePad", "homepad", "Hometown", "hometown", "open-homepad", "Studio", "studio"
if ($legacyMatches.Count -gt 0) {
    throw "Legacy HomeOS/HomePad/Hometown identity remains: $($legacyMatches[0].Path):$($legacyMatches[0].LineNumber)"
}

Write-Host "LemonOS identity contract tests passed."
