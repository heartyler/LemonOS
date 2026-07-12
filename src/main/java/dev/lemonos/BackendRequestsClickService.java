package dev.lemonos;

final class BackendRequestsClickService {
    RequestClickAction action(int clickedSlot, int declineSlot, int ignoreSlot, int acceptSlot) {
        if (clickedSlot == ignoreSlot) {
            return RequestClickAction.IGNORE;
        }
        if (clickedSlot == acceptSlot) {
            return RequestClickAction.ACCEPT;
        }
        if (clickedSlot == declineSlot) {
            return RequestClickAction.DECLINE;
        }
        return RequestClickAction.NONE;
    }

    enum RequestClickAction {
        NONE,
        IGNORE,
        ACCEPT,
        DECLINE;
    }
}
