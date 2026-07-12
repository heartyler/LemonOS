param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxInputFailureService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxInputFailureService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxInputFailureService",
    "FailureResult fail(int currentFailures, String message)",
    "int failures = currentFailures + 1;",
    "if (failures >= 3)",
    "return new FailureResult(failures, true, `"nothing changed.`", NamedTextColor.DARK_GRAY);",
    "return new FailureResult(failures, false, message, NamedTextColor.DARK_GRAY);",
    "record FailureResult(int failures, boolean closeSession, String message, NamedTextColor color)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxInputFailureService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxInputFailureService sandboxInputFailureService;",
    "this.sandboxInputFailureService = new BackendSandboxInputFailureService();",
    "BackendSandboxInputFailureService.FailureResult failureResult = this.sandboxInputFailureService.fail(drawingState.failedInputs, string);",
    "drawingState.failedInputs = failureResult.failures();",
    "if (failureResult.closeSession())",
    "this.removeDrawingState(player.getUniqueId());",
    "this.cancelSandboxIdleTimeout(player.getUniqueId());",
    "this.sendSandboxStatus(player, failureResult.message(), failureResult.color());"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxInputFailureService: $snippet"
    }
}

$forbiddenPluginSnippets = @(
    "++drawingState.failedInputs;",
    "drawingState.failedInputs >= 3"
)

foreach ($snippet in $forbiddenPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still owns drawing input failure policy: $snippet"
    }
}

Write-Host "Backend sandbox input failure contract OK"
