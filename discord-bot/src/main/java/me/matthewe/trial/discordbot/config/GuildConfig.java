package me.matthewe.trial.discordbot.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GuildConfig {

    @JsonProperty("guildId")
    private long guildId;
    @JsonProperty("ticketCreationChannelId")
    private long ticketCreationChannelId;

    @JsonProperty("ticketsCategory")
    private long ticketsCategoryId;

}
