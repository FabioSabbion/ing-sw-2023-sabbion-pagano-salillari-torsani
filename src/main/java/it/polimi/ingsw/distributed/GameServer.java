package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.events.ControllerEvent;

public interface GameServer {
    /**
     * Register a client to the server
     * @param client the client to register
     */
    void register(Client client);

    /**
     * Notify the server that a client has made a choice
     * @param client  the client that generated the event
     * @param arg     the choice made by the client
     */
    void update(Client client, ControllerEvent arg);
}
