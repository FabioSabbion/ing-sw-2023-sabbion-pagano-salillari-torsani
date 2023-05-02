package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.controller.events.EventType;

import java.io.Serializable;

public class SocketMessage implements Serializable {
    public final EventType eventType;
    public final Serializable data;

    public SocketMessage(EventType eventType, Serializable data) {
        this.eventType = eventType;
        this.data = data;
    }
}
