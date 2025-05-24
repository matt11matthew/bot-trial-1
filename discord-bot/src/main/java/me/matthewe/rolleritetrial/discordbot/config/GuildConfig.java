package me.matthewe.rolleritetrial.discordbot.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GuildConfig {

    @JsonProperty("guildId")
    private long guildId;

}
