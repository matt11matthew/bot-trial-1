package me.matthewe.trial.discordbot.command.commands;


import lombok.extern.java.Log;
import me.matthewe.trial.discordbot.command.DiscordCommand;
import me.matthewe.trial.discordbot.discord.DiscordBotService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@Log
public class NewDiscordCommand extends DiscordCommand {

    private final DiscordBotService discordBotService;

    public NewDiscordCommand(DiscordBotService discordBotService) {
        super("new");
        this.discordBotService = discordBotService;
    }

    @Override
    public void onCommand(@NotNull SlashCommandInteractionEvent event) {
        handleNew(event);
    }

    private void handleNew(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

        discordBotService.createTicket(event.getGuild(), event.getUser(), event);
    }
}
