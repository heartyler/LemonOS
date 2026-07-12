param(
    [Parameter(Mandatory = $true)][string]$RuntimeRoot,
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
$Lib = Join-Path $RuntimeRoot "lobby\libraries"
$PluginDir = Join-Path $RuntimeRoot "lobby\plugins"
$VelocityPluginDir = Join-Path $RuntimeRoot "velocity\plugins"
$SourceRoot = Join-Path $Root "src\main\java"
$Resources = Join-Path $Root "src\main\resources"
$OutputDir = Join-Path $Root "build\libs"
$OutputJar = Join-Path $OutputDir "lemonos.jar"

if (Test-Path -LiteralPath $Classes) {
    Remove-Item -LiteralPath $Classes -Recurse -Force
}
if (Test-Path -LiteralPath $OutputJar) {
    Remove-Item -LiteralPath $OutputJar -Force
}
New-Item -ItemType Directory -Path $Classes | Out-Null
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

$Classpath = @()
$Classpath += Get-ChildItem -Path $Lib -Recurse -Filter "*.jar" | ForEach-Object FullName
$Classpath += Get-ChildItem -Path $PluginDir -Recurse -Filter "*.jar" | Where-Object Name -NotLike "lemonos*.jar" | ForEach-Object FullName
$Classpath += Get-ChildItem -Path $VelocityPluginDir -Recurse -Filter "*.jar" | Where-Object Name -NotLike "lemonos*.jar" | ForEach-Object FullName

$Sources = Get-ChildItem -Path $SourceRoot -Recurse -Filter "*.java" | ForEach-Object FullName
& $Java -encoding UTF-8 -cp ($Classpath -join ";") -d $Classes $Sources
if ($LASTEXITCODE -ne 0) {
    throw "Backend javac failed with exit code $LASTEXITCODE"
}
Copy-Item -Path (Join-Path $Resources "*") -Destination $Classes -Recurse -Force
$SourceSnapshot = (& (Join-Path $Root "tools\get_source_snapshot.ps1") -Root $Root).Trim()
Set-Content -LiteralPath (Join-Path $Classes "lemonos-build.properties") -Value "sourceSnapshotSha256=$SourceSnapshot" -Encoding ASCII
& $Jar --create --file $OutputJar -C $Classes .
if ($LASTEXITCODE -ne 0) {
    throw "Backend jar failed with exit code $LASTEXITCODE"
}

Write-Host $OutputJar
