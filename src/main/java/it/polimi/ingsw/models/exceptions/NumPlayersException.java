package it.polimi.ingsw.models.exceptions;

/**
 * thrown when the number of players isn't valid
 */
public class NumPlayersException extends RuntimeException {
    public NumPlayersException(String message) {
        super(message);
    }
}
