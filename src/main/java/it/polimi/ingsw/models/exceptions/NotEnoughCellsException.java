package it.polimi.ingsw.models.exceptions;

/**
 * thrown when a column of a {@link it.polimi.ingsw.models.Bookshelf} doesn't have enough cells to store the selected
 * tiles
 */
public class NotEnoughCellsException extends Exception{
    public NotEnoughCellsException(String message){
        super(message);
    }
}
