package it.polimi.ingsw.models;

import it.polimi.ingsw.controller.events.ViewEvent;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import it.polimi.ingsw.utils.Observable;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;


/**
 * Representation of the current state of the bookshelf of the player that keeps it
 */
public class Bookshelf extends Observable<Bookshelf, ViewEvent> implements Serializable {
    public static final int ROWS = 6;
    public static final int COLUMNS = 5;
    private final Tile[][] bookshelf;

    public Bookshelf() {
        this.bookshelf = new Tile[ROWS][COLUMNS];
    }

    public int pickFirstFreeIndex(int column, List<Tile> pickedTiles) throws  NotEnoughCellsException {
        int firstEmptyIndex = ROWS;
        for (int i = 0; i < ROWS; i++) {
            if(this.bookshelf[i][column] == null){
                firstEmptyIndex = i;
                break;
            }
        }
        if (firstEmptyIndex + pickedTiles.size() > ROWS){
            throw new NotEnoughCellsException("Not enough free cells available in this column");
        }

        return firstEmptyIndex;
    }

    /**
     * Try to insert a list of tiles into a <b>single</b> column, one after the other in the order in which they are
     * given
     * @param column the column in which you want to insert your tiles
     * @param pickedTiles
     * @throws NotEnoughCellsException if the <b>column<b/> can't contain all the tiles selected by the player
     */
    public void insertTiles(int column, List<Tile> pickedTiles) throws NotEnoughCellsException, PickTilesException {
        if (column < 0 || column > COLUMNS - 1){
            throw new PickTilesException("Invalid column");
        }
        if (pickedTiles.size() > 3 || pickedTiles.isEmpty()){
            throw new PickTilesException("You can't insert " + pickedTiles.size() + " tiles");
        }

        int firstEmptyIndex = this.pickFirstFreeIndex(column, pickedTiles);

        for (int i = firstEmptyIndex; i < firstEmptyIndex + pickedTiles.size(); i++) {
            this.bookshelf[i][column] = pickedTiles.get(i - firstEmptyIndex);
        }

        notifyObservers(this, ViewEvent.ACTION_UPDATE);
    }

    /**
     * @return true if all the tiles of the bookshelf have been filled
     */
    public boolean isFull() {
        for (int i = ROWS - 1; i >= 0; i--) {
            for (int j = COLUMNS - 1; j >= 0; j--) {
                if (this.bookshelf[i][j] == null){
                    return false;
                }
            }
        }
        return true;
    }
    public Tile[][] getBookshelf() {
        return bookshelf;
    }

    /**
     * @return A map of groups of {@link Tile}s, with the number of repetitions in the group
     */
    public Map<Category, List<Integer>> getCloseTiles() {
        Map<Category, List<List<Coordinates>>> groups = new HashMap<>();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if(this.bookshelf[i][j] == null) continue;
                if(!groups.containsKey(this.bookshelf[i][j].category())){
                    groups.put(this.bookshelf[i][j].category(), new ArrayList<>(List.of(new ArrayList<>(List.of(new Coordinates(i,j))))));
                }
                else{
                    List<List<Coordinates>> temp = groups.get(this.bookshelf[i][j].category());
                    for (List<Coordinates> l: temp) {
                        for (Coordinates c: l) {
                            if(abs(c.x - j) <= 1 || abs(c.y - i) <= 1){
                                l.add(c);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return groups.entrySet().stream().map((e) -> Map.entry(e.getKey(), e.getValue().stream().map(List::size).toList()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public String toString() {
        return "Bookshelf{" +
                "bookshelf=\n" + Arrays.stream(bookshelf).map((value) -> Arrays.toString(value) + "\n").toList() +
                '}';
    }
}
