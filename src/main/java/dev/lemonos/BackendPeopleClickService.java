package dev.lemonos;

import java.util.Map;
import java.util.UUID;

final class BackendPeopleClickService {
    PeoplePageClick peoplePageAction(int clickedSlot, int backSlot, int nextSlot, Map<Integer, UUID> slotTargets) {
        if (clickedSlot == backSlot) {
            return new PeoplePageClick(PeoplePageAction.BACK, null);
        }
        if (clickedSlot == nextSlot) {
            return new PeoplePageClick(PeoplePageAction.NEXT_PAGE, null);
        }
        UUID target = slotTargets == null ? null : slotTargets.get(clickedSlot);
        if (target != null) {
            return new PeoplePageClick(PeoplePageAction.OPEN_PLAYER, target);
        }
        return new PeoplePageClick(PeoplePageAction.NONE, null);
    }

    PlayerPageAction playerPageAction(int clickedSlot, int backSlot, int messageSlot, int visitSlot, int inviteSlot) {
        if (clickedSlot == backSlot) {
            return PlayerPageAction.BACK;
        }
        if (clickedSlot == messageSlot) {
            return PlayerPageAction.MESSAGE;
        }
        if (clickedSlot == visitSlot) {
            return PlayerPageAction.VISIT;
        }
        if (clickedSlot == inviteSlot) {
            return PlayerPageAction.INVITE;
        }
        return PlayerPageAction.NONE;
    }

    record PeoplePageClick(PeoplePageAction action, UUID target) {
    }

    enum PeoplePageAction {
        NONE,
        BACK,
        NEXT_PAGE,
        OPEN_PLAYER;
    }

    enum PlayerPageAction {
        NONE,
        BACK,
        MESSAGE,
        VISIT,
        INVITE;
    }
}
