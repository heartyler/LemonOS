/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.velocitypowered.api.event.Subscribe
 *  com.velocitypowered.api.event.command.CommandExecuteEvent
 *  com.velocitypowered.api.event.command.CommandExecuteEvent$CommandResult
 *  com.velocitypowered.api.event.connection.PluginMessageEvent
 *  com.velocitypowered.api.event.connection.PluginMessageEvent$ForwardResult
 *  com.velocitypowered.api.event.proxy.ProxyInitializeEvent
 *  com.velocitypowered.api.plugin.Plugin
 *  com.velocitypowered.api.proxy.Player
 *  com.velocitypowered.api.proxy.ProxyServer
 *  com.velocitypowered.api.proxy.ServerConnection
 *  com.velocitypowered.api.proxy.messages.ChannelIdentifier
 *  com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
 *  com.velocitypowered.api.proxy.server.RegisteredServer
 *  net.kyori.adventure.text.Component
 *  org.slf4j.Logger
 */
package dev.lemonos.proxy;

import dev.lemonos.proxy.runtime.ProxyRuntimeLayout;

import dev.lemonos.common.AdminProtocol;
import dev.lemonos.common.LemonOS;
import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

@Plugin(id=LemonOS.PROXY_PLUGIN_ID, name=LemonOS.PROXY_PLUGIN_NAME, version=LemonOS.RELEASE_VERSION, authors={"LemonOS"})
public final class LemonOSProxyPlugin {
    private final ProxyServer server;
    private final Logger logger;
    private final MinecraftChannelIdentifier adminChannel = MinecraftChannelIdentifier.from((String)LemonOS.ADMIN_CHANNEL);
    private Path sharedDataFolder;
    private Path accessFile;
    private Path onlineFile;
    private Path placesFile;
    private Path playtimeFile;
    private Path hudConfigFile;
    private AccessRepository accessRepository;
    private PlaytimeRepository playtimeRepository;
    private OnlinePlayersRepository onlinePlayersRepository;
    private PlaceStatusRepository placeStatusRepository;
    private PlaceRuntimeProbe placeRuntimeProbe;
    private PlaceConnectService placeConnectService;
    private PlaceWakeService placeWakeService;
    private PeopleService peopleService;
    private SkinService skinService;
    private ProxyCommandInterceptService commandInterceptService;
    private ProxyAccessCommandService accessCommandService;
    private AccessMessageService accessMessageService;
    private ProxyLifecycleService lifecycleService;
    private ProxyRuntimeStateService runtimeStateService;

