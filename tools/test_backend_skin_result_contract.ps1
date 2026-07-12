param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSkinResultService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendSkinResultService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendSkinResultService",
    "BackendSkinResultService(SkinChoiceSaver skinChoiceSaver)",
    "void handleSkinResult(UUID uuid, String skinName, String result, boolean notify)",
    "Player player = Bukkit.getPlayer((UUID)uuid)",
    "player == null || !player.isOnline()",
    "BackendAdminProtocol.RESULT_SAVED.equals(result)",
    "this.skinChoiceSaver.save(player, skinName)",
    "Component.text((String)`"saved.`", (TextColor)NamedTextColor.GRAY)",
    "BackendAdminProtocol.RESULT_TRY_AGAIN.equals(result)",
    "Component.text((String)`"try again.`", (TextColor)NamedTextColor.DARK_GRAY)",
    "Component.text((String)`"out of range.`", (TextColor)NamedTextColor.DARK_GRAY)",
    "interface SkinChoiceSaver",
    "void save(Player player, String skinName)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendSkinResultService missing required skin result contract text: $required"
    }
}

foreach ($required in @(
    "private BackendSkinResultService skinResultService",
    "this.skinResultService = new BackendSkinResultService(this::saveSkinChoice)",
    "this.skinResultService.handleSkinResult(uUID, string, string2, bl)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendSkinResultService: $required"
    }
}

foreach ($forbidden in @(
    "BackendAdminProtocol.RESULT_SAVED.equals(string2)",
    "BackendAdminProtocol.RESULT_TRY_AGAIN.equals(string2)"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns skin result protocol detail: $forbidden"
    }
}

Write-Host "LemonOS backend skin result contract tests passed."
