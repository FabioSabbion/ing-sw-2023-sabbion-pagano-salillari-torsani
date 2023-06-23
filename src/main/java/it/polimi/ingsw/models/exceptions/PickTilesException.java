package it.polimi.ingsw.models.exceptions;

/**
 * thrown when the number of {@link it.polimi.ingsw.models.Tile}s picked isn't valid
 */
public class PickTilesException extends Exception {
    public PickTilesException(String message) {
        super(message);
    }
}
