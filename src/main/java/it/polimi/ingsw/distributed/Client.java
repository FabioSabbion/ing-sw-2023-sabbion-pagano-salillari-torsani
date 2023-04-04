package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.events.ViewEvent;

public interface Client {
    /**
     * Notify the client of a model change
     * @param o     The resulting model view
     * @param arg   The causing event
     */
    void update(GameUpdate o, ViewEvent arg);
}
