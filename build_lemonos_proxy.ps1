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
$Classes = Join-Path $Root "build\proxy-classes"
$VelocityRoot = Join-Path $RuntimeRoot "velocity"
$VelocityPluginDir = Join-Path $VelocityRoot "plugins"
$SourceRoot = Join-Path $Root "src_proxy"
$OutputDir = Join-Path $Root "build\libs"
$OutputJar = Join-Path $OutputDir "lemonos_proxy.jar"

if (Test-Path -LiteralPath $Classes) {
    Remove-Item -LiteralPath $Classes -Recurse -Force
}
if (Test-Path -LiteralPath $OutputJar) {
    Remove-Item -LiteralPath $OutputJar -Force
}
New-Item -ItemType Directory -Path $Classes | Out-Null
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

$VelocityJar = Get-ChildItem -LiteralPath $VelocityRoot -File -Filter "velocity*.jar" |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1
if ($null -eq $VelocityJar) {
    throw "Missing Velocity jar under $VelocityRoot"
}

$Classpath = @()
$Classpath += $VelocityJar.FullName
$Classpath += Get-ChildItem -Path $VelocityPluginDir -Recurse -Filter "*.jar" | Where-Object Name -NotLike "lemonos*.jar" | ForEach-Object FullName

$Sources = Get-ChildItem -Path $SourceRoot -Recurse -Filter "*.java" | ForEach-Object FullName
& $Java -encoding UTF-8 -cp ($Classpath -join ";") -d $Classes $Sources
if ($LASTEXITCODE -ne 0) {
    throw "Proxy javac failed with exit code $LASTEXITCODE"
}
Copy-Item -Path (Join-Path $SourceRoot "velocity-plugin.json") -Destination $Classes -Force
$SourceSnapshot = (& (Join-Path $Root "tools\get_source_snapshot.ps1") -Root $Root).Trim()
Set-Content -LiteralPath (Join-Path $Classes "lemonos-build.properties") -Value "sourceSnapshotSha256=$SourceSnapshot" -Encoding ASCII
& $Jar --create --file $OutputJar -C $Classes .
if ($LASTEXITCODE -ne 0) {
    throw "Proxy jar failed with exit code $LASTEXITCODE"
}

Write-Host $OutputJar
