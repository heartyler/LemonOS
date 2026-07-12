param([string]$Root = (Resolve-Path ".").Path)
$ErrorActionPreference = "Stop"
$source = Get-Content -Raw (Join-Path $Root "src\main\java\dev\lemonos\BackendBoardChunkLeaseService.java")
$plugin = Get-Content -Raw (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")
foreach ($required in @('recoverOwnedChunks()', 'recoverWorld(World world)', 'cleanupLegacyForcedChunksIfRequested()', 'beginCycle()',
    'acquire(World world, int x, int z)', 'endCycle()', 'releaseAll()', 'world.isChunkForceLoaded(x, z)')) {
    if (-not $source.Contains($required)) { throw "Board chunk lease missing: $required" }
}
foreach ($required in @('this.boardChunkLeaseService.beginCycle()', 'this.boardChunkLeaseService.endCycle()',
    'this.boardChunkLeaseService.releaseAll()', 'this.boardChunkLeaseService.acquire(world')) {
    if (-not $plugin.Contains($required)) { throw "Board chunk lease integration missing: $required" }
}
Write-Host "LemonOS Board chunk lease contract tests passed."
