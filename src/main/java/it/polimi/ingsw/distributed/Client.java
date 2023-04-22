package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.events.ViewEvent;
import it.polimi.ingsw.utils.Observer;

public interface Client extends Observer<GameUpdate, ViewEvent> {
    /**
     * Notify the client of a model change
     * @param o     The resulting model view
     * @param arg   The causing event
     */
    void update(GameUpdate o, ViewEvent arg);
}
