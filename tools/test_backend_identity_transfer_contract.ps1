param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$Service = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\BackendIdentityTransferService.java")
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

$requiredService = @(
    "void saveTransfer(FileConfiguration identities, Player player, String identityKey, String targetProxyName, IdentitySaver identitySaver)",
    "boolean acceptTransfer(FileConfiguration identities, Player player, String identityKey, String currentProxyName, IdentitySaver identitySaver)",
    'identities.set(path + ".until"',
    'identities.set(path + ".target"',
    'identities.set(path + ".address"',
    "if (accepted || until <= System.currentTimeMillis())",
    "identities.set(path, null);"
)
foreach ($snippet in $requiredService) {
    if (-not $Service.Contains($snippet)) { throw "Identity transfer contract missing: $snippet" }
}

$requiredPlugin = @(
    "this.identityTransferService.saveTransfer(this.identities, player, string, serverId.proxyName, this::saveIdentities)",
    "this.identityTransferService.acceptTransfer(this.identities, player, string, this.currentServer.proxyName, this::saveIdentities)"
)
foreach ($snippet in $requiredPlugin) {
    if (-not $Plugin.Contains($snippet)) { throw "Identity transfer wiring missing: $snippet" }
}

foreach ($snippet in @("care_mode", "CareModeApplier", "applyTransferredCareMode", "currentCareMode")) {
    if ($Service.Contains($snippet) -or $Plugin.Contains($snippet)) {
        throw "Removed World-page persistence remains: $snippet"
    }
}

Write-Host "Backend identity transfer contract OK"
