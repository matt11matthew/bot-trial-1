package me.matthewe.trial.discordbot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import me.matthewe.trial.discordbot.command.commands.AddDiscordCommand;
import me.matthewe.trial.discordbot.command.commands.CloseDiscordCommand;
import me.matthewe.trial.discordbot.command.commands.NewDiscordCommand;
import me.matthewe.trial.discordbot.command.commands.RemoveDiscordCommand;
import me.matthewe.trial.discordbot.discord.DiscordBotService;
import me.matthewe.trial.discordbot.ticket.TicketService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log
@Component
@RequiredArgsConstructor
public class CodeCommandHandler extends ListenerAdapter {

    private final TicketService ticketService;
    private final DiscordBotService discordBotService;
    private Map<String, DiscordCommand> commands;


    public void registerCommands() {

        this.commands = new HashMap<>();
        registerCommand(new RemoveDiscordCommand(ticketService));
        registerCommand(new AddDiscordCommand(ticketService));
        registerCommand(new NewDiscordCommand(discordBotService));
        registerCommand(new CloseDiscordCommand(ticketService));
    }

    private void registerCommand(DiscordCommand discordCommand) {
        this.commands.put(discordCommand.getName().toLowerCase(), discordCommand);
        log.info("Registered command " + discordCommand.getClass().getSimpleName() +" with name " + discordCommand.getName().toLowerCase());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (commands.containsKey(command.toLowerCase())) {
            commands.get(command.toLowerCase()).onCommand(event);
        }
    }
}
