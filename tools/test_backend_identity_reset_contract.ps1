param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendIdentityResetService.java")
$backend = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
$template = Get-Content -Raw -LiteralPath (Join-Path $Root "templates\runtime\lemonos-data\identity.yml")

foreach ($required in @(
    "final class BackendIdentityResetService",
    "CreateResult createRequest(FileConfiguration identities, String playerName, String identityKey, boolean bedrock",
    'identities.set(path + ".request_id", requestId)',
    'identities.set(path + ".session_id", sessionId.toString())',
    'identities.set(path + ".owner_backend", ownerBackend)',
    'identities.set(path + ".owner_instance", ownerInstance.toString())',
    "boolean clearRequestForSession(",
    "boolean clearRequestReference(",
    "boolean removeRequestReference(",
    "int clearOwnedRequests(",
    "int clearStaleRequests(",
    "List<String> requestReferences(",
    "boolean requestExistsByReference(",
    "String currentReference(",
    "record CreateResult(String reference, boolean created)",
    "boolean grantExists(FileConfiguration identities, String identityKey)",
    "void setGrant(FileConfiguration identities, String identityKey, String name)",
    "void clearGrant(FileConfiguration identities, String identityKey)",
    "Base64.getUrlEncoder().withoutPadding()"
)) { if (-not $service.Contains($required)) { throw "BackendIdentityResetService missing: $required" } }

foreach ($required in @(
    "private final Map<UUID, UUID> resetSessionIds",
    "private final UUID resetOwnerInstance",
    "this.recoverStaleResetRequests();",
    "this.clearOwnedResetRequests();",
    "this.resetSessionIds.put(player.getUniqueId(), UUID.randomUUID());",
    "this.identityResetService.createRequest(",
    "this.identityResetService.clearRequestForSession(",
    "return this.identityResetService.requestReferences(this.identities)",
    "return this.identityResetService.requestExistsByReference(this.identities, string)",
    "this.identityResetService.removeRequestReference(this.identities, string)"
)) { if (-not $backend.Contains($required)) { throw "LemonOSPlugin Reset wiring missing: $required" } }

if ($backend.Contains("player == null || this.isBedrockPlayer(player)")) { throw "Bedrock disconnect still bypasses Reset cancellation." }
if ($template -match "(?m)^reset_requests:" -or $template -match "(?m)^reset_grants:") { throw "Identity template unexpectedly seeds runtime reset state." }
Write-Host "LemonOS backend identity reset contract tests passed."
