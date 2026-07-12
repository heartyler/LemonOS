/*
 * Decompiled-compatible LemonOS place wake orchestration.
 */
package dev.lemonos.proxy;

import dev.lemonos.common.AdminProtocol;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;

final class PlaceWakeService {
    private final ProxyServer server;
    private final Logger logger;
    private final PlaceRuntimeProbe runtimeProbe;
    private final PlaceStatusRepository statusRepository;
    private final Scheduler scheduler;
    private final PlaceConnector connector;
    private final PlaceResultSender resultSender;
    private final Set<String> wakingServers = new HashSet<String>();

    PlaceWakeService(ProxyServer server, Logger logger, PlaceRuntimeProbe runtimeProbe, PlaceStatusRepository statusRepository, Scheduler scheduler, PlaceConnector connector, PlaceResultSender resultSender) {
        this.server = server;
        this.logger = logger;
        this.runtimeProbe = runtimeProbe;
        this.statusRepository = statusRepository;
        this.scheduler = scheduler;
        this.connector = connector;
        this.resultSender = resultSender;
    }

    void wakeOnly(String place, int port) {
        boolean shouldWake;
        synchronized (this.wakingServers) {
            shouldWake = this.wakingServers.add(place);
        }
        if (!shouldWake) {
            return;
        }
        this.statusRepository.setStatus(place, "waking up.");
        if (!this.runtimeProbe.startServer(place)) {
            synchronized (this.wakingServers) {
                this.wakingServers.remove(place);
            }
            this.statusRepository.setStatus(place, "unavailable");
            return;
        }
        CompletableFuture.runAsync(() -> this.waitForWakeStatus(place, port));
    }

    void wakeAndConnect(ServerConnection serverConnection, Player player, RegisteredServer registeredServer, UUID uuid, String place, int port) {
        boolean shouldWake;
        synchronized (this.wakingServers) {
            shouldWake = this.wakingServers.add(place);
        }
        if (shouldWake) {
            this.statusRepository.setStatus(place, "waking up.");
            if (!this.runtimeProbe.startServer(place)) {
                synchronized (this.wakingServers) {
                    this.wakingServers.remove(place);
                }
                try {
                    this.resultSender.send(serverConnection, uuid, place, AdminProtocol.PLACE_UNAVAILABLE);
                }
                catch (IOException exception) {
                    this.logger.debug("Unable to send LemonOS wake failure.", (Throwable)exception);
                }
                return;
            }
        }
        CompletableFuture.runAsync(() -> this.waitForWakeAndConnect(serverConnection, player, registeredServer, uuid, place, port));
    }

    private void waitForWakeAndConnect(ServerConnection serverConnection, Player player, RegisteredServer registeredServer, UUID uuid, String place, int port) {
        long deadline = System.currentTimeMillis() + 120000L;
        try {
            while (System.currentTimeMillis() <= deadline) {
                if (!this.server.getPlayer(uuid).isPresent()) {
                    return;
                }
                if (this.runtimeProbe.canConnect(port)) {
                    this.scheduler.schedule(() -> {
                        this.statusRepository.setStatus(place, "ready");
                        if (this.server.getPlayer(uuid).isPresent()) {
                            this.connector.connect(serverConnection, player, registeredServer, uuid, place);
                        }
                    });
                    return;
                }
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            this.scheduler.schedule(() -> {
                this.statusRepository.setStatus(place, "unavailable");
                try {
                    this.resultSender.send(serverConnection, uuid, place, AdminProtocol.PLACE_UNAVAILABLE);
                }
                catch (IOException exception) {
                    this.logger.debug("Unable to send LemonOS wake result.", (Throwable)exception);
                }
            });
        }
        finally {
            synchronized (this.wakingServers) {
                this.wakingServers.remove(place);
            }
        }
    }

    private void waitForWakeStatus(String place, int port) {
        long deadline = System.currentTimeMillis() + 120000L;
        try {
            while (System.currentTimeMillis() <= deadline) {
                if (this.runtimeProbe.canConnect(port)) {
                    this.statusRepository.setStatus(place, "ready");
                    return;
                }
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            this.statusRepository.setStatus(place, "unavailable");
        }
        finally {
            synchronized (this.wakingServers) {
                this.wakingServers.remove(place);
            }
        }
    }

    interface Scheduler {
        void schedule(Runnable runnable);
    }

    interface PlaceConnector {
        void connect(ServerConnection serverConnection, Player player, RegisteredServer registeredServer, UUID uuid, String place);
    }

    interface PlaceResultSender {
        void send(ServerConnection serverConnection, UUID uuid, String place, String result) throws IOException;
    }
}
