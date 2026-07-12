package dev.lemonos;

final class BackendPlacesClickService {
    <T> PlacesClick<T> action(int clickedSlot, int backSlot, T currentServer, T lobby, T survival, T creative) {
        if (clickedSlot == backSlot) {
            return new PlacesClick<T>(PlacesClickAction.BACK, null);
        }
        T target = this.targetForSlot(clickedSlot, lobby, survival, creative);
        if (target == null) {
            return new PlacesClick<T>(PlacesClickAction.NONE, null);
        }
        if (target == currentServer || target.equals(currentServer)) {
            return new PlacesClick<T>(PlacesClickAction.RETURN_SPAWN, target);
        }
        return new PlacesClick<T>(PlacesClickAction.TRAVEL, target);
    }

    private <T> T targetForSlot(int clickedSlot, T lobby, T survival, T creative) {
        return clickedSlot == 12 ? lobby : (clickedSlot == 13 ? survival : (clickedSlot == 14 ? creative : null));
    }

    record PlacesClick<T>(PlacesClickAction action, T target) {
    }

    enum PlacesClickAction {
        NONE,
        BACK,
        RETURN_SPAWN,
        TRAVEL;
    }
}
