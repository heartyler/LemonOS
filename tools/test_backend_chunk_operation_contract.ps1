param([string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)))
$ErrorActionPreference = "Stop"
$Plugin = Get-Content -Raw -LiteralPath (Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java")

foreach ($snippet in @(
    "BackendOperationRegistry<String, ChunkOperation> chunkOperations",
    "AtomicLong chunkGenerationCounter",
    "private static final int CHUNK_COMPLETION_VERIFY_ATTEMPTS = 20",
    "private static final class ChunkOperation",
    "this.chunkOperations.beginIfAbsent(chunkDimension.bukkitWorldName, token, operation)",
    "this.chunkOperations.removeIfCurrent(chunkDimension.bukkitWorldName, token)",
    "private void signalChunkCompletion(String world)",
    "this.verifyChunkCompletion(world, operation.token, 1)",
    "if (current.completionVerificationActive) return;",
    "current.completionVerificationActive = true;",
    "if (!this.chunkOperations.isCurrent(world, token)) return;",
    "Boolean running = this.chunkyRunningState(this.chunkyApi, world);",
    "if (attempt < CHUNK_COMPLETION_VERIFY_ATTEMPTS)",
    "this.completeChunkOperation(world, token, true, null);",
    "this.chunkOperations.removeIfCurrent(world, token)",
    "this.chunkParticipant(uuid)"
)) {
    if (-not $Plugin.Contains($snippet)) { throw "Chunk operation migration missing: $snippet" }
}

foreach ($forbidden in @(
    "Map<String, UUID> chunkActors",
    "Map<String, Integer> chunkStoppedConfirmations",
    "Map<String, Integer> chunkProbeFailures",
    "private void handleChunkComplete(String",
    "this.chunkActors.remove"
)) {
    if ($Plugin.Contains($forbidden)) { throw "Legacy Chunky state remains: $forbidden" }
}

$signalStart = $Plugin.IndexOf("private void signalChunkCompletion(String world)")
$signalEnd = $Plugin.IndexOf("private boolean chunkyRunning", $signalStart)
if ($signalStart -lt 0 -or $signalEnd -lt 0) { throw "Could not isolate Chunky completion verification." }
$signalBlock = $Plugin.Substring($signalStart, $signalEnd - $signalStart)
if ($signalBlock.Contains('setChunkStatusForWorld(world, "idle.")')) {
    throw "Raw Chunky callback still performs terminal cleanup before external verification."
}

Write-Host "Backend Chunk operation contract OK"
