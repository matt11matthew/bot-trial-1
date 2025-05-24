package me.matthewe.rolleritetrial.discordbot.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CodeAutoCompleteHandler extends ListenerAdapter {
//    private final ObjectProvider<CodeService> codeServiceProvider;

//    public CodeAutoCompleteHandler(ObjectProvider<CodeService> codeServiceProvider) {
//        this.codeServiceProvider = codeServiceProvider;
//    }
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        String command = event.getName();
        String focused = event.getFocusedOption().getName();

//        if ((command.equals("extendcode") || command.equals("cancelcode")) && focused.equals("code")) {
//
//            CodeService codeService = codeServiceProvider.getIfAvailable();
//            if (codeService == null) {
//                event.replyChoices(List.of()).queue();
//                return;
//            }
//
//            List<String> allCodes = switch (command) {
//                case "cancelcode" -> codeService.getActiveList();
//                case "extendcode" -> codeService.getAllList();
//                default -> List.of();
//            };
//
//            String userInput = event.getFocusedOption().getValue();
//
//            List<Command.Choice> matches = allCodes.stream()
//                    .filter(code -> code.toLowerCase().contains(userInput.toLowerCase()))
//                    .limit(25)
//                    .map(code -> new Command.Choice(code, code))
//                    .collect(Collectors.toList());
//
//            event.replyChoices(matches).queue();
//        }
    }
}
