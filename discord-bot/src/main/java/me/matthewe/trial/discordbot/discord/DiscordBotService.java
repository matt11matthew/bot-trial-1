package me.matthewe.trial.discordbot.discord;

import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import me.matthewe.trial.discordbot.config.GuildConfig;
import me.matthewe.trial.discordbot.config.GuildConfigService;
import me.matthewe.trial.discordbot.ticket.TicketService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.EnumSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Log
@Service
public class DiscordBotService  extends ListenerAdapter {

    private JDAService jdaService;
    private GuildConfigService configService;
    private TicketService ticketService;

    @Autowired
    public DiscordBotService(JDAService jdaService, GuildConfigService configService, TicketService ticketService) {
        this.jdaService = jdaService;
        this.configService = configService;
        this.ticketService = ticketService;
    }

    private String tempButtonId;

    @PostConstruct
    public void start() {
        log.info("Started discord bot");

        JDA jda = jdaService.jda;

        jda.addEventListener(this);
        for (GuildConfig allConfig : configService.getAllConfigs()) {
            Guild guildById = jda.getGuildById(allConfig.getGuildId());
            if (guildById == null) {
                log.severe("Could not find guild with id " + allConfig.getGuildId());
                continue;
            }
            log.info("Loading config for guild " + guildById.getName());
            TextChannel channel = guildById.getTextChannelById(allConfig.getTicketCreationChannelId());
            if (channel == null) {

                log.severe(guildById.getName() + " ticket creation channel not found.");
                continue;
            }
            for (Message message : channel.getHistory().retrievePast(100).complete()) {
                log.info(guildById.getName() + " " + message.getAuthor().getName());
                message.delete().complete();
            }
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🎫 Create a Ticket")
                    .setFooter(guildById.getName())
                    .setDescription("Click the button below to create a support ticket.\n\nPlease describe your issue clearly once the ticket is opened.")
                    .setColor(Color.BLUE);

            this.tempButtonId = "create-ticket-" + (UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5).trim());

            Button button = Button.primary(tempButtonId, "🎟 Create Ticket");

            channel.sendMessageEmbeds(embed.build())
                    .addActionRow(button)
                    .queue();

        }
    }

    public void createTicket(Guild guild, User user, Event event) {
        String ticketChannelName = "ticket-" + user.getName();

        GuildConfig config = configService.getConfig(guild.getIdLong());
        Category categoryById = guild.getCategoryById(config.getTicketsCategoryId());
        if (categoryById == null) {
            if (event == null) return;
            if (event instanceof ButtonInteractionEvent) {

                ((ButtonInteractionEvent) event).reply("Ticket category not found. Please contact an admin.").setEphemeral(true).queue();
            } else if (event instanceof SlashCommandInteractionEvent) {
                ((SlashCommandInteractionEvent) event).reply("Ticket category not found. Please contact an admin.").setEphemeral(true).queue();

            }

            return;
        }

        categoryById.createTextChannel(ticketChannelName)
                .addPermissionOverride(guild.getPublicRole(), EnumSet.noneOf(Permission.class), EnumSet.of(Permission.VIEW_CHANNEL))
                .addPermissionOverride(guild.getMember(user), EnumSet.of(
                        Permission.VIEW_CHANNEL,
                        Permission.MESSAGE_SEND,
                        Permission.MESSAGE_HISTORY
                ), EnumSet.noneOf(Permission.class))
                .queue(channel -> {
                    EmbedBuilder ticketEmbed = new EmbedBuilder()
                            .setTitle("🎟 Ticket Opened")
                            .setDescription("Hello " + user.getAsMention() + "! A staff member will assist you shortly.\n\n"
                                    + "Please describe your issue clearly.")
                            .addField("📋 Available Commands",
                                    """
                                            `/add <user>` - Add someone to the ticket  
                                            `/remove <user>` - Remove someone from the ticket  
                                            `/close` - Close the ticket  
                                            """,
                                    false)
                            .setColor(Color.CYAN)
                            .setFooter("Use the slash commands above if needed.");

                    channel.sendMessage(user.getAsMention()).queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                    channel.sendMessageEmbeds(ticketEmbed.build()).queue();

                    TextChannel finalChannel = channel; // from inside the channel creation callback

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("🎫 Ticket Created")
                            .setDescription("Your ticket has been created: " + finalChannel.getAsMention())
                            .setColor(Color.GREEN)
                            .setFooter("This message will disappear in 5 seconds");

                    if (event == null) return;
                    if (event instanceof ButtonInteractionEvent) {

                        ((ButtonInteractionEvent) event).replyEmbeds(embed.build())
                                .setEphemeral(true)
                                .queue(interactionHook ->
                                        interactionHook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
                                );
                    } else if (event instanceof SlashCommandInteractionEvent) {
                        ((SlashCommandInteractionEvent) event).replyEmbeds(embed.build())
                                .setEphemeral(true)
                                .queue(interactionHook ->
                                        interactionHook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
                                );
                    }
                    ticketService.createTicket(user.getId(), guild.getId(), channel.getId());
                });

    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals(tempButtonId)) {
            // Your logic to check if the user already has a ticket, etc.
            log.info("Button clicked on create ticket");
            // Example ticket creation logic:
            Guild guild = event.getGuild();
            if (guild == null) return;

            createTicket(guild, event.getUser(), event);


        }
    }
}
