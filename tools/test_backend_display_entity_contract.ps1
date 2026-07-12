param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendDisplayEntityService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendDisplayEntityService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendDisplayEntityService",
    "EntityStyle stayedCloseStyle(BackendDisplayConfig config, String role, boolean bedrockRole, boolean bottomRole, boolean bedrockBottomRole)",
    "EntityStyle hudStyle(BackendDisplayConfig config, String configPath, String role)",
    "config.doubleValue(`"stayed-close.display.bedrock.bottom-scale`", 0.42, 0.25, 2.00)",
    "config.doubleValue(`"stayed-close.display.bottom-scale`", 0.42, 0.25, 2.00)",
    "config.doubleValue(`"stayed-close.display.bedrock.scale`", 0.53, 0.25, 2.00)",
    "config.doubleValue(`"stayed-close.display.scale`", 0.53, 0.25, 2.00)",
    "config.intValue(`"stayed-close.display.bedrock.background-alpha`", 0, 0, 255)",
    "config.intValue(`"stayed-close.display.background-alpha`", 0, 0, 255)",
    "config.intValue(`"stayed-close.display.view-range`", 32, 1, 128)",
    "config.intValue(`"stayed-close.display.bedrock.bottom-line-width`", 260, 60, 800)",
    "config.intValue(`"stayed-close.display.bottom-line-width`", 260, 60, 800)",
    "config.intValue(`"stayed-close.display.bedrock.line-width`", 260, 60, 800)",
    "config.intValue(`"stayed-close.display.line-width`", 220, 60, 800)",
    "boolean bedrockRole = role.contains(`"bedrock_`")",
    "boolean bedrockBottomRole = role.endsWith(`"bedrock_bottom`")",
    "boolean bottomRole = role.endsWith(`"bottom`") && !bedrockBottomRole",
    "config.doubleValue(configPath + `".display.bedrock.bottom-scale`", 0.42, 0.25, 2.00)",
    "config.doubleValue(configPath + `".display.bottom-scale`", 0.42, 0.25, 2.00)",
    "config.doubleValue(configPath + `".display.bedrock.scale`", 0.53, 0.25, 2.00)",
    "config.doubleValue(configPath + `".display.scale`", 0.53, 0.25, 2.00)",
    "config.intValue(configPath + `".display.bedrock.background-alpha`", 0, 0, 255)",
    "config.intValue(configPath + `".display.background-alpha`", 0, 0, 255)",
    "config.intValue(configPath + `".display.view-range`", 32, 1, 128)",
    "config.intValue(configPath + `".display.bedrock.bottom-line-width`", 260, 60, 800)",
    "config.intValue(configPath + `".display.bottom-line-width`", 260, 60, 800)",
    "config.intValue(configPath + `".display.bedrock.line-width`", 260, 60, 800)",
    "config.intValue(configPath + `".display.line-width`", 220, 60, 800)",
    "static final class EntityStyle",
    "float scale()",
    "int backgroundAlpha()",
    "int viewRange()",
    "int lineWidth()"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendDisplayEntityService missing required entity style contract text: $required"
    }
}

foreach ($required in @(
    "private BackendDisplayEntityService displayEntityService",
    "this.displayEntityService = new BackendDisplayEntityService()",
    "this.applyBackendDisplayEntity(textDisplay, component, textAlignment, this.displayEntityService.stayedCloseStyle(this.backendDisplayConfig(), string, bl, bl2, bl3))",
    "this.applyBackendDisplayEntity(textDisplay, component, textAlignment, this.displayEntityService.hudStyle(this.backendDisplayConfig(), string2, string))",
    "private void applyBackendDisplayEntity(TextDisplay textDisplay, Component component, TextDisplay.TextAlignment textAlignment, BackendDisplayEntityService.EntityStyle entityStyle)",
    "textDisplay.text(component)",
    "textDisplay.setBillboard(this.stayedCloseBillboard())",
    "textDisplay.setAlignment(textAlignment)",
    "textDisplay.setShadowed(false)",
    "textDisplay.setSeeThrough(false)",
    "textDisplay.setDefaultBackground(false)",
    "float f = entityStyle.scale()",
    "new Transformation(new Vector3f(0.0f, 0.0f, 0.0f), new AxisAngle4f(), new Vector3f(f, f, f), new AxisAngle4f())",
    "Color.fromARGB(entityStyle.backgroundAlpha(), 18, 22, 26)",
    "textDisplay.setViewRange((float)entityStyle.viewRange())",
    "textDisplay.setLineWidth(entityStyle.lineWidth())",
    "textDisplay.setPersistent(true)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendDisplayEntityService: $required"
    }
}

foreach ($forbidden in @(
    "float f = (float)(bl3 ? this.configDouble(`"stayed-close.display.bedrock.bottom-scale`"",
    "float f = (float)(bl2 ? this.configDouble(string2 + `".display.bedrock.bottom-scale`"",
    "textDisplay.setLineWidth(bl3 ? this.configInt(`"stayed-close.display.bedrock.bottom-line-width`"",
    "textDisplay.setLineWidth(bl2 ? this.configInt(string2 + `".display.bedrock.bottom-line-width`""
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns duplicated display entity style detail: $forbidden"
    }
}

Write-Host "LemonOS backend display entity contract tests passed."
