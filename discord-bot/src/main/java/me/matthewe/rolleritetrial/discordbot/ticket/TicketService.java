package me.matthewe.rolleritetrial.discordbot.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    // Create a new ticket
    public Ticket createTicket(String userId, String guildId, String channelId) {
        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .guildId(guildId)
                .channelId(channelId)
                .status(Ticket.Status.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        return ticketRepository.save(ticket);
    }

    // Get all tickets by user
    public List<Ticket> getTicketsByUser(String userId) {
        return ticketRepository.findByUserId(userId);
    }

    // Get all tickets by guild
    public List<Ticket> getTicketsByGuild(String guildId) {
        return ticketRepository.findByGuildId(guildId);
    }

    // Close a ticket
    public boolean closeTicket(String ticketId) {
        Optional<Ticket> optional = ticketRepository.findById(ticketId);
        if (optional.isPresent()) {
            Ticket ticket = optional.get();
            ticket.setStatus(Ticket.Status.CLOSED);
            ticket.setClosedAt(LocalDateTime.now());
            ticketRepository.save(ticket);
            return true;
        }
        return false;
    }

    // Check if user has an open ticket in a guild
    public boolean hasOpenTicket(String userId, String guildId) {
        return ticketRepository.findByUserId(userId).stream()
                .anyMatch(t -> t.getGuildId().equals(guildId) && t.getStatus() == Ticket.Status.OPEN);
    }

    // Find ticket by channel ID
    public Optional<Ticket> getTicketByChannelId(String channelId) {
        List<Ticket> byChannelId = ticketRepository.findByChannelId(channelId);
        if (byChannelId.isEmpty())return Optional.empty();
        return Optional.of(byChannelId.getFirst());
    }

    public void save(Ticket ticket) {
        ticketRepository.save(ticket);
    }
}
