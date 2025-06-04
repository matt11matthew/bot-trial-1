package me.matthewe.trial.discordbot.command.commands;


import lombok.extern.java.Log;
import me.matthewe.trial.discordbot.command.DiscordCommand;
import me.matthewe.trial.discordbot.ticket.Ticket;
import me.matthewe.trial.discordbot.ticket.TicketService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log
public class AddDiscordCommand  extends DiscordCommand {

    private final TicketService ticketService;

    public AddDiscordCommand(TicketService ticketService) {
        super("add");
        this.ticketService = ticketService;
    }

    @Override
    public void onCommand(@NotNull SlashCommandInteractionEvent event) {
        handleAdd(event);
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
}
