param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
foreach ($buildScript in @("build_lemonos_backend.ps1", "build_lemonos_proxy.ps1")) {
    $content = Get-Content -Raw -LiteralPath (Join-Path $Root $buildScript)
    foreach ($flag in @("-Xlint:deprecation", "-Xlint:unchecked", "-Werror")) {
        if (-not $content.Contains($flag)) { throw "$buildScript does not enforce $flag." }
    }
}
$backend = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($forbidden in @("GameRule.DO_FIRE_TICK", "DamageCause.HOT_FLOOR", "(Consumer)consumer", 'getMethod("clickedButtonId"')) {
    if ($backend.Contains($forbidden)) { throw "Backend warning-prone API remains: $forbidden" }
}
foreach ($required in @("GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER", "Consumer<SimpleFormResponse>", "response.clickedButtonId()")) {
    if (-not $backend.Contains($required)) { throw "Backend typed replacement missing: $required" }
}
$proxyPlugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\dev\lemonos\proxy\LemonOSProxyPlugin.java")
$proxyCommand = Get-Content -Raw -LiteralPath (Join-Path $Root "src_proxy\dev\lemonos\proxy\ProxyAccessCommandService.java")
foreach ($forbidden in @("kickedDuringLogin()", "LiteralArgumentBuilder literalArgumentBuilder", "(ArgumentType)")) {
    if ($proxyPlugin.Contains($forbidden) -or $proxyCommand.Contains($forbidden)) { throw "Proxy warning-prone API remains: $forbidden" }
}
foreach ($required in @("kickedDuringServerConnect()", "register(accessCommandMeta, accessCommand)", "LiteralArgumentBuilder<CommandSource>")) {
    if (-not $proxyPlugin.Contains($required) -and -not $proxyCommand.Contains($required)) { throw "Proxy typed replacement missing: $required" }
}
Write-Host "Java lint gate contract OK"
