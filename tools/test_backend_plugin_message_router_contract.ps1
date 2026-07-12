param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"

$ServicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendPluginMessageRouterService.java"
$PluginPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $ServicePath -PathType Leaf)) {
    throw "BackendPluginMessageRouterService.java is missing."
}

$Service = Get-Content -Raw -LiteralPath $ServicePath
$Plugin = Get-Content -Raw -LiteralPath $PluginPath

$requiredServiceSnippets = @(
    "final class BackendPluginMessageRouterService",
    "private final BackendOpenCubeeMessageService openCubeeMessageService;",
    "private final BackendSkinProtocolService skinProtocolService;",
    "private final BackendAccessLegacyService accessLegacyService;",
    "private final BackendAdminSendProtocolService adminSendProtocolService;",
    "BackendPluginMessageRouterService(",
    "void route(Player player, byte[] data)",
    "if (this.openCubeeMessageService.handleOpenCubeeMessage(player, data))",
    "if (this.skinProtocolService.handleSkinResultMessage(data))",
    "if (this.adminSendProtocolService.handleResult(data))",
    "this.accessLegacyService.handleAccessMessage(data);"
)

foreach ($snippet in $requiredServiceSnippets) {
    if (-not $Service.Contains($snippet)) {
        throw "BackendPluginMessageRouterService missing required behavior snippet: $snippet"
    }
}

$openCubeeIndex = $Service.IndexOf("this.openCubeeMessageService.handleOpenCubeeMessage(player, data)")
$skinIndex = $Service.IndexOf("this.skinProtocolService.handleSkinResultMessage(data)")
$sendIndex = $Service.IndexOf("this.adminSendProtocolService.handleResult(data)")
$accessIndex = $Service.IndexOf("this.accessLegacyService.handleAccessMessage(data);")
if ($openCubeeIndex -lt 0 -or $skinIndex -lt 0 -or $sendIndex -lt 0 -or $accessIndex -lt 0 -or -not ($openCubeeIndex -lt $skinIndex -and $skinIndex -lt $sendIndex -and $sendIndex -lt $accessIndex)) {
    throw "BackendPluginMessageRouterService must preserve routing order: open-cubee, skin-result, send-result, legacy access."
}

$requiredPluginSnippets = @(
    "private BackendPluginMessageRouterService pluginMessageRouterService;",
    "this.pluginMessageRouterService = new BackendPluginMessageRouterService(this.openCubeeMessageService, this.skinProtocolService, this.accessLegacyService, this.adminSendProtocolService);",
    "public void onPluginMessageReceived(String string, Player player, byte[] byArray)",
    "if (!ADMIN_CHANNEL.equals(string))",
    "this.pluginMessageRouterService.route(player, byArray);"
)

foreach ($snippet in $requiredPluginSnippets) {
    if (-not $Plugin.Contains($snippet)) {
        throw "LemonOSPlugin is not wired to BackendPluginMessageRouterService: $snippet"
    }
}

$messageHandlerStart = $Plugin.IndexOf("public void onPluginMessageReceived(String string, Player player, byte[] byArray)")
if ($messageHandlerStart -lt 0) {
    throw "LemonOSPlugin onPluginMessageReceived is missing."
}
$messageHandlerEnd = $Plugin.IndexOf("private void handleProxyOpenCubee", $messageHandlerStart)
if ($messageHandlerEnd -lt 0) {
    throw "LemonOSPlugin plugin message handler boundary is missing."
}
$messageHandler = $Plugin.Substring($messageHandlerStart, $messageHandlerEnd - $messageHandlerStart)
if ($messageHandler.Contains("handleOpenCubeeMessage(") -or $messageHandler.Contains("handleSkinResultMessage(") -or $messageHandler.Contains("handleAccessMessage(")) {
    throw "LemonOSPlugin should delegate protocol routing to BackendPluginMessageRouterService."
}

Write-Host "Backend plugin message router contract OK"
