/*
 * Backend-side LemonOS rest lifecycle state.
 */
package dev.lemonos;

final class BackendRestStateService {
    private RestState restState = RestState.ACTIVE;
    private long emptySinceMillis;
    private long restSinceMillis;
    private String preRestPlaceStatus;

    void reset() {
        this.restState = RestState.ACTIVE;
        this.emptySinceMillis = 0L;
        this.restSinceMillis = 0L;
        this.preRestPlaceStatus = null;
    }

    RestAction tick(long nowMillis, boolean onlineEmpty, int idleMinutes, boolean autoStop, int sleepMinutes) {
        if (!onlineEmpty) {
            return this.wakeFromRestIfNeeded();
        }
        if (this.restState == RestState.SLEEPING) {
            return RestAction.NONE;
        }
        if (this.emptySinceMillis <= 0L) {
            this.emptySinceMillis = nowMillis;
            return RestAction.NONE;
        }
        if (this.restState == RestState.RESTING) {
            return this.sleepReady(nowMillis, autoStop, sleepMinutes, onlineEmpty) ? RestAction.SLEEP : RestAction.NONE;
        }
        long idleMillis = nowMillis - this.emptySinceMillis;
        return idleMillis >= (long)idleMinutes * 60000L ? RestAction.ENTER_REST : RestAction.NONE;
    }

    void markEmptyIfNeeded(long nowMillis, boolean onlineEmpty) {
        if (onlineEmpty && this.emptySinceMillis <= 0L) {
            this.emptySinceMillis = nowMillis;
        }
    }

    RestAction wakeFromRestIfNeeded() {
        this.emptySinceMillis = 0L;
        if (this.restState != RestState.RESTING) {
            return RestAction.NONE;
        }
        this.restState = RestState.WAKING_UP;
        this.restSinceMillis = 0L;
        return RestAction.WAKE;
    }

    boolean isWakingUp() {
        return this.restState == RestState.WAKING_UP;
    }

    boolean isSleeping() {
        return this.restState == RestState.SLEEPING;
    }

    void finishWake() {
        this.restState = RestState.ACTIVE;
        this.restSinceMillis = 0L;
    }

    boolean canEnterRest() {
        return this.restState != RestState.RESTING;
    }

    void enterRest(long nowMillis) {
        this.restState = RestState.RESTING;
        this.restSinceMillis = nowMillis;
    }

    boolean sleepReady(long nowMillis, boolean autoStop, int sleepMinutes, boolean onlineEmpty) {
        if (!autoStop || this.restState != RestState.RESTING || !onlineEmpty) {
            return false;
        }
        long startedAt = this.restSinceMillis > 0L ? this.restSinceMillis : nowMillis;
        return nowMillis - startedAt >= (long)sleepMinutes * 60000L;
    }

    void markSleeping() {
        this.restState = RestState.SLEEPING;
    }

    void capturePreRestPlaceStatus(String status, String restingStatus, String wakingStatus) {
        String safeStatus = status == null ? "" : status;
        if (safeStatus.isBlank() || safeStatus.equals(restingStatus) || safeStatus.equals(wakingStatus)) {
            safeStatus = "ready";
        }
        this.preRestPlaceStatus = safeStatus;
    }

    String restorePlaceStatus(String currentStatus, String restingStatus, String wakingStatus) {
        String safeCurrentStatus = String.valueOf(currentStatus);
        if (!safeCurrentStatus.equals(restingStatus) && !safeCurrentStatus.equals(wakingStatus)) {
            this.preRestPlaceStatus = null;
            return null;
        }
        String restored = this.preRestPlaceStatus == null || this.preRestPlaceStatus.isBlank() ? "ready" : this.preRestPlaceStatus;
        this.preRestPlaceStatus = null;
        this.restSinceMillis = 0L;
        return restored;
    }

    boolean isResting() {
        return this.restState == RestState.RESTING;
    }

    long restSinceMillis(long nowMillis) {
        return this.restSinceMillis > 0L ? this.restSinceMillis : nowMillis;
    }

    enum RestAction {
        NONE,
        ENTER_REST,
        WAKE,
        SLEEP;

    }

    private enum RestState {
        ACTIVE,
        RESTING,
        WAKING_UP,
        SLEEPING;

    }
}
