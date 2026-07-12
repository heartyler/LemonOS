/*
 * Decompiled-compatible LemonOS proxy access console command.
 */
package dev.lemonos.proxy;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

final class ProxyAccessCommandService {
    private final ProxyServer server;
    private final Logger logger;
    private final AccessRepository accessRepository;

    ProxyAccessCommandService(ProxyServer server, Logger logger, AccessRepository accessRepository) {
        this.server = server;
        this.logger = logger;
        this.accessRepository = accessRepository;
    }

    BrigadierCommand lemonosAccessCommand() {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)((LiteralArgumentBuilder)BrigadierCommand.literalArgumentBuilder((String)"lemonos").requires(commandSource -> !(commandSource instanceof Player))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)BrigadierCommand.literalArgumentBuilder((String)"access").executes(commandContext -> {
            this.sendLemonOSHelp((CommandSource)commandContext.getSource());
            return 1;
        })).then(BrigadierCommand.literalArgumentBuilder((String)"list").executes(commandContext -> {
            this.handleConsoleLemonOSCommand((CommandSource)commandContext.getSource(), "access list");
            return 1;
        }))).then(BrigadierCommand.literalArgumentBuilder((String)"add").then(BrigadierCommand.requiredArgumentBuilder((String)"name", (ArgumentType)StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> this.suggestOnlineNames(suggestionsBuilder)).executes(commandContext -> {
            this.handleConsoleLemonOSCommand((CommandSource)commandContext.getSource(), "access add " + StringArgumentType.getString((CommandContext)commandContext, (String)"name"));
            return 1;
        })))).then(BrigadierCommand.literalArgumentBuilder((String)"admin").then(BrigadierCommand.requiredArgumentBuilder((String)"name", (ArgumentType)StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> this.suggestOnlineNames(suggestionsBuilder)).executes(commandContext -> {
            this.handleConsoleLemonOSCommand((CommandSource)commandContext.getSource(), "access admin " + StringArgumentType.getString((CommandContext)commandContext, (String)"name"));
            return 1;
        })))).then(BrigadierCommand.literalArgumentBuilder((String)"remove").then(BrigadierCommand.requiredArgumentBuilder((String)"name", (ArgumentType)StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> this.suggestAdminNames(suggestionsBuilder)).executes(commandContext -> {
            this.handleConsoleLemonOSCommand((CommandSource)commandContext.getSource(), "access remove " + StringArgumentType.getString((CommandContext)commandContext, (String)"name"));
            return 1;
        })))).then(BrigadierCommand.literalArgumentBuilder((String)"default").then(BrigadierCommand.requiredArgumentBuilder((String)"name", (ArgumentType)StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> this.suggestAdminNames(suggestionsBuilder)).executes(commandContext -> {
            this.handleConsoleLemonOSCommand((CommandSource)commandContext.getSource(), "access default " + StringArgumentType.getString((CommandContext)commandContext, (String)"name"));
            return 1;
        }))));
        return new BrigadierCommand(literalArgumentBuilder);
    }

    private CompletableFuture<Suggestions> suggestOnlineNames(SuggestionsBuilder suggestionsBuilder) {
        String remaining = AccessRepository.normalizeAccessName(suggestionsBuilder.getRemaining());
        this.server.getAllPlayers().stream().map(player -> AccessRepository.normalizeAccessName(player.getUsername())).filter(name -> !name.isBlank()).filter(name -> name.startsWith(remaining)).sorted(String.CASE_INSENSITIVE_ORDER).forEach(suggestionsBuilder::suggest);
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestAdminNames(SuggestionsBuilder suggestionsBuilder) {
        String remaining = AccessRepository.normalizeAccessName(suggestionsBuilder.getRemaining());
        this.loadAccess();
        this.accessRepository.adminNameAccess().stream().filter(name -> name.startsWith(remaining)).forEach(suggestionsBuilder::suggest);
        return suggestionsBuilder.buildFuture();
    }

    private void handleConsoleLemonOSCommand(CommandSource commandSource, String command) {
        String[] parts = command == null || command.isBlank() ? new String[]{} : command.trim().split("\\s+");
        if (parts.length < 1 || !"access".equalsIgnoreCase(parts[0])) {
            this.sendLemonOSHelp(commandSource);
            return;
        }
        if (parts.length >= 2 && "list".equalsIgnoreCase(parts[1])) {
            this.loadAccess();
            ArrayList<String> admins = new ArrayList<String>(this.accessRepository.adminNameAccess());
            commandSource.sendMessage((Component)Component.text((String)(admins.isEmpty() ? "LemonOS access admins: none" : "LemonOS access admins: " + String.join((CharSequence)", ", admins))));
            return;
        }
        if (parts.length < 3) {
            this.sendLemonOSHelp(commandSource);
            return;
        }
        String action = parts[1].toLowerCase(Locale.ROOT);
        String name = AccessRepository.normalizeAccessName(parts[2]);
        if (name.isBlank()) {
            commandSource.sendMessage((Component)Component.text((String)"LemonOS access: invalid name."));
            return;
        }
        this.loadAccess();
        if ("add".equals(action) || "admin".equals(action)) {
            this.saveAccessFromCommand(commandSource, "added", name, true);
        } else if ("remove".equals(action) || "default".equals(action)) {
            this.saveAccessFromCommand(commandSource, "removed", name, false);
        } else {
            this.sendLemonOSHelp(commandSource);
        }
    }

    private void saveAccessFromCommand(CommandSource commandSource, String action, String name, boolean admin) {
        try {
            this.accessRepository.updateNameAccess(name, admin);
            commandSource.sendMessage((Component)Component.text((String)("LemonOS access " + action + ": " + name)));
            this.logger.info("LemonOS access {} by console for {}.", (Object)action, (Object)name);
        }
        catch (IOException exception) {
            commandSource.sendMessage((Component)Component.text((String)"LemonOS access: unable to save."));
            this.logger.warn("Unable to save LemonOS access file: {}", (Object)exception.getMessage());
        }
    }

    private void sendLemonOSHelp(CommandSource commandSource) {
        commandSource.sendMessage((Component)Component.text((String)"Usage: lemonos access add <name> | lemonos access remove <name> | lemonos access list"));
    }

    private void loadAccess() {
        this.accessRepository.load();
    }
}
