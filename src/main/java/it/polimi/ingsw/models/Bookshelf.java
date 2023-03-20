package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Representation of the current state of the bookshelf of the player that keeps it
 */
public class Bookshelf {
    private final Tile[][] bookshelf;

    public Bookshelf() {
        this.bookshelf = new Tile[6][5];
    }

    /**
     * Try to insert a list of tiles into a <b>single</b> column, one after the other in the order in which they are
     * given
     * @param column the column in which you want to insert your tiles
     * @param pickedTiles
     * @throws NotEnoughCellsException if the <b>column<b/> can't contain all the tiles selected by the player
     */
    void insertTiles(int column, List<Tile> pickedTiles) throws NotEnoughCellsException {
        throw new NotImplementedException();
    }

    /**
     * @return true if all the tiles of the bookshelf have been filled
     */
    boolean isFull() {
        throw new NotImplementedException();
    }
    public Tile[][] getBookshelf() {
        return bookshelf;
    }

    /**
     * @return A list of groups of {@link Tile}s, with the number of repetitions in the group
     */
    public List<Pair<Category, Integer>> getCloseTiles() {
        throw new NotImplementedException();
    }
}
