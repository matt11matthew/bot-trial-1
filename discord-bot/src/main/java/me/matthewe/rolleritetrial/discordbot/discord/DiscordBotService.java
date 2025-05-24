package me.matthewe.rolleritetrial.discordbot.discord;

import com.ageekondemand.itassistant.bot.jda.JDAService;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log
@Service
public class DiscordBotService {

    private JDAService jdaService;

    @Autowired
    public DiscordBotService(JDAService jdaService) {
        this.jdaService = jdaService;
    }

    @PostConstruct
    public void start() {
        log.info("Started discord bot");
    }
}
