package dev.lemonos;

final class BackendAdminChunksSizeClickService {
    AdminChunksSizeClick action(int clickedSlot, int backSlot, BackendAdminWorldNavigationService<?> worldNavigationService) {
        if (clickedSlot == backSlot) {
            return new AdminChunksSizeClick(AdminChunksSizeAction.BACK, null);
        }
        Integer size = worldNavigationService.chunkSizeForSlot(clickedSlot);
        if (size != null) {
            return new AdminChunksSizeClick(AdminChunksSizeAction.SELECT, size);
        }
        return new AdminChunksSizeClick(AdminChunksSizeAction.NONE, null);
    }

    record AdminChunksSizeClick(AdminChunksSizeAction action, Integer size) {
    }

    enum AdminChunksSizeAction {
        NONE,
        BACK,
        SELECT;
    }
}
