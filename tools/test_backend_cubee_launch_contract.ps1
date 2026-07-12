param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendCubeeLaunchService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendCubeeLaunchService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendCubeeLaunchService",
    "LaunchPlan rememberedSurfacePlan(",
    "CubeeSurface surface",
    "CubeeRoot root",
    "boolean currentServerLobby",
    "boolean peopleShortcutPublic",
    "boolean admin",
    "boolean sandboxAvailable",
    "boolean bedrock",
    "if (surface == CubeeSurface.PLACES)",
    "LaunchAction.OPEN_GO",
    "if (surface == CubeeSurface.PEOPLE)",
    "if (!currentServerLobby && peopleShortcutPublic)",
    "LaunchAction.OPEN_LAST_PEOPLE",
    "plannedSurface = CubeeSurface.HOME;",
    "if (surface == CubeeSurface.ADMIN_PEOPLE)",
    "LaunchAction.OPEN_LAST_ADMIN_PEOPLE",
    "plannedRoot = CubeeRoot.CUBEE;",
    "if (surface == CubeeSurface.SANDBOX)",
    "LaunchAction.OPEN_DRAWING",
    "if (plannedRoot == CubeeRoot.CARE)",
    "LaunchAction.OPEN_ADMIN",
    "LaunchAction.OPEN_BEDROCK_HOME",
    "LaunchAction.OPEN_JAVA_HOME",
    "LaunchPlan defaultLaunchPlan(",
    "boolean currentServerSurvival",
    "boolean currentServerCreative",
    "if (currentServerLobby)",
    "if (currentServerSurvival && peopleShortcutPublic)",
    "if (currentServerCreative && sandboxAvailable)",
    "record LaunchPlan(LaunchAction action, CubeeRoot root, CubeeSurface surface)",
    "enum LaunchAction"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendCubeeLaunchService missing required behavior snippet: $snippet"
    }
}

$requiredPluginSnippets = @(
    "private BackendCubeeLaunchService cubeeLaunchService;",
    "new BackendCubeeLaunchService()",
    "this.openCubeeLaunchPlan(player, this.cubeeLaunchService.rememberedSurfacePlan(",
    "this.currentCubeeSurface(player)",
    "this.currentCubeeRoot(player)",
    "this.currentServer == ServerId.LOBBY",
    "this.peopleShortcutPublic(player)",
    "this.isAdmin(player)",
    "this.sandboxAvailable(player)",
    "this.isBedrockPlayer(player)",
    "this.openCubeeLaunchPlan(player, this.cubeeLaunchService.defaultLaunchPlan(",
    "this.currentServer == ServerId.SURVIVAL",
    "this.currentServer == ServerId.CREATIVE",
    "private void openCubeeLaunchPlan(Player player, BackendCubeeLaunchService.LaunchPlan launchPlan)",
    "this.switchCubeeRoot(player, launchPlan.root());",
    "this.switchCubeeSurface(player, launchPlan.surface());",
    "case OPEN_GO -> this.openGo(player);",
    "case OPEN_LAST_PEOPLE -> this.openLastPeople(player);",
    "case OPEN_LAST_ADMIN_PEOPLE -> this.openLastAdminPeople(player);",
    "case OPEN_DRAWING -> this.openDrawing(player);",
    "case OPEN_ADMIN -> this.openAdmin(player);",
    "case OPEN_BEDROCK_HOME -> this.openBedrockHome(player);",
    "case OPEN_JAVA_HOME -> player.openInventory(this.createJavaHomeInventory(player));"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendCubeeLaunchService: $snippet"
    }
}

Write-Host "Backend Cubee launch contract OK"
