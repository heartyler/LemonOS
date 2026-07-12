param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendIdentityAccountService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$templatePath = Join-Path $Root "templates\runtime\lemonos-data\identity.yml"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendIdentityAccountService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath
$template = Get-Content -Raw -LiteralPath $templatePath

foreach ($required in @(
    "final class BackendIdentityAccountService",
    "boolean registered(FileConfiguration identities, String identityKey)",
    "void savePasscode(FileConfiguration identities, String identityKey, String name, String passcode)",
    "boolean verifyPasscode(FileConfiguration identities, String identityKey, String passcode)",
    "void removePasscode(FileConfiguration identities, String identityKey)",
    "return `"identities.`" + this.identityPathToken(identityKey)",
    "String legacyPath = this.legacyIdentityPath(identityKey)",
    "identityKey.startsWith(`"java:`")",
    "return legacyName.isBlank() ? `"`" : `"identities.`" + legacyName",
    "identities.set(path + `".name`", (Object)name)",
    "identities.set(path + `".platform`", (Object)(identityKey.startsWith(`"bedrock:`") ? `"bedrock`" : `"java`"))",
    "identities.set(path + `".salt`", (Object)this.randomToken())",
    "identities.set(path + `".hash`", (Object)this.hashPasscode(passcode, identities.getString(path + `".salt`", `"`")))",
    "identities.set(path + `".created`", (Object)System.currentTimeMillis())",
    "identities.set(path + `".key`", (Object)identityKey)",
    "identities.set(path + `".passcode`", (Object)true)",
    "identities.getBoolean(path + `".passcode`", false)",
    "PBEKeySpec(passcode.toCharArray(), this.decodeSalt(salt), 65536, 256)",
    "SecretKeyFactory.getInstance(`"PBKDF2WithHmacSHA256`")",
    "Base64.getEncoder().encodeToString(bytes)",
    "catch (IllegalArgumentException | GeneralSecurityException exception)",
    "return `"`"",
    "Base64.getUrlDecoder().decode(salt)",
    "ThreadLocalRandom.current().nextBytes(bytes)",
    "Base64.getUrlEncoder().withoutPadding().encodeToString(identityKey.getBytes(StandardCharsets.UTF_8))"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendIdentityAccountService missing required account contract text: $required"
    }
}

foreach ($required in @(
    "private BackendIdentityAccountService identityAccountService",
    "this.identityAccountService = new BackendIdentityAccountService()",
    "return this.identityAccountService.registered(this.identities, string)",
    "this.identityAccountService.savePasscode(this.identities, string, string2, string3)",
    "return this.identityAccountService.verifyPasscode(this.identities, string, string2)",
    "this.identityAccountService.removePasscode(this.identities, string)",
    "this.identityResetService.clearGrant(this.identities, string)",
    "return this.verifyIdentityPasscode(string, string3) && !this.identityResetGrantExists(string)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendIdentityAccountService: $required"
    }
}

foreach ($forbidden in @(
    "private String identityPath(",
    "private String identityDataPath(",
    "private boolean identityPathReady(",
    "private String legacyIdentityPath(",
    "private String hashIdentityPasscode(",
    "private byte[] decodeIdentitySalt(",
    "private String randomToken(",
    "SecretKeyFactory.getInstance(`"PBKDF2WithHmacSHA256`")",
    "new PBEKeySpec(",
    ".salt`", (Object)this.randomToken()",
    ".hash`", (Object)this.hashIdentityPasscode"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns identity account persistence/hash detail: $forbidden"
    }
}

if ($template -notmatch "(?m)^identities:\s*\{\}\s*$") {
    throw "Identity template no longer preserves empty identities schema."
}

Write-Host "LemonOS backend identity account contract tests passed."
