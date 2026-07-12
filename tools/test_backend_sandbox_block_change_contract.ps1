param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxBlockChangeService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxBlockChangeService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxBlockChangeService",
    "boolean sameBlockData(BlockData first, BlockData second)",
    "return first != null && second != null && first.matches(second);",
    "boolean sameExactBlockData(BlockData first, BlockData second)",
    "return first != null && second != null && first.getAsString().equals(second.getAsString());",
    "boolean shouldAddChange(BlockData oldData, BlockData newData)",
    "return !this.sameExactBlockData(oldData, newData);",
    "BlockData targetData(BlockData oldData, BlockData newData, boolean reverse)",
    "return reverse ? oldData : newData;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxBlockChangeService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxBlockChangeService sandboxBlockChangeService;",
    "this.sandboxBlockChangeService = new BackendSandboxBlockChangeService();",
    "return this.sandboxBlockChangeService.sameBlockData(blockData, blockData2);",
    "return this.sandboxBlockChangeService.sameExactBlockData(blockData, blockData2);",
    "if (!this.sandboxBlockChangeService.shouldAddChange(blockData, blockData2))",
    "BlockData blockData = this.sandboxBlockChangeService.targetData(blockChange.oldData, blockChange.newData, bl);",
    "setBlockData(this.sandboxBlockChangeService.targetData(blockChange.oldData, blockChange.newData, bl), false)"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxBlockChangeService: $snippet"
    }
}

Write-Host "Backend sandbox block-change contract OK"
