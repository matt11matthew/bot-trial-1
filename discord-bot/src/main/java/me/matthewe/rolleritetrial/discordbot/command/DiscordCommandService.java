package me.matthewe.rolleritetrial.discordbot.command;

import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import me.matthewe.rolleritetrial.discordbot.config.GuildConfigService;
import me.matthewe.rolleritetrial.discordbot.discord.DiscordBotService;
import me.matthewe.rolleritetrial.discordbot.discord.JDAService;
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

    @Autowired private CodeAutoCompleteHandler codeAutoCompleteHandler;
    @Autowired private CodeCommandHandler commandHandler;

    @PostConstruct
    public void start() {

        jdaService.jda.addEventListener(codeAutoCompleteHandler);
        jdaService.jda.addEventListener(commandHandler);

        jdaService.jda.getGuilds().forEach(guild -> {
            if (!guildConfigService.hasConfig(guild))return;

            registerCommands(guild);
        });
        log.info("Discord command service started");
    }


    private void registerCommands(Guild guild) {
//        guild.upsertCommand("createcode", "Create a new code")
//                .addOption(OptionType.STRING, "code", "The code value", true, false)
//                .addOption(OptionType.STRING, "customer", "Customer name", true, false)
//                .addOption(OptionType.STRING, "time", "Expiration time (e.g. 10d, 2h, -1 for, forever)", true, false)
//                .queue();
//        guild.upsertCommand("extendcode", "Extends a code")
//                .addOption(OptionType.STRING, "code", "The code value", true, true)
//                .addOption(OptionType.STRING, "time", "Extension time (e.g. 10d, 2h, -1 for, forever)", true, false)
//                .queue();
//        guild.upsertCommand("cancelcode", "Cancels a code")
//                .addOption(OptionType.STRING, "code", "The code value", true, true)
//                .queue();

        log.info("Discord command service registered for " + guild.getName());

    }
}
