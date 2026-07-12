param(
    [string]$Root = (Split-Path -Parent $PSScriptRoot)
)

$ErrorActionPreference = "Stop"
$Root = [System.IO.Path]::GetFullPath($Root)
$toolsRoot = Join-Path $Root "tools"

$tests = @(
    Get-ChildItem -LiteralPath $toolsRoot -File -Filter "test_*_contract.ps1" |
        Sort-Object Name
)
$sourceTest = Join-Path $toolsRoot "test_lemonos_backend_source.ps1"
if (Test-Path -LiteralPath $sourceTest -PathType Leaf) {
    $tests += Get-Item -LiteralPath $sourceTest
}

$passed = 0
$failures = New-Object System.Collections.Generic.List[object]
foreach ($test in $tests) {
    $output = & powershell.exe -NoProfile -ExecutionPolicy Bypass -File $test.FullName -Root $Root 2>&1
    $exitCode = $LASTEXITCODE
    if ($exitCode -eq 0) {
        $passed++
        continue
    }
    $failures.Add([pscustomobject]@{
        test = $test.Name
        exitCode = $exitCode
        output = ($output | Out-String).Trim()
    })
}

Write-Host "LemonOS contracts: $passed passed, $($failures.Count) failed."
foreach ($failure in $failures) {
    Write-Host ""
    Write-Host "[FAILED] $($failure.test) (exit $($failure.exitCode))"
    Write-Host $failure.output
}

if ($failures.Count -gt 0) {
    exit 1
}