    @Inject
    public LemonOSProxyPlugin(ProxyServer proxyServer, Logger logger) {
        this.server = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent proxyInitializeEvent) {
        this.lifecycleService = new ProxyLifecycleService(this.logger);
        ProxyRuntimeLayout runtimePaths = ProxyRuntimeLayout.resolve();
        this.sharedDataFolder = runtimePaths.sharedDataFolder;
        this.accessFile = runtimePaths.accessFile;
        this.onlineFile = runtimePaths.onlineFile;
        this.placesFile = runtimePaths.placesFile;
        this.playtimeFile = runtimePaths.playtimeFile;
        this.hudConfigFile = runtimePaths.hudConfigFile;
        this.accessRepository = new AccessRepository(this.accessFile, this.logger);
        this.playtimeRepository = new PlaytimeRepository(this.playtimeFile, this.logger);
        this.onlinePlayersRepository = new OnlinePlayersRepository(this.onlineFile, this.logger);
        this.placeStatusRepository = new PlaceStatusRepository(this.placesFile, this.logger);
        this.placeRuntimeProbe = new PlaceRuntimeProbe(this.sharedDataFolder.getParent(), this.logger);
        this.placeConnectService = new PlaceConnectService(this.server, this.logger, this.adminChannel, this.placeRuntimeProbe, this.placeStatusRepository, this::scheduleProxyTask, (serverConnection, player, registeredServer, uuid, place, port) -> this.placeWakeService.wakeAndConnect(serverConnection, player, registeredServer, uuid, place, port));
        this.placeWakeService = new PlaceWakeService(this.server, this.logger, this.placeRuntimeProbe, this.placeStatusRepository, this::scheduleProxyTask, this.placeConnectService::connectPlayerToPlace, this.placeConnectService::sendPlaceResult);
        this.peopleService = new PeopleService(this.server, this.logger, this.adminChannel, this::scheduleProxyTask);
        this.skinService = new SkinService(this.server, this.logger, this.adminChannel, this::scheduleProxyTask);
        this.commandInterceptService = new ProxyCommandInterceptService(this.logger, this.adminChannel);
        this.accessCommandService = new ProxyAccessCommandService(this.server, this.logger, this.accessRepository);
        this.accessMessageService = new AccessMessageService(this.server, this.logger, this.adminChannel, this.accessRepository);
        this.runtimeStateService = new ProxyRuntimeStateService(this.server, this.logger, this.onlineFile, this.playtimeFile, this.placesFile, this.onlinePlayersRepository, this.playtimeRepository, this.placeStatusRepository, this.placeRuntimeProbe, this::stayedCloseCollectionEnabled);
        this.lifecycleService.ensureSharedDataFiles(runtimePaths);
        this.loadAccess();
        if (this.stayedCloseCollectionEnabled()) {
            this.runtimeStateService.loadPlaytime();
        } else {
            this.runtimeStateService.clearPlaytimeState();
        }
        this.runtimeStateService.writeOnlinePlayers();
        this.runtimeStateService.syncPlaytime();
        this.runtimeStateService.reconcilePlaceStatuses();
        this.server.getChannelRegistrar().register(new ChannelIdentifier[]{this.adminChannel});
        BrigadierCommand accessCommand = this.accessCommandService.lemonosAccessCommand();
        var accessCommandMeta = this.server.getCommandManager().metaBuilder(accessCommand).plugin(this).build();
        this.server.getCommandManager().register(accessCommandMeta, accessCommand);
        this.server.getScheduler().buildTask((Object)this, this.runtimeStateService::writeOnlinePlayers).repeat(5L, TimeUnit.SECONDS).schedule();
        this.server.getScheduler().buildTask((Object)this, this.runtimeStateService::syncPlaytime).repeat(60L, TimeUnit.SECONDS).schedule();
        this.server.getScheduler().buildTask((Object)this, this.runtimeStateService::reconcilePlaceStatuses).repeat(5L, TimeUnit.SECONDS).schedule();
        this.logger.info("LemonOS proxy {} started. Build {}.", (Object)LemonOS.RELEASE_VERSION, (Object)this.lifecycleService.buildSourceSnapshot(LemonOSProxyPlugin.class));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent proxyShutdownEvent) {
        this.runtimeStateService.syncPlaytime();
        this.runtimeStateService.savePlaytime();
    }

    @Subscribe
    public void onCommandExecute(CommandExecuteEvent commandExecuteEvent) {
        this.commandInterceptService.handle(commandExecuteEvent);
    }

