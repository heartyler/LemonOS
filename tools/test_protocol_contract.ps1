param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$protocolPath = Join-Path $Root "src_proxy\dev\lemonos\common\AdminProtocol.java"
$proxyRoot = Join-Path $Root "src_proxy\dev\lemonos\proxy"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

$protocol = Get-Content -Raw -LiteralPath $protocolPath
$proxy = (Get-ChildItem -LiteralPath $proxyRoot -File -Filter "*.java" | Sort-Object Name | ForEach-Object { Get-Content -Raw -LiteralPath $_.FullName }) -join "`n"
$backend = Get-Content -Raw -LiteralPath $backendPath
$backendSources = (Get-ChildItem -LiteralPath (Split-Path -Parent $backendPath) -File -Filter "*.java" | Sort-Object Name | ForEach-Object { Get-Content -Raw -LiteralPath $_.FullName }) -join "`n"

$expected = [ordered]@{
    REQUEST_ACCESS = "request-access"
    ACCESS_STATE = "access-state"
    REQUEST_KEYS = "request-keys"
    KEYS_STATE = "keys-state"
    SET_ACCESS = "set-access"
    ACCESS_SAVED = "access-saved"
    CONNECT_PLACE = "connect-place"
    WAKE_PLACE = "wake-place"
    PLACE_CONNECTED = "place-connected"
    PLACE_UNAVAILABLE = "place-unavailable"
    REQUEST_PEOPLE = "request-people"
    PEOPLE_STATE = "people-state"
    MEET_PLAYER = "meet-player"
    BRING_PLAYER = "bring-player"
    PEOPLE_ACTION_DONE = "people-action-done"
    PEOPLE_ACTION_UNAVAILABLE = "people-action-unavailable"
    SKIN_APPLY = "skin-apply"
    SKIN_RESULT = "skin-result"
    OPEN_CUBEE = "open-cubee"
    ROLE_DEFAULT = "default"
    ROLE_ADMIN = "admin"
    STATUS_READY = "ready"
    STATUS_NOT_READY = "not_ready"
    STATUS_UNAVAILABLE = "unavailable"
    RESULT_SAVED = "saved"
    RESULT_TRY_AGAIN = "try-again"
}

foreach ($entry in $expected.GetEnumerator()) {
    $pattern = "public\s+static\s+final\s+String\s+$([regex]::Escape($entry.Key))\s*=\s*`"$([regex]::Escape($entry.Value))`"\s*;"
    if ($protocol -notmatch $pattern) {
        throw "AdminProtocol missing or changed constant $($entry.Key)=$($entry.Value)."
    }
}

$messageNames = @(
    "request-access",
    "access-state",
    "request-keys",
    "keys-state",
    "set-access",
    "access-saved",
    "connect-place",
    "wake-place",
    "place-connected",
    "place-unavailable",
    "request-people",
    "people-state",
    "meet-player",
    "bring-player",
    "people-action-done",
    "people-action-unavailable",
    "skin-apply",
    "skin-result",
    "open-cubee"
)

foreach ($name in $messageNames) {
    if ($proxy.Contains("`"$name`"")) {
        throw "Proxy source hardcodes protocol message name instead of AdminProtocol: $name"
    }
}

foreach ($name in @("SKIN_APPLY", "SKIN_RESULT", "WAKE_PLACE", "OPEN_CUBEE")) {
    if (-not $backendSources.Contains("BackendAdminProtocol.$name")) {
        throw "Backend protocol surface is not wired through BackendAdminProtocol.$name"
    }
}

if ($proxy -notmatch "AdminProtocol\.REQUEST_ACCESS" -or
    $proxy -notmatch "AdminProtocol\.SKIN_RESULT" -or
    $proxy -notmatch "AdminProtocol\.OPEN_CUBEE") {
    throw "Proxy protocol handling is not wired through AdminProtocol constants."
}

foreach ($legacy in @("OPEN_HOMEPAD", "OPEN_HONEYPAD", "OPEN_PAD", "open-homepad", "open-honeypad", "open-pad")) {
    if ($protocol.Contains($legacy) -or $proxy.Contains($legacy) -or $backendSources.Contains($legacy)) {
        throw "Legacy Cubee protocol alias remains: $legacy"
    }
}

Write-Host "LemonOS protocol contract tests passed."
