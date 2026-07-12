param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\SkinService.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing proxy SkinService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$proxy = Get-Content -Raw -LiteralPath $proxyPath

foreach ($required in @(
    "final class SkinService",
    "void applySkin(ServerConnection serverConnection, UUID uuid, String skinName)",
    "private String applySkinViaSkinsRestorer(Player player, String skinName)",
    "private void invokeSkinApplier(Object skinApplier, Player player, Object property) throws ReflectiveOperationException",
    "private void sendSkinResult(ServerConnection serverConnection, UUID uuid, String skinName, String result)",
    "private static boolean validSkinName(String string)",
    "CompletableFuture.supplyAsync",
    "net.skinsrestorer.api.SkinsRestorerProvider",
    "findOrCreateSkinData",
    "getSkinApplier",
    "applySkin",
    "AdminProtocol.SKIN_RESULT",
    "AdminProtocol.RESULT_TRY_AGAIN",
    "AdminProtocol.RESULT_SAVED",
    "AdminProtocol.STATUS_UNAVAILABLE",
    "dataOutputStream.writeUTF(uuid.toString())",
    "dataOutputStream.writeUTF(skinName == null ? `"`" : skinName)",
    "Unable to apply LemonOS proxy skin.",
    "Unable to send LemonOS skin result.",
    "[A-Za-z0-9_]+"
)) {
    if (-not $service.Contains($required)) {
        throw "SkinService missing required skin contract text: $required"
    }
}

foreach ($forbidden in @(
    "private void applySkin(",
    "private String applySkinViaSkinsRestorer(",
    "private void invokeSkinApplier(",
    "private void sendSkinResult(",
    "private boolean validSkinName(",
    "AdminProtocol.SKIN_RESULT",
    "AdminProtocol.RESULT_TRY_AGAIN",
    "AdminProtocol.RESULT_SAVED",
    "Unable to apply LemonOS proxy skin.",
    "Unable to send LemonOS skin result."
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns skin service detail: $forbidden"
    }
}

if ($proxy -notmatch "new SkinService\(this\.server, this\.logger, this\.adminChannel, this::scheduleProxyTask\)" -or
    $proxy -notmatch "this\.skinService\.applySkin\(serverConnection, uUID, string5\)") {
    throw "LemonOSProxyPlugin is not wired through SkinService."
}

Write-Host "LemonOS skin service contract tests passed."
