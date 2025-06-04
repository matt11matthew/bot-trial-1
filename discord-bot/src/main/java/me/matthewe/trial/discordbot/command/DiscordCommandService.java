package me.matthewe.trial.discordbot.command;

import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import me.matthewe.trial.discordbot.config.GuildConfigService;
import me.matthewe.trial.discordbot.discord.DiscordBotService;
import me.matthewe.trial.discordbot.discord.JDAService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class DiscordCommandService {
    @Autowired private JDAService jdaService;
    @Autowired private GuildConfigService guildConfigService;
    @Autowired private DiscordBotService botService;

    @Autowired private CodeCommandHandler commandHandler;

    @PostConstruct
    public void start() {

        commandHandler.registerCommands();
        jdaService.jda.addEventListener(commandHandler);

        jdaService.jda.getGuilds().forEach(guild -> {
            if (!guildConfigService.hasConfig(guild))return;

            registerCommands(guild);
        });
        log.info("Discord command service started");
    }


    private void registerCommands(Guild guild) {
        guild.upsertCommand("close", "Closes a ticket.").queue();
        guild.upsertCommand("new", "Creates a ticket.").queue();
        guild.upsertCommand("add", "Adds user to a ticket.")
                .addOption(OptionType.USER, "user", "User to add to ticket.", true)
                .queue();
        guild.upsertCommand("remove", "Removes user from a ticket.")
                .addOption(OptionType.USER, "user", "User to remove from ticket.", true)
                .queue();



        log.info("Discord command service registered for " + guild.getName());

    }
}
