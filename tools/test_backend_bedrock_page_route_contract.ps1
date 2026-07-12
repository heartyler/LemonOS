param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendBedrockPageRouteService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendBedrockPageRouteService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendBedrockPageRouteService",
    "PageRoute route(boolean bedrockPlayer)",
    "bedrockPlayer ? PageRoute.BEDROCK : PageRoute.JAVA",
    "enum PageRoute",
    "BEDROCK",
    "JAVA",
    "boolean bedrock()",
    "return this == BEDROCK;"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendBedrockPageRouteService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendBedrockPageRouteService bedrockPageRouteService;",
    "this.bedrockPageRouteService = new BackendBedrockPageRouteService();",
    "private boolean shouldOpenBedrockPage(Player player)",
    "return this.bedrockPageRouteService.route(this.isBedrockPlayer(player)).bedrock();",
    "if (this.shouldOpenBedrockPage(player)) {",
    "this.openBedrockGo(player);",
    "this.openBedrockPeople(player, n3);",
    "this.openBedrockAdmin(player);",
    "this.openBedrockRequests(player, requestState, player2);",
    "this.openBedrockDrawing(player);",
    "this.openBedrockCloneConfirm(player);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendBedrockPageRouteService: $snippet"
    }
}

Write-Host "Backend Bedrock page route contract OK"
