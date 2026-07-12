param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminResetActionService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendAdminResetActionService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendAdminResetActionService",
    "Predicate<String> resetRequestExists",
    "boolean canOpen(String token)",
    "return token != null && this.resetRequestExists.test(token);",
    "boolean canResolve(String token)",
    "return this.canOpen(token);"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendAdminResetActionService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendAdminResetActionService adminResetActionService;",
    "new BackendAdminResetActionService(this::resetRequestExistsByToken)",
    "this.adminResetActionService.canOpen(token)",
    "this.adminResetActionService.canResolve(string)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendAdminResetActionService: $snippet"
    }
}

$denyBody = [regex]::Match($Plugin, "private void denyResetRequest\(Player player, String string\) \{(?<body>[\s\S]*?)\n    \}").Groups["body"].Value
if ($denyBody.Contains("adminResetActionService.canResolve") -or $denyBody.Contains("adminResetActionService.canOpen")) {
    throw "denyResetRequest must remain idempotent and must not be guarded by reset action validation."
}

Write-Host "Backend admin reset action contract OK"
