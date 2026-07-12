param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendSandboxPreviewRenderService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendSandboxPreviewRenderService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendSandboxPreviewRenderService",
    "List<Line> boxLines(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)",
    "double endX = (double)maxX + 1.0;",
    "double endY = (double)maxY + 1.0;",
    "double endZ = (double)maxZ + 1.0;",
    "lines.add(new Line(minX, minY, minZ, endX, minY, minZ));",
    "lines.add(new Line(endX, minY, endZ, endX, endY, endZ));",
    "int lineSteps(Line line)",
    "return Math.max(1, Math.min(128, (int)Math.ceil(Math.sqrt(dx * dx + dy * dy + dz * dz) * 2.0)));",
    "Point pointAt(Line line, int step, int steps)",
    "double progress = (double)step / (double)steps;",
    "record Line(double startX, double startY, double startZ, double endX, double endY, double endZ)",
    "record Point(double x, double y, double z)"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendSandboxPreviewRenderService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendSandboxPreviewRenderService sandboxPreviewRenderService;",
    "this.sandboxPreviewRenderService = new BackendSandboxPreviewRenderService();",
    "for (BackendSandboxPreviewRenderService.Line line : this.sandboxPreviewRenderService.boxLines(n, n2, n3, n4, n5, n6))",
    "private void drawPreviewLine(Player player, World world, BackendSandboxPreviewRenderService.Line line)",
    "int n = this.sandboxPreviewRenderService.lineSteps(line);",
    "BackendSandboxPreviewRenderService.Point point = this.sandboxPreviewRenderService.pointAt(line, i, n);",
    "player.spawnParticle(Particle.DUST, point.x(), point.y(), point.z(), 1, 0.0, 0.0, 0.0, 0.0, (Object)dustOptions);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendSandboxPreviewRenderService: $snippet"
    }
}

Write-Host "Backend sandbox preview render contract OK"
