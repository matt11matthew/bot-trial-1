package me.matthewe.rolleritetrial.discordbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class DiscordBotApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(DiscordBotApplication.class, args);
        new CountDownLatch(1).await(); // block forever
    }

}
