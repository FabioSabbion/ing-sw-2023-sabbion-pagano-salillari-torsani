package it.polimi.ingsw.models;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Record of the message
 * @param id order of the message
 * @param from sender
 * @param to if null the message will be sent to all players
 * @param message the real message
 * @param timestamp
 */
public record Message(int id, String from, @Nullable String to, String message, LocalDateTime timestamp) implements Serializable {
}
