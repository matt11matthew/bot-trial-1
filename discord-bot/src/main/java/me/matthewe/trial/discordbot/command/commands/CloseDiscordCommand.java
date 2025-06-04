package me.matthewe.trial.discordbot.command.commands;


import lombok.extern.java.Log;
import me.matthewe.trial.discordbot.command.DiscordCommand;
import me.matthewe.trial.discordbot.ticket.Ticket;
import me.matthewe.trial.discordbot.ticket.TicketService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log
public class CloseDiscordCommand extends DiscordCommand {

    private final TicketService ticketService;

    public CloseDiscordCommand(TicketService ticketService) {
        super("close");
        this.ticketService = ticketService;
    }

    @Override
    public void onCommand(@NotNull SlashCommandInteractionEvent event) {
        handleClose(event);
    }


    private void handleClose(SlashCommandInteractionEvent event) {
        Optional<Ticket> ticketByChannelId = ticketService.getTicketByChannelId(event.getChannelId());
        if (!ticketByChannelId.isPresent()) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️ Ticket Not Found")
                    .setDescription("There is no ticket associated with this channel ID.")
                    .setColor(Color.YELLOW)
                    .setFooter("This message will disappear shortly");

            event.replyEmbeds(embed.build())
                    .setEphemeral(true)
                    .queue(hook -> hook.deleteOriginal().queueAfter(8000, TimeUnit.MILLISECONDS));
            return;
        }

        Ticket ticket = ticketByChannelId.get();

        boolean b = ticketService.closeTicket(ticket.getId());
        if (!b) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️ Major Ticket Error")
                    .setDescription("There is no ticket associated with the id in this channel.")
                    .setColor(Color.RED)
                    .setFooter("This message will disappear shortly");

            event.replyEmbeds(embed.build())
                    .setEphemeral(true)
                    .queue(hook -> hook.deleteOriginal().queueAfter(8000, TimeUnit.MILLISECONDS));
            return;
        }
        event.getChannel().delete().queue();
    }
}
