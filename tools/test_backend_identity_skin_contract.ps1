param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendIdentitySkinService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\skins.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendIdentitySkinService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendIdentitySkinService",
    "void saveChoice(FileConfiguration skins, Player player, String identityKey, String skinName)",
    "String savedSkin(FileConfiguration skins, String identityKey)",
    "if (skins == null)",
    "String path = this.skinPath(identityKey)",
    "skins.set(path + `".name`", (Object)player.getName())",
    "skins.set(path + `".skin`", (Object)skinName)",
    "skins.set(path + `".property`", null)",
    "skins.set(path + `".updated`", (Object)System.currentTimeMillis())",
    "return skins.getString(this.skinPath(identityKey) + `".skin`", `"`")",
    "return `"skins.`" + this.identityPathToken(identityKey)",
    "Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8))"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendIdentitySkinService missing required skin persistence contract text: $required"
    }
}

foreach ($required in @(
    "private BackendIdentitySkinService identitySkinService",
    "this.identitySkinService = new BackendIdentitySkinService()",
    "this.identitySkinService.saveChoice(this.skins, player, this.identityKey(player), string)",
    "this.identitySkinService.savedSkin(this.skins, this.identityKey(player))",
    "this.reloadSkins()",
    "this.saveSkins()",
    "this.validSkinName(string4)",
    "this.applySkin(player, string4, bl)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendIdentitySkinService: $required"
    }
}

foreach ($forbidden in @(
    "`"skins.`" + this.identityPathToken(this.identityKey(player))",
    ".property`", null",
    ".updated`", (Object)System.currentTimeMillis()",
    "this.skins.getString(string + `".skin`", `"`")",
    "this.skins.set(string2 + `".name`"",
    "this.skins.set(string2 + `".skin`""
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns identity skin persistence detail: $forbidden"
    }
}

if ($template -notmatch "(?m)^skins:\s*\{\}\s*$") {
    throw "Skins template no longer preserves empty skins schema."
}

Write-Host "LemonOS backend identity skin contract tests passed."
