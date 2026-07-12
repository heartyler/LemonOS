package dev.lemonos;

final class BackendAdminChunksDimensionClickService<D> {
    AdminChunksDimensionClick<D> action(int clickedSlot, int backSlot, BackendAdminWorldNavigationService<D> worldNavigationService, D world, D nether, D theEnd) {
        if (clickedSlot == backSlot) {
            return new AdminChunksDimensionClick<D>(AdminChunksDimensionAction.BACK, null);
        }
        D dimension = worldNavigationService.dimensionForSlot(clickedSlot, world, nether, theEnd);
        if (dimension != null) {
            return new AdminChunksDimensionClick<D>(AdminChunksDimensionAction.SELECT, dimension);
        }
        return new AdminChunksDimensionClick<D>(AdminChunksDimensionAction.NONE, null);
    }

    record AdminChunksDimensionClick<D>(AdminChunksDimensionAction action, D dimension) {
    }

    enum AdminChunksDimensionAction {
        NONE,
        BACK,
        SELECT;
    }
}
