param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$protocolPath = Join-Path $Root "src\main\java\dev\lemonos\BackendAdminProtocol.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"
$buildPath = Join-Path $Root "build_lemonos_backend.ps1"

if (-not (Test-Path -LiteralPath $protocolPath -PathType Leaf)) {
    throw "Missing backend BackendAdminProtocol."
}

$protocol = Get-Content -Raw -LiteralPath $protocolPath
$backend = Get-Content -Raw -LiteralPath $backendPath
$backendSources = (Get-ChildItem -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos") -File -Filter "*.java" | Sort-Object Name | ForEach-Object { Get-Content -Raw -LiteralPath $_.FullName }) -join "`n"
$build = Get-Content -Raw -LiteralPath $buildPath

foreach ($required in @(
    "final class BackendAdminProtocol",
    "static final String ADMIN_CHANNEL = `"lemonos:admin2`"",
    "static final byte ACCESS_REQUEST_MAGIC = 42",
    "static final byte ACCESS_ACK_MAGIC = 43",
    "static final String SKIN_APPLY = `"skin-apply`"",
    "static final String SKIN_RESULT = `"skin-result`"",
    "static final String OPEN_CUBEE = `"open-cubee`"",
    "static final String WAKE_PLACE = `"wake-place`"",
    "static final String RESULT_SAVED = `"saved`"",
    "static final String RESULT_TRY_AGAIN = `"try-again`"",
    "private BackendAdminProtocol()"
)) {
    if (-not $protocol.Contains($required)) {
        throw "BackendAdminProtocol missing required backend protocol contract text: $required"
    }
}

foreach ($required in @(
    "private static final String ADMIN_CHANNEL = BackendAdminProtocol.ADMIN_CHANNEL",
    "this.wakePlaceService.sendWakePlaceRequest(player, serverId.proxyName)",
    "this.skinResultService.handleSkinResult(uUID, string, string2, bl)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendAdminProtocol: $required"
    }
}

foreach ($required in @(
    "byteArrayDataOutput.writeByte(BackendAdminProtocol.ACCESS_REQUEST_MAGIC)",
    "data[0] == BackendAdminProtocol.ACCESS_ACK_MAGIC",
    "byteArrayDataOutput.writeUTF(BackendAdminProtocol.WAKE_PLACE)",
    "BackendAdminProtocol.RESULT_SAVED.equals(result)",
    "BackendAdminProtocol.RESULT_TRY_AGAIN.equals(result)",
    "byteArrayDataOutput.writeUTF(BackendAdminProtocol.SKIN_APPLY)",
    "BackendAdminProtocol.SKIN_RESULT.equals(command)",
    "BackendAdminProtocol.OPEN_CUBEE.equals(command)"
)) {
    if (-not $backendSources.Contains($required)) {
        throw "Backend source tree is not wired through BackendAdminProtocol: $required"
    }
}

foreach ($forbidden in @(
    'private static final String ADMIN_CHANNEL = "lemonos:admin2"',
    'private static final byte ACCESS_REQUEST_MAGIC = 42',
    'private static final byte ACCESS_ACK_MAGIC = 43',
    'writeUTF("skin-apply")',
    'writeUTF("wake-place")',
    '"skin-result".equals(string)',
    '"open-pad".equals(string)',
    '"open-homepad".equals(string)',
    '"open-honeypad".equals(string)',
    '"saved".equals(string2)',
    '"try-again".equals(string2)',
    'writeByte(42)',
    'byArray[0] == 43'
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still hardcodes backend admin protocol detail: $forbidden"
    }
}

if ($build -notmatch 'Get-ChildItem -Path \$SourceRoot -Recurse -Filter "\*\.java"') {
    throw "Backend build script does not compile backend protocol source files."
}

Write-Host "LemonOS backend admin protocol contract tests passed."
