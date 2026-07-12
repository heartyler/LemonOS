/*
 * Backend-side LemonOS place runtime status orchestration.
 */
package dev.lemonos;

import org.bukkit.configuration.file.FileConfiguration;

final class BackendPlaceRuntimeStatusService {
    private final BackendPlaceStatusService placeStatusService;

    BackendPlaceRuntimeStatusService(BackendPlaceStatusService placeStatusService) {
        this.placeStatusService = placeStatusService;
    }

    boolean closed(FileConfiguration places, String place) {
        return this.placeStatusService.closed(this.normalizedStatus(places, place));
    }

    boolean wakeable(FileConfiguration places, String place) {
        return this.placeStatusService.wakeable(this.normalizedStatus(places, place));
    }

    boolean resting(FileConfiguration places, String place) {
        return this.placeStatusService.resting(this.normalizedStatus(places, place));
    }

    void setStatus(FileConfiguration places, String place, String status) {
        this.placeStatusService.setStatus(places, place, status);
    }

    String normalizedStatus(FileConfiguration places, String place) {
        return this.placeStatusService.normalizedStatus(places, place);
    }

    void capturePreRestStatus(FileConfiguration places, String place, BackendRestStateService restStateService, String restingStatus, String wakingStatus) {
        String status = this.placeStatusService.rawStatus(places, place, "ready");
        restStateService.capturePreRestPlaceStatus(status, restingStatus, wakingStatus);
    }

    String restoreRestStatus(FileConfiguration places, String place, BackendRestStateService restStateService, String restingStatus, String wakingStatus) {
        String status = this.placeStatusService.rawStatus(places, place, "ready");
        return restStateService.restorePlaceStatus(status, restingStatus, wakingStatus);
    }
}
