param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path -LiteralPath $Root).Path
$servicePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAtmosphereMusicService.java"
$lifecyclePath = Join-Path $Root "src\main\java\dev\lemonos\BackendAtmosphereMusicLifecycleService.java"
$orchestrationPath = Join-Path $Root "src\main\java\dev\lemonos\BackendAtmosphereMusicOrchestrationService.java"
$configPath = Join-Path $Root "src\main\java\dev\lemonos\BackendAtmosphereConfig.java"
$backendPath = Join-Path $Root "src\main\java\dev\lemonos\LemonOSPlugin.java"

if (-not (Test-Path -LiteralPath $servicePath -PathType Leaf)) {
    throw "Missing backend BackendAtmosphereMusicService."
}

$service = Get-Content -Raw -LiteralPath $servicePath
$lifecycle = Get-Content -Raw -LiteralPath $lifecyclePath
$orchestration = Get-Content -Raw -LiteralPath $orchestrationPath
$config = Get-Content -Raw -LiteralPath $configPath
$backend = Get-Content -Raw -LiteralPath $backendPath

if (-not $lifecycle.Contains("extends BackendRepeatingLifecycleService")) { throw "Lobby music does not own a lifecycle." }
foreach ($required in @("Schedule schedule(boolean lobby, boolean musicEnabled)", "return lobby && musicEnabled", "boolean shouldTick(boolean lobby, boolean musicEnabled)")) {
    if (-not $orchestration.Contains($required)) { throw "Lobby music orchestration missing: $required" }
}
if (-not $config.Contains('this.source.getStringList(ROOT + "music.tracks." + key)')) { throw "atmosphere.yml does not own music tracks." }

foreach ($required in @(
    "final class BackendAtmosphereMusicService",
    "private final LobbyMusicState lobbyMusicState = new LobbyMusicState()",
    "private final Map<UUID, Long> actionBarSuppressedUntilMillis = new ConcurrentHashMap<UUID, Long>()",
    "void reset()",
    "boolean waitingForNextTrack(long nowMillis, int delaySeconds)",
    "this.lobbyMusicState.nextTrackMillis = nowMillis + (long)delaySeconds * 1000L",
    "void markNoTrack(long nowMillis)",
    "this.lobbyMusicState.nextTrackMillis = nowMillis + 60000L",
    "Track pickTrack(List<Group> groups)",
    "weightedGroups.add(group.key())",
    "candidates.removeIf(track -> track.key().equalsIgnoreCase(this.lobbyMusicState.lastTrack))",
    "ThreadLocalRandom.current().nextInt(candidates.size())",
    "void finishTrackAttempt(Track track, long nowMillis, int gapSeconds, boolean played)",
    "this.lobbyMusicState.currentTrackUntilMillis = nowMillis + (long)track.seconds() * 1000L",
    "this.lobbyMusicState.recentGroups.addLast(track.group())",
    "this.lobbyMusicState.nextTrackMillis = nowMillis + (long)(track.seconds() + gapSeconds) * 1000L",
    "Track refreshActionBarTrack(long nowMillis, int refreshTicks)",
    "long refreshMillis = (long)refreshTicks * 50L",
    "boolean actionBarResumeDelayed(UUID uuid, long nowMillis)",
    "this.actionBarSuppressedUntilMillis.remove(uuid, untilMillis)",
    "void delayActionBarResume(UUID uuid, long nowMillis, int delayTicks)",
    "this.actionBarSuppressedUntilMillis.put(uuid, nowMillis + (long)delayTicks * 50L)",
    "void removeActionBarSuppression(UUID uuid)",
    "String currentSound()",
    'if ("playful".equals(group))',
    "checked < 2",
    'if ("special".equals(group))',
    "checked < 8",
    "static final class Group",
    "static final class Track",
    "private static final class LobbyMusicState"
)) {
    if (-not $service.Contains($required)) {
        throw "BackendAtmosphereMusicService missing required music contract text: $required"
    }
}

