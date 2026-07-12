param(
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path))
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$TemplateRoot = Join-Path $Root "templates\runtime\LemonOS"
$ResourceRoot = Join-Path $Root "src\main\resources\defaults\LemonOS"
foreach ($name in @("config.yml", "messages.yml", "places.yml", "sandbox.yml", "survival.yml", "boards.yml", "atmosphere.yml")) {
    $template = Join-Path $TemplateRoot $name
    $resource = Join-Path $ResourceRoot $name
    if (-not (Test-Path -LiteralPath $resource -PathType Leaf)) {
        throw "Standard bundled LemonOS preset is missing: $name"
    }
    $templateText = (Get-Content -Raw -LiteralPath $template).Replace("`r`n", "`n")
    $resourceText = (Get-Content -Raw -LiteralPath $resource).Replace("`r`n", "`n")
    if ($templateText -ne $resourceText) {
        throw "Runtime and bundled LemonOS presets drifted: $name"
    }
}

$boards = Get-Content -Raw -LiteralPath (Join-Path $TemplateRoot "boards.yml")
$atmosphere = Get-Content -Raw -LiteralPath (Join-Path $TemplateRoot "atmosphere.yml")
foreach ($required in @(
    "boards.stayed-close.display.bedrock.bottom-line-width",
    "boards.made-room.scoring.track-blocks-changed",
    "atmosphere.activity.session-minutes.cooldown-seconds",
    "atmosphere.music.track-seconds.MUSIC_DISC_OTHERSIDE"
)) {
    $parts = $required.Split('.')
    $leaf = $parts[-1] + ":"
    $document = if ($required.StartsWith("boards.")) { $boards } else { $atmosphere }
    if (-not $document.Contains($leaf)) { throw "Canonical preset missing final default: $required" }
}

$bootstrap = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\config\BackendConfigBootstrapService.java")
if (-not $bootstrap.Contains('this.plugin.getResource("defaults/LemonOS/" + name)')) {
    throw "Config bootstrap does not load standard bundled presets."
}
Write-Host "Backend standard config preset resources contract OK"
