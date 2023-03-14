package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class Bookshelf {
    private Tile[][] bookshelf = new Tile[6][5];

    void insertTiles(int column, List<Tile> pickedTiles) throws NotEnoughCellsException {
        throw new NotImplementedException();
    }

    boolean isFull() {
        throw new NotImplementedException();
    }
    public Tile[][] getBookshelf() {
        return bookshelf;
    }
}
