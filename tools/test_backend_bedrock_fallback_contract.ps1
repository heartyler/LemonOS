param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendBedrockFallbackService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendBedrockFallbackService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendBedrockFallbackService",
    "List<FallbackButton> homeButtons(",
    "boolean currentServerLobby",
    "boolean peopleShortcutPublic",
    "boolean sandboxAvailable",
    "boolean admin",
    "buttons.add(FallbackButton.LOOK);",
    "if (!currentServerLobby && peopleShortcutPublic)",
    "buttons.add(FallbackButton.PEOPLE);",
    "buttons.add(FallbackButton.PLACES);",
    "if (sandboxAvailable)",
    "buttons.add(FallbackButton.SANDBOX);",
    "if (admin)",
    "buttons.add(FallbackButton.CARE);",
    "enum FallbackButton",
    "LOOK",
    "PEOPLE",
    "PLACES",
    "SANDBOX",
    "CARE"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendBedrockFallbackService missing required behavior snippet: $snippet"
    }
}

$lookIndex = $Service.IndexOf("buttons.add(FallbackButton.LOOK);")
$peopleIndex = $Service.IndexOf("buttons.add(FallbackButton.PEOPLE);")
$placesIndex = $Service.IndexOf("buttons.add(FallbackButton.PLACES);")
$sandboxIndex = $Service.IndexOf("buttons.add(FallbackButton.SANDBOX);")
$careIndex = $Service.IndexOf("buttons.add(FallbackButton.CARE);")
if ($lookIndex -lt 0 -or $peopleIndex -lt 0 -or $placesIndex -lt 0 -or $sandboxIndex -lt 0 -or $careIndex -lt 0 -or -not ($lookIndex -lt $peopleIndex -and $peopleIndex -lt $placesIndex -and $placesIndex -lt $sandboxIndex -and $sandboxIndex -lt $careIndex)) {
    throw "BackendBedrockFallbackService must preserve fallback button order: Look, People, Places, Sandbox, Care."
}

$requiredPluginSnippets = @(
    "private BackendBedrockFallbackService bedrockFallbackService;",
    "this.bedrockFallbackService = new BackendBedrockFallbackService();",
    "private Inventory createBedrockJavaFallbackHomeInventory(Player player)",
    "this.bedrockFallbackService.homeButtons(",
    "this.currentServer == ServerId.LOBBY",
    "this.peopleShortcutPublic(player)",
    "this.sandboxAvailable(player)",
    "this.isAdmin(player)",
    "private void setBedrockFallbackButton(Inventory inventory, BackendBedrockFallbackService.FallbackButton button)",
    "case LOOK -> this.setButton(inventory, Ui.Home.LOOK);",
    "case PEOPLE -> this.setButton(inventory, Ui.Home.PEOPLE);",
    "case PLACES -> this.setButton(inventory, Ui.Home.PLACES);",
    "case SANDBOX -> this.setButton(inventory, Ui.Home.SANDBOX);",
    "case CARE -> this.setButton(inventory, Ui.Home.CARE);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendBedrockFallbackService: $snippet"
    }
}

Write-Host "Backend Bedrock fallback contract OK"
