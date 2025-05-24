package me.matthewe.rolleritetrial.discordbot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Log
@Component
@RequiredArgsConstructor
public class CodeCommandHandler extends ListenerAdapter {

//    private final CodeService codeService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

//        switch (command) {
//            case "createcode" -> handleCreate(event);
//            case "extendcode" -> handleExtend(event);
//            case "cancelcode" -> handleCancel(event);
//        }
    }


}
