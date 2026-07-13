param(
    [string]$Root = (Resolve-Path ".").Path,
    [string]$JdkRoot = $(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-26.0.1" })
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
    "interface AddressResolver",
    "AddressResolver addressResolver",
    "int port(String place)",
    "boolean canConnect(String place)",
    "boolean startServer(String place)",
    "socket.connect(address, 300)",
    "resolved.isLoopbackAddress()",
    "reportedResolutionFailures",
    "server is not registered in Velocity",
    "registered address is not loopback",
    'resolve(".honeydock").resolve("launchers").resolve(normalizedPlace + ".bat")',
    'resolve("honeydock.bat")',
    "startHoneyDockLauncher(normalizedPlace, launcher, serverDirectory)",
    "startHoneyDockEntrypoint(normalizedPlace, entrypoint)",
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

foreach ($forbiddenPort in @(
    'return 30066;',
    'return 30067;',
    'return 30068;'
)) {
    if ($probe.Contains($forbiddenPort)) {
        throw "PlaceRuntimeProbe must not hardcode a runtime port mapping: $forbiddenPort"
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

if (-not $proxy.Contains("new PlaceRuntimeProbe(this.sharedDataFolder.getParent(), this.logger, place -> this.server.getServer(place)") -or
    $proxy -notmatch "this\.placeRuntimeProbe\.port\(string\)" -or
    $proxy -notmatch "this\.placeRuntimeProbe\.canConnect\(string\)" -or
    $proxy -notmatch "this\.placeRuntimeProbe\.startServer\(string\)") {
    throw "LemonOSProxyPlugin is not wired through PlaceRuntimeProbe."
}

$Classes = Join-Path $Root "build\test-place-runtime-probe"
$VelocityJar = Join-Path $Root "third_party\runtime\velocity.jar"
if (-not (Test-Path -LiteralPath $VelocityJar -PathType Leaf)) {
    throw "Velocity test dependency missing. Run tools\restore_test_dependencies.ps1."
}
if (Test-Path -LiteralPath $Classes) { Remove-Item -LiteralPath $Classes -Recurse -Force }
New-Item -ItemType Directory -Path $Classes -Force | Out-Null
& (Join-Path $JdkRoot "bin\javac.exe") -Xlint:all -Werror -encoding UTF-8 -cp $VelocityJar -d $Classes `
    $probePath `
    (Join-Path $Root "tools\java\dev\lemonos\proxy\PlaceRuntimeProbeHarness.java")
if ($LASTEXITCODE -ne 0) { throw "PlaceRuntimeProbe behavior harness compilation failed." }
& (Join-Path $JdkRoot "bin\java.exe") -cp "$Classes;$VelocityJar" dev.lemonos.proxy.PlaceRuntimeProbeHarness
if ($LASTEXITCODE -ne 0) { throw "PlaceRuntimeProbe behavior harness failed." }

Write-Host "LemonOS place runtime probe contract tests passed."
