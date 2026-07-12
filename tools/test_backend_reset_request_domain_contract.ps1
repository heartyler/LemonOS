param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendIdentityResetService.java")
$plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

foreach ($required in @(
    'private static final String REFERENCE_SEPARATOR = "~"',
    "String currentReference(FileConfiguration identities, String identityKey)",
    "boolean requestExistsByReference(FileConfiguration identities, String reference)",
    "parts.requestId().equals(identities.getString",
    "this.reference(token, requestId)"
)) { if (-not $service.Contains($required)) { throw "Reset request reference domain missing: $required" } }

foreach ($required in @(
    "new BackendAdminResetActionService(this::resetRequestExistsByToken)",
    "return this.identityResetService.requestExistsByReference(this.identities, string)",
    "this.identityResetService.removeRequestReference(this.identities, string)"
)) { if (-not $plugin.Contains($required)) { throw "Reset request caller lacks stale-reference protection: $required" } }

Write-Host "Backend reset request reference-domain contract OK"
