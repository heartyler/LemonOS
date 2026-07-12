param(
    [string]$RuntimeRoot,
    [string]$JdkRoot = $env:JAVA_HOME
)

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
if ([string]::IsNullOrWhiteSpace($JdkRoot)) {
    throw "JdkRoot is required when JAVA_HOME is not set."
}
$Java = Join-Path $JdkRoot "bin\javac.exe"
$Jar = Join-Path $JdkRoot "bin\jar.exe"
$Classes = Join-Path $Root "build\classes"
$SourceRoot = Join-Path $Root "src\main\java"
$Resources = Join-Path $Root "src\main\resources"
$OutputDir = Join-Path $Root "build\libs"
$OutputJar = Join-Path $OutputDir "lemonos.jar"
$ReleaseVersion = (Get-Content -Raw -LiteralPath (Join-Path $Root "VERSION")).Trim()
if ($ReleaseVersion -notmatch '^\d+\.\d+\.\d+(?:-[0-9A-Za-z.-]+)?$') {
    throw "VERSION is not a supported semantic version: $ReleaseVersion"
}

if (Test-Path -LiteralPath $Classes) {
    Remove-Item -LiteralPath $Classes -Recurse -Force
}
if (Test-Path -LiteralPath $OutputJar) {
    Remove-Item -LiteralPath $OutputJar -Force
}
New-Item -ItemType Directory -Path $Classes | Out-Null
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

$Classpath = @()
if ([string]::IsNullOrWhiteSpace($RuntimeRoot)) {
    $DependencyRoot = Join-Path $Root "third_party\runtime"
    $Classpath += Get-ChildItem -Path $DependencyRoot -File -Filter "*.jar" | ForEach-Object FullName
    foreach ($required in @("paper-api-26.2.build.56-alpha.jar", "velocity.jar", "floodgate-spigot.jar", "joml-1.10.8.jar", "bungeecord-chat-1.21-R0.2-deprecated+build.21.jar")) {
        if (-not (Test-Path -LiteralPath (Join-Path $DependencyRoot $required) -PathType Leaf)) {
            throw "Standalone dependency missing: $required. Run tools\restore_test_dependencies.ps1."
        }
    }
} else {
    $RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path
    $Classpath += Get-ChildItem -Path (Join-Path $RuntimeRoot "lobby\libraries") -Recurse -Filter "*.jar" | ForEach-Object FullName
    $Classpath += Get-ChildItem -Path (Join-Path $RuntimeRoot "lobby\plugins") -Recurse -Filter "*.jar" | Where-Object Name -NotLike "lemonos*.jar" | ForEach-Object FullName
    $Classpath += Get-ChildItem -Path (Join-Path $RuntimeRoot "velocity\plugins") -Recurse -Filter "*.jar" | Where-Object Name -NotLike "lemonos*.jar" | ForEach-Object FullName
}

$Sources = Get-ChildItem -Path $SourceRoot -Recurse -Filter "*.java" | ForEach-Object FullName
& $Java -encoding UTF-8 -cp ($Classpath -join ";") -d $Classes $Sources
if ($LASTEXITCODE -ne 0) {
    throw "Backend javac failed with exit code $LASTEXITCODE"
}
Copy-Item -Path (Join-Path $Resources "*") -Destination $Classes -Recurse -Force
$PluginDescriptor = Join-Path $Classes "plugin.yml"
$PluginDescriptorContent = (Get-Content -Raw -LiteralPath $PluginDescriptor).Replace('${LEMONOS_VERSION}', $ReleaseVersion)
Set-Content -LiteralPath $PluginDescriptor -Value $PluginDescriptorContent -Encoding ASCII -NoNewline
$SourceSnapshot = (& (Join-Path $Root "tools\get_source_snapshot.ps1") -Root $Root).Trim()
Set-Content -LiteralPath (Join-Path $Classes "lemonos-build.properties") -Value "sourceSnapshotSha256=$SourceSnapshot" -Encoding ASCII
& $Jar --create --file $OutputJar -C $Classes .
if ($LASTEXITCODE -ne 0) {
    throw "Backend jar failed with exit code $LASTEXITCODE"
}

Write-Host $OutputJar
