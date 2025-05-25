package me.matthewe.rolleritetrial.discordbot.ticket;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    List<Ticket> findByGuildId(String guildId);
    List<Ticket> findByUserId(String userId);
    List<Ticket> findByChannelId(String channelId);
}