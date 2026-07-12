param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendIdentityPreferenceService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\identity.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendIdentityPreferenceService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendIdentityPreferenceService",
    "boolean cubeeVisible(FileConfiguration identities, String identityKey, String serverProxyName, boolean lobby)",
    "void setCubeeVisible(FileConfiguration identities, String identityKey, String serverProxyName, boolean visible)",
    "if (lobby)",
    "return true",
    "if (identities == null)",
    "String path = this.cubeeServerPreferencePath(identityKey, serverProxyName)",
    "if (identities.isSet(path))",
    "return identities.getBoolean(path, true)",
    "return identities.getBoolean(this.cubeePreferencePath(identityKey), true)",
    "identities.set(this.cubeeServerPreferencePath(identityKey, serverProxyName), (Object)visible)",
    "return `"preferences.`" + this.identityPathToken(identityKey) + `".cubee.visible`"",
    "return `"preferences.`" + this.identityPathToken(identityKey) + `".cubee.visible-by-server.`" + serverProxyName",
    "Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8))"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendIdentityPreferenceService missing required preference contract text: $required"
    }
}

foreach ($required in @(
    "private BackendIdentityPreferenceService identityPreferenceService",
    "this.identityPreferenceService = new BackendIdentityPreferenceService()",
    "return this.identityPreferenceService.cubeeVisible(this.identities, this.identityKey(player), this.currentServer.proxyName, this.currentServer == ServerId.LOBBY)",
    "if (this.currentServer == ServerId.LOBBY)",
    "this.reloadIdentities()",
    "this.identityPreferenceService.setCubeeVisible(this.identities, this.identityKey(player), this.currentServer.proxyName, bl)",
    "this.saveIdentities()"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendIdentityPreferenceService: $required"
    }
}

foreach ($forbidden in @(
    "private String cubeePreferencePath(",
    "private String cubeeServerPreferencePath(",
    "`"preferences.`" + this.identityPathToken(this.identityKey(player)) + `".cubee.visible`"",
    "`"preferences.`" + this.identityPathToken(this.identityKey(player)) + `".cubee.visible-by-server.`" + this.currentServer.proxyName",
    "this.identities.isSet(string)",
    "this.identities.getBoolean(this.cubeePreferencePath(player), true)",
    "this.identities.set(this.cubeeServerPreferencePath(player), (Object)bl)"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns identity preference persistence detail: $forbidden"
    }
}

if ($template -notmatch "(?m)^preferences:\s*\{\}\s*$") {
    throw "Identity template no longer preserves empty preferences schema."
}

Write-Host "LemonOS backend identity preference contract tests passed."
