param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxTransformInputService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxTransformInputService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxTransformInputService",
    "Integer rotation(String input)",
    "return value == 90 || value == 180 || value == 270 ? value : null;",
    "Character flipAxis(String input)",
    "String value = input.trim().toLowerCase(Locale.ROOT);",
    "return value.equals(`"x`") || value.equals(`"z`") ? value.charAt(0) : null;",
    "String failureMessage()",
    "return `"try again.`";",
    "private int parseInt(String input, int fallback)",
    "return Integer.parseInt(input.trim());",
    "return fallback;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxTransformInputService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxTransformInputService sandboxTransformInputService;",
    "this.sandboxTransformInputService = new BackendSandboxTransformInputService();",
    "Integer rotation = this.sandboxTransformInputService.rotation(string);",
    "this.failDrawingInput(player, drawingState, this.sandboxTransformInputService.failureMessage());",
    "drawingState.rotation = rotation;",
    "Character flipAxis = this.sandboxTransformInputService.flipAxis(string);",
    "drawingState.flipAxis = flipAxis;"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxTransformInputService: $snippet"
    }
}

Write-Host "Backend sandbox transform input contract OK"
