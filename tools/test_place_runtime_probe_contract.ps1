param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$probePath = Join-Path $Root "src_proxy\dev\lemonos\proxy\PlaceRuntimeProbe.java"
$proxyPath = Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java"

if (-not (Test-Path -LiteralPath $probePath -PathType Leaf)) {
    throw "Missing proxy PlaceRuntimeProbe."
}

$probe = Get-Content -Raw -LiteralPath $probePath
$proxy = Get-Content -Raw -LiteralPath $proxyPath

foreach ($required in @(
    "final class PlaceRuntimeProbe",
    "int port(String place)",
    "boolean canConnect(int port)",
    "boolean startServer(String place)",
    "new InetSocketAddress(`"127.0.0.1`", port), 300",
    'resolve(".honeydock").resolve("launchers").resolve(place + ".bat")',
    'resolve("honeydock.bat")',
    "startHoneyDockLauncher(place, launcher, serverDirectory)",
    "startHoneyDockEntrypoint(place, entrypoint)",
    "Start-Process -FilePath 'cmd.exe'",
    "powerShellLiteral(launcher.toString())",
    "powerShellLiteral(serverDirectory.toString())",
    "powerShellLiteral(entrypoint.getParent().toString())",
    "-WindowStyle Hidden",
    "powerShellLiteral"
)) {
    if (-not $probe.Contains($required)) {
        throw "PlaceRuntimeProbe missing required runtime contract text: $required"
    }
}

if ($probe.Contains('resolve(place).resolve("start.bat")')) {
    throw "PlaceRuntimeProbe must not depend on per-server start.bat files."
}

foreach ($requiredPort in @(
    'return 30066;',
    'return 30067;',
    'return 30068;'
)) {
    if (-not $probe.Contains($requiredPort)) {
        throw "PlaceRuntimeProbe missing expected port mapping: $requiredPort"
    }
}

foreach ($forbidden in @(
    "new InetSocketAddress",
    "new Socket()",
    "new ProcessBuilder",
    "Start-Process -FilePath",
    'resolve(place).resolve("start.bat")'
)) {
    if ($proxy.Contains($forbidden)) {
        throw "LemonOSProxyPlugin still owns place runtime probe detail: $forbidden"
    }
}

if ($probe.Contains("com.velocitypowered.api.proxy.Player") -or
    $probe.Contains("com.velocitypowered.api.proxy.server.RegisteredServer")) {
    throw "PlaceRuntimeProbe must not depend on Velocity runtime objects."
}

if ($proxy -notmatch "new PlaceRuntimeProbe\(this\.sharedDataFolder\.getParent\(\), this\.logger\)" -or
    $proxy -notmatch "this\.placeRuntimeProbe\.port\(string\)" -or
    $proxy -notmatch "this\.placeRuntimeProbe\.canConnect\(n\)" -or
    $proxy -notmatch "this\.placeRuntimeProbe\.startServer\(string\)") {
    throw "LemonOSProxyPlugin is not wired through PlaceRuntimeProbe."
}

Write-Host "LemonOS place runtime probe contract tests passed."
