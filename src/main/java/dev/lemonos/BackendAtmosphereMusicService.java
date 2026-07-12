/*
 * Backend-side LemonOS atmosphere music state and track selection.
 */
package dev.lemonos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

final class BackendAtmosphereMusicService {
    private final LobbyMusicState lobbyMusicState = new LobbyMusicState();
    private final Map<UUID, Long> actionBarSuppressedUntilMillis = new ConcurrentHashMap<UUID, Long>();

    void reset() {
        this.actionBarSuppressedUntilMillis.clear();
        this.lobbyMusicState.reset();
    }

    boolean waitingForNextTrack(long nowMillis, int delaySeconds) {
        if (this.lobbyMusicState.nextTrackMillis <= 0L) {
            this.lobbyMusicState.nextTrackMillis = nowMillis + (long)delaySeconds * 1000L;
            return true;
        }
        return nowMillis < this.lobbyMusicState.nextTrackMillis;
    }

    void markNoTrack(long nowMillis) {
        this.lobbyMusicState.nextTrackMillis = nowMillis + 60000L;
    }

    Track pickTrack(List<Group> groups) {
        ArrayList<String> weightedGroups = new ArrayList<String>();
        int totalWeight = 0;
        for (Group group : groups == null ? List.<Group>of() : groups) {
            if (group == null || !this.canUseGroup(group.key()) || group.weight() <= 0 || group.tracks().isEmpty()) {
                continue;
            }
            totalWeight += group.weight();
            for (int i = 0; i < group.weight(); ++i) {
                weightedGroups.add(group.key());
            }
        }
        if (weightedGroups.isEmpty() || totalWeight <= 0) {
            return null;
        }
        String groupKey = weightedGroups.get(ThreadLocalRandom.current().nextInt(weightedGroups.size()));
        Group selected = null;
        for (Group group : groups) {
            if (group != null && groupKey.equals(group.key())) {
                selected = group;
                break;
            }
        }
        if (selected == null || selected.tracks().isEmpty()) {
            return null;
        }
        ArrayList<Track> candidates = new ArrayList<Track>(selected.tracks());
        if (candidates.size() > 1) {
            candidates.removeIf(track -> track.key().equalsIgnoreCase(this.lobbyMusicState.lastTrack));
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    void finishTrackAttempt(Track track, long nowMillis, int gapSeconds, boolean played) {
        if (track == null) {
            return;
        }
        if (played) {
            this.lobbyMusicState.currentTrack = track;
            this.lobbyMusicState.currentTrackUntilMillis = nowMillis + (long)track.seconds() * 1000L;
            this.lobbyMusicState.lastActionBarMillis = 0L;
            this.lobbyMusicState.currentSound = track.sound();
            this.lobbyMusicState.lastTrack = track.key();
            this.lobbyMusicState.recentGroups.addLast(track.group());
            while (this.lobbyMusicState.recentGroups.size() > 10) {
                this.lobbyMusicState.recentGroups.removeFirst();
            }
        } else {
            this.lobbyMusicState.currentTrack = null;
            this.lobbyMusicState.currentTrackUntilMillis = 0L;
            this.lobbyMusicState.lastActionBarMillis = 0L;
        }
        this.lobbyMusicState.nextTrackMillis = nowMillis + (long)(track.seconds() + gapSeconds) * 1000L;
    }

    Track refreshActionBarTrack(long nowMillis, int refreshTicks) {
        Track track = this.lobbyMusicState.currentTrack;
        if (track == null || nowMillis > this.lobbyMusicState.currentTrackUntilMillis) {
            this.lobbyMusicState.currentTrack = null;
            this.lobbyMusicState.currentTrackUntilMillis = 0L;
            this.lobbyMusicState.lastActionBarMillis = 0L;
            return null;
        }
        long refreshMillis = (long)refreshTicks * 50L;
        if (this.lobbyMusicState.lastActionBarMillis > 0L && nowMillis - this.lobbyMusicState.lastActionBarMillis < refreshMillis) {
            return null;
        }
        this.lobbyMusicState.lastActionBarMillis = nowMillis;
        return track;
    }

    boolean actionBarResumeDelayed(UUID uuid, long nowMillis) {
        if (uuid == null) {
            return false;
        }
        Long untilMillis = this.actionBarSuppressedUntilMillis.get(uuid);
        if (untilMillis == null) {
            return false;
        }
        if (nowMillis < untilMillis) {
            return true;
        }
        this.actionBarSuppressedUntilMillis.remove(uuid, untilMillis);
        return false;
    }

    void delayActionBarResume(UUID uuid, long nowMillis, int delayTicks) {
        if (uuid == null) {
            return;
        }
        if (delayTicks <= 0) {
            this.actionBarSuppressedUntilMillis.remove(uuid);
            return;
        }
        this.actionBarSuppressedUntilMillis.put(uuid, nowMillis + (long)delayTicks * 50L);
    }

    void removeActionBarSuppression(UUID uuid) {
        if (uuid != null) {
            this.actionBarSuppressedUntilMillis.remove(uuid);
        }
    }

    String currentSound() {
        return this.lobbyMusicState.currentSound;
    }

    private boolean canUseGroup(String group) {
        if ("playful".equals(group)) {
            int checked = 0;
            Iterator<String> iterator = this.lobbyMusicState.recentGroups.descendingIterator();
            while (iterator.hasNext() && checked < 2) {
                if ("playful".equals(iterator.next())) {
                    return false;
                }
                ++checked;
            }
            return true;
        }
        if ("special".equals(group)) {
            int checked = 0;
            Iterator<String> iterator = this.lobbyMusicState.recentGroups.descendingIterator();
            while (iterator.hasNext() && checked < 8) {
                if ("special".equals(iterator.next())) {
                    return false;
                }
                ++checked;
            }
        }
        return true;
    }

    static final class Group {
        private final String key;
        private final int weight;
        private final List<Track> tracks;

        Group(String key, int weight, List<Track> tracks) {
            this.key = key;
            this.weight = Math.max(0, weight);
            this.tracks = tracks == null ? List.of() : List.copyOf(tracks);
        }

        String key() {
            return this.key;
        }

        int weight() {
            return this.weight;
        }

        List<Track> tracks() {
            return this.tracks;
        }
    }

    static final class Track {
        private final String key;
        private final String sound;
        private final String group;
        private final int seconds;

        Track(String key, String sound, String group, int seconds) {
            this.key = key;
            this.sound = sound;
            this.group = group;
            this.seconds = seconds;
        }

        String key() {
            return this.key;
        }

        String sound() {
            return this.sound;
        }

        String group() {
            return this.group;
        }

        int seconds() {
            return this.seconds;
        }
    }

    private static final class LobbyMusicState {
        private final Deque<String> recentGroups = new ArrayDeque<String>();
        private Track currentTrack;
        private String currentSound;
        private String lastTrack;
        private long currentTrackUntilMillis;
        private long lastActionBarMillis;
        private long nextTrackMillis;

        private LobbyMusicState() {
        }

        private void reset() {
            this.recentGroups.clear();
            this.currentTrack = null;
            this.currentSound = null;
            this.lastTrack = null;
            this.currentTrackUntilMillis = 0L;
            this.lastActionBarMillis = 0L;
            this.nextTrackMillis = 0L;
        }
    }
}
