package me.matthewe.trial.discordbot.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Document("tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    private String id;

    private String userId;
    private String guildId;
    private String channelId;

    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    private HashSet<String> usersAdded;

    @JsonIgnore
    public void addUser(String id) {
        if (usersAdded==null){
            usersAdded = new HashSet<>();
        }
        usersAdded.add(id);
    }

    @JsonIgnore
    public boolean removeUser(String id) {
        if (usersAdded==null) {
            usersAdded =new HashSet<>();
            return false;

        }
        if (usersAdded.contains(id)) {
            return usersAdded.remove(id);
        }
        return false;
    }

    @JsonIgnore
    public boolean isAdded(String id) {
        if (usersAdded==null) {
            usersAdded =new HashSet<>();

        }
        return usersAdded.contains(id);
    }

    public enum Status {
        OPEN,
        CLOSED
    }
}