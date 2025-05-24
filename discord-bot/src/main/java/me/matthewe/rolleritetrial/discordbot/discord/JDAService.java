package me.matthewe.rolleritetrial.discordbot.discord;


import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.stereotype.Service;

@Service
@Log
public class JDAService {

    public JDA jda;

    @PostConstruct
    public void start() throws Exception {
        this.jda = JDABuilder.createDefault(System.getenv("BOT_TOKEN")).build();
        jda.awaitReady(); // wait for JDA to be fully loaded
        jda.getPresence().setActivity(Activity.watching("Tickets"));
        log.info("JDA started");
    }

    public JDA getJda() {
        return jda;
    }
}
