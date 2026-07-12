param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendIdentitySessionService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\identity.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendIdentitySessionService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendIdentitySessionService",
    "void saveSession(FileConfiguration identities, Player player, String identityKey, long sessionMillis, IdentitySaver identitySaver)",
    "boolean acceptSession(FileConfiguration identities, Player player, String identityKey)",
    "void clearSession(FileConfiguration identities, String identityKey)",
    "identities.set(path + `".until`", (Object)(System.currentTimeMillis() + sessionMillis))",
    "identities.set(path + `".address`", (Object)this.playerAddress(player))",
    "identities.getLong(path + `".until`", 0L)",
    "identities.getString(path + `".address`", `"`")",
    "until > System.currentTimeMillis() && !address.isBlank() && address.equals(this.playerAddress(player))",
    "identities.set(this.identitySessionPath(identityKey), null)",
    "return `"sessions.`" + this.identityPathToken(identityKey)",
    "Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8))",
    "player.getAddress() == null || player.getAddress().getAddress() == null",
    "interface IdentitySaver"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendIdentitySessionService missing required session contract text: $required"
    }
}

foreach ($required in @(
    "private BackendIdentitySessionService identitySessionService",
    "this.identitySessionService = new BackendIdentitySessionService()",
    "this.identitySessionService.saveSession(this.identities, player, string, this.identitySessionMillis(), this::saveIdentities)",
    "return this.identitySessionService.acceptSession(this.identities, player, string)",
    "this.identitySessionService.clearSession(this.identities, string)",
    "private long identitySessionMillis()",
    "this.configInt(`"auth.session-minutes`", 10, 0, 10080)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendIdentitySessionService: $required"
    }
}

foreach ($forbidden in @(
    "private String identitySessionPath(",
    "private String identityAddress(",
    "`"sessions.`" + this.identityPathToken",
    ".address`", (Object)this.identityAddress(player)",
    "string3.equals(this.identityAddress(player))"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns identity session persistence detail: $forbidden"
    }
}

if ($template -notmatch "(?m)^sessions:\s*\{\}\s*$") {
    throw "Identity template no longer preserves empty sessions schema."
}

Write-Host "LemonOS backend identity session contract tests passed."