foreach ($required in @(
    "private BackendAtmosphereMusicService atmosphereMusicService",
    "private BackendAtmosphereMusicLifecycleService atmosphereMusicLifecycleService",
    "private BackendAtmosphereMusicOrchestrationService atmosphereMusicOrchestrationService",
    "this.atmosphereMusicService = new BackendAtmosphereMusicService()",
    "this.startAtmosphereMusicTask()",
    "this.atmosphereMusicLifecycleService.stop()",
    'this.atmosphereMusicLifecycleService.start(schedule.initialDelayTicks(), schedule.periodTicks()',
    "this.atmosphereMusicService.reset()",
    "this.atmosphereMusicService.waitingForNextTrack(l, this.atmosphereMusicDelaySeconds())",
    "this.atmosphereMusicService.pickTrack(this.atmosphereMusicGroups())",
    "this.atmosphereMusicService.markNoTrack(l)",
    "this.atmosphereMusicService.finishTrackAttempt(atmosphereTrack, l, this.atmosphereMusicGapSeconds(), bl)",
    "private List<BackendAtmosphereMusicService.Group> atmosphereMusicGroups()",
    "new BackendAtmosphereMusicService.Track(track, this.normalizeAtmosphereMusicSound(track), group, this.atmosphereMusicTrackSeconds(track))",
    "new BackendAtmosphereMusicService.Group(group, this.atmosphereMusicWeight(group), tracks)",
    "private boolean playAtmosphereMusic(Player player, BackendAtmosphereMusicService.Track atmosphereTrack)",
    "player.playSound((Entity)player, atmosphereTrack.sound(), SoundCategory.RECORDS, f, f2)",
    "player.stopSound(SoundCategory.MUSIC)",
    "player.stopSound(string, SoundCategory.RECORDS)",
    "this.isAtmosphereMusicSoundAvailable(atmosphereTrack.sound())",
    'this.logOperationFailure("music-play-" + atmosphereTrack.sound()',
    'this.logOperationFailure("music-stop-" + player.getUniqueId()',
    "private void showAtmosphereMusicActionBar(Player player, BackendAtmosphereMusicService.Track atmosphereTrack)",
    "this.atmosphereMusicService.refreshActionBarTrack(l, this.atmosphereMusicActionBarRefreshTicks())",
    "this.atmosphereMusicService.actionBarResumeDelayed(uUID, this.monotonicMillis())",
    "this.atmosphereMusicService.delayActionBarResume(uUID, this.monotonicMillis(), this.atmosphereMusicActionBarResumeDelayTicks())",
    "private String atmosphereMusicDisplayName(BackendAtmosphereMusicService.Track atmosphereTrack)",
    "this.atmosphereMusicService.removeActionBarSuppression(player.getUniqueId())",
    "String currentSound = this.atmosphereMusicService.currentSound()"
)) {
    if (-not $backend.Contains($required)) {
        throw "LemonOSPlugin is not wired through BackendAtmosphereMusicService: $required"
    }
}

foreach ($forbidden in @(
    "private final Map<UUID, Long> atmosphereMusicActionBarSuppressedUntilMillis",
    "private final LobbyMusicState lobbyMusicState",
    "private AtmosphereTrack pickAtmosphereTrack",
    "private int addAtmosphereMusicGroup",
    "private boolean canUseAtmosphereMusicGroup",
    "private static final class LobbyMusicState",
    "private static final class AtmosphereTrack",
    "ThreadLocalRandom.current().nextInt",
    'this.config.getStringList("atmosphere.music.tracks.',
    "player.playSound((Entity)player, atmosphereTrack.sound(), f, f2)",
    "player.stopSound(string);"
)) {
    if ($backend.Contains($forbidden)) {
        throw "LemonOSPlugin still owns atmosphere music state detail: $forbidden"
    }
}

Write-Host "LemonOS backend atmosphere music contract tests passed."
