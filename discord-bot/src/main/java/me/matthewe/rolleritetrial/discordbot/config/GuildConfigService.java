package me.matthewe.rolleritetrial.discordbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Log
@Service
public class GuildConfigService {

    @Value("${config.guild-folder:classpath:guild_configs}")
    private String guildConfigPath;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private final Map<Long, GuildConfig> configMap = new HashMap<>();

    @PostConstruct
    public void loadAllConfigs() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(guildConfigPath+"/*.json");

        for (Resource resource : resources) {
            GuildConfig config = objectMapper.readValue(resource.getInputStream(), GuildConfig.class);
            configMap.put(config.getGuildId(), config);
            log.info("Loaded config for guild: " + config.getGuildId());
        }

        log.info("[GuildConfigService] Loaded " + configMap.size() + " guild configs.");
    }

    public GuildConfig getConfig(long guildId) {
        return configMap.get(guildId);
    }

    public Collection<GuildConfig> getAllConfigs() {
        return configMap.values();
    }

    public boolean hasConfig(Guild guild) {
        return configMap.containsKey(guild.getIdLong());
    }
}
