param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAtmosphereActionBarService.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendAtmosphereActionBarService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$backend = Get-Content -Raw -LiteralPath $backendPath

foreach ($required in @(
    "final class BackendAtmosphereActionBarService",
    "private final Map<UUID, AtmosphereState> atmosphereStates = new ConcurrentHashMap<UUID, AtmosphereState>()",
    "void clear()",
    "void remove(UUID uuid)",
    "boolean periodicReady(UUID uuid, long nowMillis, int cooldownSeconds)",
    "state.lastShownMillis = nowMillis",
    "state.activeMessage == null && nowMillis - state.lastShownMillis >= (long)cooldownSeconds * 1000L",
    "boolean showReady(UUID uuid, long nowMillis, int cooldownSeconds)",
    "boolean hasActiveMessage(UUID uuid, long nowMillis)",
    "String pickMessage(UUID uuid, String key, List<String> messages)",
    "candidates.add(message.trim())",
    "state.lastMessages.put(key, candidates.get(0))",
    "ThreadLocalRandom.current().nextInt(candidates.size())",
    "alternates.remove(lastMessage)",
    "void activate(UUID uuid, String message, long nowMillis, int durationSeconds, boolean markShown)",
    "state.activeUntilMillis = nowMillis + (long)durationSeconds * 1000L",
    "state.lastRepeatMillis = nowMillis",
    "List<RepeatMessage> repeatMessages(long nowMillis, int repeatSeconds)",
    "state.activeMessage = null",
    "messages.add(new RepeatMessage(entry.getKey(), state.activeMessage))",
    "void markRepeated(UUID uuid, long nowMillis)",
    "static final class RepeatMessage",
    "private static final class AtmosphereState"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendAtmosphereActionBarService missing required actionbar contract text: $required"
    }
}

foreach ($required in @(
    "private BackendAtmosphereActionBarService atmosphereActionBarService",
    "this.atmosphereActionBarService = new BackendAtmosphereActionBarService()",
    "this.atmosphereActionBarService.clear()",
    "this.atmosphereActionBarService.remove(playerQuitEvent.getPlayer().getUniqueId())",
    "this.atmosphereActionBarService.periodicReady(player.getUniqueId(), l, this.atmosphereCooldownSeconds())",
    "for (BackendAtmosphereActionBarService.RepeatMessage repeatMessage : this.atmosphereActionBarService.repeatMessages(l, this.atmosphereRepeatSeconds()))",
    "this.atmosphereActionBarService.markRepeated(repeatMessage.uuid(), l)",
    'this.notifyActionBar(player, BackendActionBarCoordinator.Owner.ATMOSPHERE',
    'this.monotonicMillis()',
    "this.atmosphereActionBarService.showReady(uUID, l, this.atmosphereCooldownSeconds())",
    "this.atmosphereActionBarService.activate(uUID, string2, l, this.atmosphereDurationSeconds(), true)",
    "this.atmosphereActionBarService.hasActiveMessage(uUID, l)",
    "this.atmosphereActionBarService.activate(uUID, string2, l, this.atmosphereDurationSeconds(), false)",
    "return this.atmosphereActionBarService.pickMessage(uUID, string, list)"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendAtmosphereActionBarService: $required"
    }
}

foreach ($required in @(
    'this.setMissing(this.config, "atmosphere.music.actionbar.enabled", false)',
    'this.atmosphereConfig.musicActionBarEnabled()',
    'return "Lena Raine - Creator";'
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin missing approved music presentation default: $required"
    }
}

if ($backend.Contains('Lena Raine - Creator (music box)')) {
    throw "LemonOSPlugin still contains the superseded Creator music-box suffix."
}

foreach ($forbidden in @(
    "private final Map<UUID, AtmosphereState> atmosphereStates",
    "this.atmosphereStates.clear",
    "this.atmosphereStates.remove",
    "this.atmosphereStates.computeIfAbsent",
    "private static final class AtmosphereState"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns atmosphere actionbar state detail: $forbidden"
    }
}

Write-Host "LemonOS backend atmosphere actionbar contract tests passed."
