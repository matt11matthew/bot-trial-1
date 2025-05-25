package me.matthewe.trial.discordbot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import me.matthewe.trial.discordbot.discord.DiscordBotService;
import me.matthewe.trial.discordbot.ticket.Ticket;
import me.matthewe.trial.discordbot.ticket.TicketService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log
@Component
@RequiredArgsConstructor
public class CodeCommandHandler extends ListenerAdapter {

    private final TicketService ticketService;
    private final DiscordBotService discordBotService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        switch (command) {
            case "close" -> handleClose(event);
            case "new" -> handleNew(event);
            case "add" -> handleAdd(event);
            case "remove" -> handleRemove(event);
        }
    }
    private void handleRemove(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

        Optional<Ticket> ticketByChannelId = ticketService.getTicketByChannelId(event.getChannelId());
        if (!ticketByChannelId.isPresent()) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️ Ticket Not Found")
                    .setDescription("There is no ticket associated with this channel ID.")
                    .setColor(Color.YELLOW)
                    .setFooter("This message will disappear shortly");

            event.replyEmbeds(embed.build())
                    .setEphemeral(true)
                    .queue(hook -> hook.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
            return;
        }

        Ticket ticket = ticketByChannelId.get();
        User targetUser = event.getOption("user").getAsUser();
        Member targetMember = guild.getMember(targetUser);

        if (!ticket.isAdded(targetUser.getId())) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️ User Not in Ticket")
                    .setDescription(targetUser.getAsMention() + " is not a member of this ticket.")
                    .setColor(Color.ORANGE)
                    .setFooter("This message will disappear shortly");

            event.replyEmbeds(embed.build())
                    .setEphemeral(true)
                    .queue(hook -> hook.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
            return;
        }

        if (targetMember == null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌ Member Not Found")
                    .setDescription("Could not find that member in this guild.")
                    .setColor(Color.RED)
                    .setFooter("This message will disappear shortly");

            event.replyEmbeds(embed.build())
                    .setEphemeral(true)
                    .queue(hook -> hook.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
            return;
        }

        TextChannel channel = (TextChannel) event.getChannel();
        if (channel != null) {
            channel.getPermissionOverride(targetMember)
                    .delete()
                    .queue();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("✅ User Removed")
                    .setDescription(targetUser.getAsMention() + " has been removed from this ticket.")
                    .setColor(Color.GREEN)
                    .setFooter("This message will disappear shortly");

            event.replyEmbeds(embed.build())
                    .setEphemeral(false)
                    .queue();

            ticket.removeUser(targetUser.getId());
            ticketService.save(ticket);
        } else {
            event.reply("❌ Could not find the ticket channel.").setEphemeral(true).queue();
        }
    }

    private void handleAdd(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

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
        User targetUser = event.getOption("user").getAsUser();

        log.info(targetUser.getName());

        Member targetMember = guild.getMember(targetUser);


        if (ticket.isAdded(targetUser.getId())){

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️ User Already Added")
                    .setDescription(targetUser.getAsMention() + " is already a member of this ticket.")
                    .setColor(Color.ORANGE)
                    .setFooter("This message will disappear shortly");

            event.replyEmbeds(embed.build())
                    .setEphemeral(true)
                    .queue(hook -> hook.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
            return;
        }
        if (targetMember == null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌ Member Not Found")
                    .setDescription("Could not find that member in this guild.")
                    .setColor(Color.RED)
                    .setFooter("This message will disappear shortly");

            event.replyEmbeds(embed.build())
                    .setEphemeral(true)
                    .queue(hook -> hook.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
            return;
        }


        // ✅ Add user to channel permissions
        TextChannel channel = (TextChannel) event.getChannel();
        if (channel != null) {
            channel.upsertPermissionOverride(targetMember)
                    .setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                    .queue();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("✅ User Added")
                    .setDescription(targetUser.getAsMention() + " has been added to this ticket.")
                    .setColor(Color.GREEN)
                    .setFooter("This message will disappear shortly");

            event.replyEmbeds(embed.build())
                    .setEphemeral(false)
                    .queue();

            ticket.addUser(targetUser.getId());
            ticketService.save(ticket);

            channel.sendMessage(targetUser.getAsMention())   .queue(hook -> hook.delete().queueAfter(3, TimeUnit.SECONDS));
        } else {
            event.reply("❌ Could not find the ticket channel.").setEphemeral(true).queue();
        }

    }

    private void handleNew(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

        discordBotService.createTicket(event.getGuild(), event.getUser(), event);
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
