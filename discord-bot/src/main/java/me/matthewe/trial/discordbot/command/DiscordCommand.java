package me.matthewe.trial.discordbot.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public abstract class DiscordCommand  {
    private String name;


    public abstract void onCommand(@NotNull SlashCommandInteractionEvent event);


}
