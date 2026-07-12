package dev.lemonos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class BackendCubeeNavigationService {
    private final Map<UUID, CubeeRoot> roots = new ConcurrentHashMap<UUID, CubeeRoot>();
    private final Map<UUID, CubeeSurface> surfaces = new ConcurrentHashMap<UUID, CubeeSurface>();

    CubeeRoot root(UUID playerId) {
        return this.roots.getOrDefault(playerId, CubeeRoot.CUBEE);
    }

    CubeeSurface surface(UUID playerId) {
        return this.surfaces.getOrDefault(playerId, CubeeSurface.HOME);
    }

    void switchRoot(UUID playerId, CubeeRoot root) {
        this.roots.put(playerId, root);
    }

    void switchSurface(UUID playerId, CubeeSurface surface) {
        this.surfaces.put(playerId, surface);
    }

    void reset(UUID playerId) {
        this.roots.put(playerId, CubeeRoot.CUBEE);
        this.surfaces.put(playerId, CubeeSurface.HOME);
    }

    void remove(UUID playerId) {
        this.roots.remove(playerId);
        this.surfaces.remove(playerId);
    }

    void clear() {
        this.roots.clear();
        this.surfaces.clear();
    }
}

enum CubeeSurface {
    HOME,
    PLACES,
    SANDBOX,
    PEOPLE,
    ADMIN_PEOPLE;
}

enum CubeeRoot {
    CUBEE,
    CARE;
}
