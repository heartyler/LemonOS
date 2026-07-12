package dev.lemonos;

final class BackendAdminChunkActionService {
    boolean canOpenChunks(boolean available) {
        return available;
    }

    boolean canEditChunks(boolean running) {
        return !running;
    }

    boolean canStartChunks(boolean running) {
        return !running;
    }

    boolean canCancelChunks(boolean running) {
        return running;
    }

    boolean hasChunkSignal(boolean available, boolean chunkyReady) {
        return available && chunkyReady;
    }

    String chunksButtonLore(boolean running) {
        return running ? "running." : "prepare the world.";
    }

    String dimensionLore(boolean running) {
        return running ? "running." : "choose where.";
    }

    String sizeLore(boolean running) {
        return running ? "running." : "choose how far.";
    }

    String centerLore(boolean running) {
        return running ? "running." : "start from here.";
    }

    boolean isActiveStatus(String status) {
        return "waiting.".equals(status) || "running.".equals(status) || status.endsWith("% ready");
    }
}
