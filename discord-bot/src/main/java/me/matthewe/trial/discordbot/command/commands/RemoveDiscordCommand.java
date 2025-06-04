package me.matthewe.trial.discordbot.command.commands;

import me.matthewe.trial.discordbot.command.DiscordCommand;
import me.matthewe.trial.discordbot.ticket.Ticket;
import me.matthewe.trial.discordbot.ticket.TicketService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RemoveDiscordCommand  extends DiscordCommand {

    private final TicketService ticketService;

    public RemoveDiscordCommand(TicketService ticketService) {
        super("remove");
        this.ticketService = ticketService;
    }

    @Override
    public void onCommand(@NotNull SlashCommandInteractionEvent event) {
        handleRemove(event);
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
}
