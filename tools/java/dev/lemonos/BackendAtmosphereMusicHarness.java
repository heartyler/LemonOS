package dev.lemonos;

import java.util.List;
import java.util.UUID;

public final class BackendAtmosphereMusicHarness {
    public static void main(String[] args) {
        BackendAtmosphereMusicOrchestrationService orchestration = new BackendAtmosphereMusicOrchestrationService();
        BackendAtmosphereMusicOrchestrationService.Schedule lobby = orchestration.schedule(true, true);
        require(lobby.active() && lobby.initialDelayTicks() == 20L && lobby.periodTicks() == 20L,
                "Lobby music schedule must tick once per second.");
        require(!orchestration.schedule(false, true).active(), "Non-Lobby must not start music.");
        require(!orchestration.schedule(true, false).active(), "Disabled music must not start.");
        require(orchestration.shouldTick(true, true), "Enabled Lobby music must tick.");
        require(!orchestration.shouldTick(false, true), "Music must stop outside Lobby.");

        BackendAtmosphereMusicService service = new BackendAtmosphereMusicService();
        long now = 1_000L;
        require(service.waitingForNextTrack(now, 8), "First tick must establish the initial delay.");
        require(service.waitingForNextTrack(now + 7_999L, 8), "Initial delay must not finish early.");
        require(!service.waitingForNextTrack(now + 8_000L, 8), "Initial delay must finish on time.");

        BackendAtmosphereMusicService.Track track = new BackendAtmosphereMusicService.Track(
                "MUSIC_DISC_CAT", "music_disc.cat", "warm", 185);
        BackendAtmosphereMusicService.Group group = new BackendAtmosphereMusicService.Group("warm", 80, List.of(track));
        require(service.pickTrack(List.of(group)) == track, "Single eligible track must be selected.");

        long started = now + 8_000L;
        service.finishTrackAttempt(track, started, 20, true);
        require("music_disc.cat".equals(service.currentSound()), "Played sound must be tracked for cleanup.");
        require(service.refreshActionBarTrack(started, 20) == track, "Current track must remain active.");
        require(service.refreshActionBarTrack(started + 500L, 20) == null, "Action Bar refresh must be rate limited.");
        require(service.refreshActionBarTrack(started + 1_000L, 20) == track, "Action Bar must refresh on cadence.");
        require(service.waitingForNextTrack(started + 204_999L, 8), "Track plus gap must not finish early.");
        require(!service.waitingForNextTrack(started + 205_000L, 8), "Track plus gap must finish on time.");

        UUID player = UUID.randomUUID();
        service.delayActionBarResume(player, started, 80);
        require(service.actionBarResumeDelayed(player, started + 3_999L), "Resume suppression must remain active.");
        require(!service.actionBarResumeDelayed(player, started + 4_000L), "Resume suppression must expire exactly.");
        service.reset();
        require(service.currentSound() == null, "Reset must release current sound state.");

        System.out.println("Backend Atmosphere music behavior harness passed.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
