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
$Classes = Join-Path $Root "build\proxy-classes"
$SourceRoot = Join-Path $Root "src_proxy"
$OutputDir = Join-Path $Root "build\libs"
$OutputJar = Join-Path $OutputDir "lemonos_proxy.jar"
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
    $VelocityJar = Join-Path $Root "third_party\runtime\velocity.jar"
    if (-not (Test-Path -LiteralPath $VelocityJar -PathType Leaf)) {
        throw "Standalone dependency missing: velocity.jar. Run tools\restore_test_dependencies.ps1."
    }
    $Classpath += $VelocityJar
} else {
    $RuntimeRoot = (Resolve-Path -LiteralPath $RuntimeRoot).Path
    $VelocityRoot = Join-Path $RuntimeRoot "velocity"
    $VelocityJar = Get-ChildItem -LiteralPath $VelocityRoot -File -Filter "velocity*.jar" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($null -eq $VelocityJar) { throw "Missing Velocity jar under $VelocityRoot" }
    $Classpath += $VelocityJar.FullName
    $Classpath += Get-ChildItem -Path (Join-Path $VelocityRoot "plugins") -Recurse -Filter "*.jar" | Where-Object Name -NotLike "lemonos*.jar" | ForEach-Object FullName
}

$GeneratedSourceRoot = Join-Path $Root "build\generated-sources\proxy\dev\lemonos\common"
$GeneratedVersionSource = Join-Path $GeneratedSourceRoot "LemonOSBuildVersion.java"
& (Join-Path $Root "tools\write_version_source.ps1") -Root $Root -OutputPath $GeneratedVersionSource | Out-Null
$Sources = @(Get-ChildItem -Path $SourceRoot -Recurse -Filter "*.java" | ForEach-Object FullName)
$Sources += $GeneratedVersionSource
& $Java -encoding UTF-8 -cp ($Classpath -join ";") -d $Classes $Sources
if ($LASTEXITCODE -ne 0) {
    throw "Proxy javac failed with exit code $LASTEXITCODE"
}
Copy-Item -Path (Join-Path $SourceRoot "velocity-plugin.json") -Destination $Classes -Force
$ProxyDescriptor = Join-Path $Classes "velocity-plugin.json"
$ProxyDescriptorContent = (Get-Content -Raw -LiteralPath $ProxyDescriptor).Replace('${LEMONOS_VERSION}', $ReleaseVersion)
Set-Content -LiteralPath $ProxyDescriptor -Value $ProxyDescriptorContent -Encoding ASCII -NoNewline
$SourceSnapshot = (& (Join-Path $Root "tools\get_source_snapshot.ps1") -Root $Root).Trim()
Set-Content -LiteralPath (Join-Path $Classes "lemonos-build.properties") -Value "sourceSnapshotSha256=$SourceSnapshot" -Encoding ASCII
& $Jar --create --file $OutputJar -C $Classes .
if ($LASTEXITCODE -ne 0) {
    throw "Proxy jar failed with exit code $LASTEXITCODE"
}

Write-Host $OutputJar
