/*
 * Decompiled-compatible LemonOS proxy command intercepts.
 */
package dev.lemonos.proxy;

import dev.lemonos.common.AdminProtocol;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.slf4j.Logger;

final class ProxyCommandInterceptService {
    private final Logger logger;
    private final ChannelIdentifier adminChannel;

    ProxyCommandInterceptService(Logger logger, ChannelIdentifier adminChannel) {
        this.logger = logger;
        this.adminChannel = adminChannel;
    }

    void handle(CommandExecuteEvent commandExecuteEvent) {
        String command = commandExecuteEvent.getCommand() == null ? "" : commandExecuteEvent.getCommand().trim();
        String commandWithoutSlash = command.startsWith("/") ? command.substring(1) : command;
        String[] parts = commandWithoutSlash.split("\\s+", 2);
        String commandName = parts[0];
        CommandSource commandSource = commandExecuteEvent.getCommandSource();
        if (!(commandSource instanceof Player)) {
            return;
        }
        Player player = (Player)commandSource;
        if ("cubee".equalsIgnoreCase(commandName)) {
            player.getCurrentServer().ifPresent(serverConnection -> this.sendOpenCubee((ServerConnection)serverConnection, player, false));
            commandExecuteEvent.setResult(CommandExecuteEvent.CommandResult.denied());
            return;
        }
        if (!"help".equalsIgnoreCase(commandName)) {
            return;
        }
        player.getCurrentServer().ifPresent(serverConnection -> this.sendOpenCubee((ServerConnection)serverConnection, player, true));
        commandExecuteEvent.setResult(CommandExecuteEvent.CommandResult.denied());
    }

    private void sendOpenCubee(ServerConnection serverConnection, Player player, boolean recovery) {
        this.sendOpenLemonOSCommand(serverConnection, player, AdminProtocol.OPEN_CUBEE, recovery);
    }

    private void sendOpenLemonOSCommand(ServerConnection serverConnection, Player player, String command, boolean recovery) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
                dataOutputStream.writeUTF(command);
                dataOutputStream.writeUTF(player.getUniqueId().toString());
                dataOutputStream.writeBoolean(recovery);
            }
            serverConnection.getServer().sendPluginMessage((ChannelIdentifier)this.adminChannel, byteArrayOutputStream.toByteArray());
        }
        catch (IOException exception) {
            this.logger.debug("Unable to send LemonOS open request.", (Throwable)exception);
        }
    }
}
