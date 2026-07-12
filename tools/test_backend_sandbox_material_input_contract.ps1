param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxMaterialInputService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxMaterialInputService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxMaterialInputService",
    "Material newBlockMaterial(String input, boolean replace, Material replaceTargetMaterial, Material defaultSandboxMaterial)",
    "return input.isBlank() ? (replace ? replaceTargetMaterial : defaultSandboxMaterial) : this.parseBlockMaterial(input);",
    "Material oldBlockMaterial(String input, Material replaceSourceMaterial)",
    "return input.isBlank() ? replaceSourceMaterial : this.parseBlockMaterial(input);",
    "Material circleBlockMaterial(String input, Material defaultSandboxMaterial)",
    "return input.isBlank() ? defaultSandboxMaterial : this.parseCircleMaterial(input);",
    "Material basicBlockMaterial(String input)",
    "return this.parseBlockMaterial(input);",
    "String failureMessage()",
    "return `"try again.`";",
    "return material != null && material.isBlock() ? material : null;",
    "if (material == Material.AIR)",
    "return Material.matchMaterial(input.trim().toUpperCase(Locale.ROOT).replace(' ', '_'));"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxMaterialInputService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxMaterialInputService sandboxMaterialInputService;",
    "this.sandboxMaterialInputService = new BackendSandboxMaterialInputService();",
    "this.sandboxMaterialInputService.newBlockMaterial(string, drawingState.action == DrawingAction.REPLACE, this.replaceTargetMaterial(), this.defaultSandboxMaterial())",
    "this.sandboxMaterialInputService.oldBlockMaterial(string, this.replaceSourceMaterial())",
    "this.sandboxMaterialInputService.circleBlockMaterial(string, this.defaultSandboxMaterial())",
    "this.sandboxMaterialInputService.basicBlockMaterial(string)",
    "this.failDrawingInput(player, drawingState, this.sandboxMaterialInputService.failureMessage());"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxMaterialInputService: $snippet"
    }
}

$forbiddenPluginSnippets = @(
    "private Material parseBlockMaterial",
    "private Material parseCircleMaterial"
)

foreach ($snippet in $forbiddenPluginSnippets) {
    if ($Plugin.Contains($snippet)) {
        throw "LemonOSPlugin still owns material input parsing: $snippet"
    }
}

Write-Host "Backend sandbox material input contract OK"
