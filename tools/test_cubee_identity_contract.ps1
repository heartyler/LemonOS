param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$productionRoots = @(
    (Join-Path $Root "src"),
    (Join-Path $Root "src_proxy"),
    (Join-Path $Root "templates")
)
$productionFiles = @(
    Get-ChildItem -LiteralPath $productionRoots -Recurse -File |
        Where-Object { $_.Extension -in @(".java", ".yml", ".yaml") }
)
$forbidden = @(
    "HoneyPad", "honeyPad", "honeypad", "HONEYPAD",
    "OPEN_PAD", "open-pad", '"pad"', "/pad"
)
foreach ($file in $productionFiles) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    foreach ($token in $forbidden) {
        if ($text.Contains($token)) {
            throw "Legacy HoneyPad surface remains in production: $($file.FullName) -> $token"
        }
    }
}

$pluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$plugin = Get-Content -Raw -LiteralPath $pluginPath
foreach ($required in @(
    'private static final String SYSTEM_ITEM_CUBEE = "cubee";',
    'this.systemItem(Material.HONEYCOMB, this.messageText("items.cubee.name", "Cubee")',
    'itemStack.getType() != Material.HONEYCOMB',
    'return this.hasSystemItemMarker(itemMeta, SYSTEM_ITEM_CUBEE);',
    'this.configInt("cubee.slot", 8, 0, 8)',
    'this.configBoolean("cubee.enabled", true)'
)) {
    if (-not $plugin.Contains($required)) {
        throw "Cubee canonical backend behavior is missing: $required"
    }
}
if ($plugin -match 'isLegacySystemItem\(itemMeta,\s*"Cubee"') {
    throw "Cubee must not recognize a name/lore legacy item."
}

$pluginYml = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\resources\plugin.yml")
if ($pluginYml -notmatch '(?m)^  cubee:\s*$' -or $pluginYml -notmatch '(?m)^    usage: /cubee\s*$') {
    throw "plugin.yml does not declare the canonical /cubee command."
}
$adminProtocol = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\dev\lemonos\common\AdminProtocol.java")
if (-not $adminProtocol.Contains('OPEN_CUBEE = "open-cubee"')) {
    throw "Proxy protocol does not declare canonical open-cubee."
}

$initializer = Get-Content -Raw -LiteralPath (Join-Path $Root "tools\initialize_lemonos_runtime.ps1")
if ($initializer -match '(?i)honeypad' -or $initializer -notmatch 'templates\\runtime') {
    throw "Fresh runtime initialization must use canonical Cubee templates without legacy cutover logic."
}

Write-Host "Cubee clean identity contract OK"