    @Subscribe
    public void onKickedFromServer(KickedFromServerEvent kickedFromServerEvent) {
        String string = kickedFromServerEvent.getServer().getServerInfo().getName().toLowerCase(Locale.ROOT);
        if (!Set.of("lobby", "survival", "creative").contains(string) || kickedFromServerEvent.kickedDuringServerConnect()) {
            return;
        }
        Component component = Component.text((String)"not available right now.", (NamedTextColor)NamedTextColor.DARK_GRAY);
        Optional optional = this.server.getServer("lobby");
        if (optional.isEmpty() || "lobby".equals(string)) {
            kickedFromServerEvent.setResult((KickedFromServerEvent.ServerKickResult)KickedFromServerEvent.Notify.create((Component)component));
            return;
        }
        kickedFromServerEvent.setResult((KickedFromServerEvent.ServerKickResult)KickedFromServerEvent.RedirectPlayer.create((RegisteredServer)optional.get(), (Component)component));
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent pluginMessageEvent) {
        if (!pluginMessageEvent.getIdentifier().equals((Object)this.adminChannel)) {
            return;
        }
        pluginMessageEvent.setResult(PluginMessageEvent.ForwardResult.handled());
        Object object = pluginMessageEvent.getSource();
        if (!(object instanceof ServerConnection)) {
            return;
        }
        ServerConnection serverConnection = (ServerConnection)object;
        if (this.accessMessageService.handleLegacyAccessWrite(serverConnection, pluginMessageEvent.getData())) {
            return;
        }
        try {
            object = new DataInputStream(new ByteArrayInputStream(pluginMessageEvent.getData()));
            try {
                String string = ((DataInputStream)object).readUTF();
                if (this.accessMessageService.handleAccessMessage(serverConnection, string, (DataInputStream)object)) {
                    return;
                }
                if (AdminProtocol.CONNECT_PLACE.equals(string)) {
                    UUID uUID = UUID.fromString(((DataInputStream)object).readUTF());
                    String string4 = ((DataInputStream)object).readUTF();
                    this.placeConnectService.connectPlace(serverConnection, uUID, string4);
                } else if (AdminProtocol.WAKE_PLACE.equals(string)) {
                    UUID uUID = UUID.fromString(((DataInputStream)object).readUTF());
                    String string4 = ((DataInputStream)object).readUTF();
                    this.wakePlace(serverConnection, uUID, string4);
                } else if (AdminProtocol.REQUEST_PEOPLE.equals(string)) {
                    this.peopleService.sendPeopleState(serverConnection);
                } else if (AdminProtocol.MEET_PLAYER.equals(string)) {
                    UUID uUID = UUID.fromString(((DataInputStream)object).readUTF());
                    UUID uUID3 = UUID.fromString(((DataInputStream)object).readUTF());
                    this.peopleService.meetPlayer(serverConnection, uUID, uUID3);
                } else if (AdminProtocol.BRING_PLAYER.equals(string)) {
                    UUID uUID = UUID.fromString(((DataInputStream)object).readUTF());
                    UUID uUID4 = UUID.fromString(((DataInputStream)object).readUTF());
                    this.peopleService.bringPlayer(serverConnection, uUID, uUID4);
                } else if (AdminProtocol.SEND_PLAYER_PLACE.equals(string)) {
                    UUID actorId = UUID.fromString(((DataInputStream)object).readUTF());
                    UUID targetId = UUID.fromString(((DataInputStream)object).readUTF());
                    String place = ((DataInputStream)object).readUTF();
                    UUID operationId = UUID.fromString(((DataInputStream)object).readUTF());
                    this.peopleService.sendPlayerPlace(serverConnection, actorId, targetId, place, operationId);
                } else if (AdminProtocol.SKIN_APPLY.equals(string)) {
                    UUID uUID = UUID.fromString(((DataInputStream)object).readUTF());
                    String string5 = ((DataInputStream)object).readUTF();
                    this.skinService.applySkin(serverConnection, uUID, string5);
                }
            }
            finally {
                ((FilterInputStream)object).close();
            }
        }
        catch (IOException | IllegalArgumentException exception) {
            this.logger.debug("Ignoring malformed LemonOS admin message.", (Throwable)exception);
        }
    }

    private void loadAccess() {
        this.accessRepository.load();
    }

    private boolean playerOnSourceServer(ServerConnection serverConnection, UUID uUID) {
        return this.server.getPlayer(uUID).flatMap(Player::getCurrentServer).map(currentServer -> currentServer.getServer().getServerInfo().getName().equalsIgnoreCase(serverConnection.getServerInfo().getName())).orElse(false);
    }

    private boolean stayedCloseCollectionEnabled() {
        return this.lifecycleService != null && this.lifecycleService.stayedCloseCollectionEnabled(this.hudConfigFile);
    }

    private void wakePlace(ServerConnection serverConnection, UUID uUID, String string) {
        String string2 = this.normalizePlaceName(string);
        int n = this.placePort(string2);
        if (!this.playerOnSourceServer(serverConnection, uUID) || !this.canProxyWake(string2) || n <= 0 || this.canConnect(n)) {
            return;
        }
        String string3 = this.placeStatus(string2);
        if (!this.isWakeStatus(string3) && !this.isReadyStatus(string3)) {
            return;
        }
        this.placeWakeService.wakeOnly(string2, n);
    }

    private String normalizePlaceName(String string) {
        return string == null ? "" : string.trim().toLowerCase(Locale.ROOT);
    }

    private boolean canProxyWake(String string) {
        return "survival".equals(string) || "creative".equals(string);
    }

    private int placePort(String string) {
        return this.placeRuntimeProbe.port(string);
    }

    private boolean canConnect(int n) {
        return this.placeRuntimeProbe.canConnect(n);
    }

    private boolean startPlaceServer(String string) {
        return this.placeRuntimeProbe.startServer(string);
    }

    private void scheduleProxyTask(Runnable runnable) {
        this.server.getScheduler().buildTask((Object)this, runnable).schedule();
    }

    private String placeStatus(String string) {
        return this.placeStatusRepository.status(string);
    }

    private boolean isReadyStatus(String string) {
        return PlaceStatusRepository.isReadyStatus(string);
    }

    private boolean isWakeStatus(String string) {
        return PlaceStatusRepository.isWakeStatus(string);
    }

}
