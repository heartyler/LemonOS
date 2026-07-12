/*
 * Backend-side LemonOS place runtime status persistence.
 */
package dev.lemonos;

import java.util.Locale;
import org.bukkit.configuration.file.FileConfiguration;

final class BackendPlaceStatusService {
    boolean ensureStatus(FileConfiguration places, String place, String defaultStatus) {
        String path = this.statusPath(place);
        if (places.isString(path)) {
            return false;
        }
        places.set(path, (Object)defaultStatus);
        return true;
    }

    void setStatus(FileConfiguration places, String place, String status) {
        places.set(this.statusPath(place), (Object)status);
    }

    String rawStatus(FileConfiguration places, String place, String defaultStatus) {
        if (places == null) {
            return defaultStatus;
        }
        return places.getString(this.statusPath(place), defaultStatus);
    }

    String normalizedStatus(FileConfiguration places, String place) {
        String status = this.rawStatus(places, place, "ready");
        return status == null ? "ready" : status.trim().toLowerCase(Locale.ROOT);
    }

    boolean closed(String status) {
        return "not_ready".equals(status) || "not ready".equals(status) || "not ready yet.".equals(status) || "unavailable".equals(status) || "unavailable.".equals(status);
    }

    boolean wakeable(String status) {
        return "sleep".equals(status) || "sleep.".equals(status) || "waking".equals(status) || "waking.".equals(status) || "waking up".equals(status) || "waking up.".equals(status) || this.resting(status);
    }

    boolean resting(String status) {
        return "resting".equals(status) || "resting.".equals(status);
    }

    private String statusPath(String place) {
        return "places." + place + ".status";
    }
}
