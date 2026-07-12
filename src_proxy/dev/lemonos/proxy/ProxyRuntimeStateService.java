/*
 * Decompiled-compatible LemonOS proxy runtime state jobs.
 */
package dev.lemonos.proxy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import org.slf4j.Logger;

final class ProxyRuntimeStateService {
    private final ProxyServer server;
    private final Logger logger;
    private final Path onlineFile;
    private final Path playtimeFile;
    private final Path placesFile;
    private final OnlinePlayersRepository onlinePlayersRepository;
    private final PlaytimeRepository playtimeRepository;
    private final PlaceStatusRepository placeStatusRepository;
    private final PlaceRuntimeProbe placeRuntimeProbe;
    private final BooleanSupplier stayedCloseCollectionEnabled;

    ProxyRuntimeStateService(ProxyServer server, Logger logger, Path onlineFile, Path playtimeFile, Path placesFile, OnlinePlayersRepository onlinePlayersRepository, PlaytimeRepository playtimeRepository, PlaceStatusRepository placeStatusRepository, PlaceRuntimeProbe placeRuntimeProbe, BooleanSupplier stayedCloseCollectionEnabled) {
        this.server = server;
        this.logger = logger;
        this.onlineFile = onlineFile;
        this.playtimeFile = playtimeFile;
        this.placesFile = placesFile;
        this.onlinePlayersRepository = onlinePlayersRepository;
        this.playtimeRepository = playtimeRepository;
        this.placeStatusRepository = placeStatusRepository;
        this.placeRuntimeProbe = placeRuntimeProbe;
        this.stayedCloseCollectionEnabled = stayedCloseCollectionEnabled;
    }

    void writeOnlinePlayers() {
        if (this.onlineFile == null) {
            return;
        }
        List<OnlinePlayersRepository.PlayerSnapshot> list = new ArrayList<OnlinePlayersRepository.PlayerSnapshot>();
        for (Player player : this.server.getAllPlayers()) {
            list.add(new OnlinePlayersRepository.PlayerSnapshot(player.getUsername()));
        }
        this.onlinePlayersRepository.write(list);
    }

    synchronized void loadPlaytime() {
        this.playtimeRepository.load();
    }

    synchronized void syncPlaytime() {
        if (this.playtimeFile == null || !this.stayedCloseCollectionEnabled.getAsBoolean()) {
            this.clearPlaytimeState();
            return;
        }
        List<PlaytimeRepository.PlayerSnapshot> list = new ArrayList<PlaytimeRepository.PlayerSnapshot>();
        for (Player player : this.server.getAllPlayers()) {
            list.add(new PlaytimeRepository.PlayerSnapshot(player.getUniqueId(), player.getUsername()));
        }
        this.playtimeRepository.sync(list);
        this.savePlaytime();
    }

    synchronized void savePlaytime() {
        if (this.playtimeFile == null || !this.stayedCloseCollectionEnabled.getAsBoolean()) {
            return;
        }
        try {
            this.playtimeRepository.save();
        }
        catch (IOException exception) {
            this.logger.debug("Unable to write LemonOS playtime state.", (Throwable)exception);
        }
    }

    synchronized void clearPlaytimeState() {
        if (this.playtimeFile == null) {
            return;
        }
        try {
            this.playtimeRepository.clear();
        }
        catch (IOException exception) {
            this.logger.debug("Unable to clear disabled Stayed Close state.", (Throwable)exception);
        }
    }

    void reconcilePlaceStatuses() {
        for (String place : List.of("lobby", "survival", "creative")) {
            int port = this.placeRuntimeProbe.port(place);
            if (port <= 0) {
                continue;
            }
            String status = this.placeStatusRepository.status(place);
            boolean canConnect = this.placeRuntimeProbe.canConnect(port);
            if (canConnect) {
                if ("unavailable".equals(status) || "unavailable.".equals(status) || PlaceStatusRepository.isWakeStatus(status) || status.isBlank()) {
                    this.setPlaceStatus(place, "ready");
                }
                continue;
            }
            if (PlaceStatusRepository.isWakeStatus(status)) {
                continue;
            }
            if (PlaceStatusRepository.isReadyStatus(status)) {
                this.setPlaceStatus(place, "unavailable");
            }
        }
    }

    private void setPlaceStatus(String place, String status) {
        if (this.placesFile == null) {
            return;
        }
        this.placeStatusRepository.setStatus(place, status);
    }
}
