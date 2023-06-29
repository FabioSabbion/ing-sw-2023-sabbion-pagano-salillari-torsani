package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.events.EventType;

import java.io.Serializable;

/**
 * The SocketMessage class represents a message sent over a socket connection.
 *
 * It contains an event type and associated data.
 */
public class SocketMessage implements Serializable {
    public final EventType eventType;
    public final Serializable data;

    public SocketMessage(EventType eventType, Serializable data) {
        this.eventType = eventType;
        this.data = data;
    }
}
