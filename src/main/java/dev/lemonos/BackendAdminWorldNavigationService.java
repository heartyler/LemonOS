package dev.lemonos;

final class BackendAdminWorldNavigationService<D> {
    D dimensionForSlot(int slot, D world, D nether, D theEnd) {
        return switch (slot) {
            case 12 -> world;
            case 13 -> nether;
            case 14 -> theEnd;
            default -> null;
        };
    }

    Integer chunkSizeForSlot(int slot) {
        return switch (slot) {
            case 12 -> 1500;
            case 13 -> 3000;
            case 14 -> 5000;
            default -> null;
        };
    }

    String timeForSlot(int slot) {
        return switch (slot) {
            case 11 -> "day";
            case 12 -> "night";
            default -> null;
        };
    }

    Boolean weatherForSlot(int slot) {
        return switch (slot) {
            case 13 -> Boolean.TRUE;
            case 14 -> Boolean.FALSE;
            default -> null;
        };
    }
}
