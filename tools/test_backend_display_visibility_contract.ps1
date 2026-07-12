param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendDisplayVisibilityService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendDisplayVisibilityService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendDisplayVisibilityService",
    "private static final String STAYED_CLOSE_TITLE_DISPLAY = `"stayed_close_title`"",
    "private static final String STAYED_CLOSE_SUBTITLE_DISPLAY = `"stayed_close_subtitle`"",
    "private static final String STAYED_CLOSE_BOTTOM_DISPLAY = `"stayed_close_bottom`"",
    "private static final String STAYED_CLOSE_NAME_DISPLAY = `"stayed_close_name_`"",
    "private static final String STAYED_CLOSE_TIME_DISPLAY = `"stayed_close_time_`"",
    "private static final String STAYED_CLOSE_BEDROCK_TITLE_DISPLAY = `"stayed_close_bedrock_title`"",
    "private static final String STAYED_CLOSE_BEDROCK_SUBTITLE_DISPLAY = `"stayed_close_bedrock_subtitle`"",
    "private static final String STAYED_CLOSE_BEDROCK_BOTTOM_DISPLAY = `"stayed_close_bedrock_bottom`"",
    "private static final String STAYED_CLOSE_BEDROCK_ROW_DISPLAY = `"stayed_close_bedrock_row_`"",
    "boolean hudVisible(String role, boolean bedrockEnabled, boolean bedrockPlayer)",
    "boolean bedrockRole = role.contains(`"bedrock_`")",
    "if (bedrockPlayer && !bedrockEnabled || bedrockRole && !bedrockPlayer || !bedrockRole && bedrockPlayer)",
    "boolean stayedCloseVisible(String role, boolean bedrockEnabled, boolean bedrockPlayer)",
    "boolean bedrockRole = this.isStayedCloseBedrockRole(role)",
    "boolean javaPreferredRole = role != null && (role.startsWith(STAYED_CLOSE_NAME_DISPLAY) || role.startsWith(STAYED_CLOSE_TIME_DISPLAY) || bedrockEnabled && (STAYED_CLOSE_TITLE_DISPLAY.equals(role) || STAYED_CLOSE_SUBTITLE_DISPLAY.equals(role) || STAYED_CLOSE_BOTTOM_DISPLAY.equals(role)))",
    "if (bedrockPlayer && !bedrockEnabled)",
    "if (bedrockRole && !bedrockEnabled)",
    "if (bedrockRole && !bedrockPlayer || javaPreferredRole && bedrockPlayer)",
    "boolean isStayedCloseBedrockRole(String role)",
    "STAYED_CLOSE_BEDROCK_TITLE_DISPLAY.equals(role)",
    "role != null && role.startsWith(STAYED_CLOSE_BEDROCK_ROW_DISPLAY)"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendDisplayVisibilityService missing required visibility contract text: $required"
    }
}

foreach ($required in @(
    "private BackendDisplayVisibilityService displayVisibilityService",
    "this.displayVisibilityService = new BackendDisplayVisibilityService()",
    "if (!this.displayVisibilityService.hudVisible(string, this.boardConfig.bedrockEnabledAtPath(string2), this.isBedrockPlayer(player)))",
    "if (!this.displayVisibilityService.stayedCloseVisible(string, this.boardConfig.bedrockEnabled(BackendBoardConfig.STAYED_CLOSE), this.isBedrockPlayer(player)))",
    "return this.displayVisibilityService.isStayedCloseBedrockRole(string)",
    "player.hideEntity((Plugin)this, (Entity)textDisplay)",
    "player.showEntity((Plugin)this, (Entity)textDisplay)",
    "player.hideEntity((Plugin)this, entity)",
    "player.showEntity((Plugin)this, entity)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendDisplayVisibilityService: $required"
    }
}

foreach ($forbidden in @(
    "boolean bl = string.contains(`"bedrock_`")",
    "boolean bl3 = this.isBedrockPlayer(player)",
    "boolean bl2 = this.isBedrockStayedCloseRole(string)",
    "boolean bl3 = string != null && (string.startsWith(STAYED_CLOSE_NAME_DISPLAY)",
    "boolean bl4 = this.isBedrockPlayer(player)"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns duplicated display visibility policy: $forbidden"
    }
}

Write-Host "LemonOS backend display visibility contract tests passed."
