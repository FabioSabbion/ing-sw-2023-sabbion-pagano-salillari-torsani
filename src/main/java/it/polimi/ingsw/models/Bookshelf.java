package it.polimi.ingsw.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.polimi.ingsw.events.ViewEvent;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import it.polimi.ingsw.utils.Observable;

import java.io.Serializable;
import java.util.*;

import static java.lang.Math.abs;


/**
 * Representation of the current state of the bookshelf of the player that keeps it
 */
public class Bookshelf extends Observable<Bookshelf, ViewEvent> implements Serializable {
    public static final int ROWS = 6;
    public static final int COLUMNS = 5;
    private final Tile[][] bookshelf;
    public static final int[] POINTS = {0, 0, 0, 2, 3, 5, 8};

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
    @JsonIgnore
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
    @JsonIgnore
    public Map<Category, List<Integer>> getCloseTiles() {

        boolean[][] visited = new boolean[ROWS][COLUMNS];
        Map<Category, List<Integer>> groups = new HashMap<>();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (!visited[i][j] && this.bookshelf[i][j] != null) {
                    Category value = this.bookshelf[i][j].category();
                    List<Coordinates> coordinates = new ArrayList<>();
                    exploreGroup(visited, i, j, value, coordinates);

                    if (groups.containsKey(value)) {
                        groups.get(value).add(coordinates.size());
                    } else {
                        groups.put(value, new ArrayList<>(List.of(coordinates.size())));
                    }
                }
            }
        }

        return groups;
    }

    @JsonIgnore
    private void exploreGroup(boolean[][] visited, int row, int col, Category value, List<Coordinates> coordinates) {

        if (row < 0 || row >= ROWS || col < 0 || col >= COLUMNS || visited[row][col] || this.bookshelf[row][col] == null || this.bookshelf[row][col].category() != value) {
            return;
        }

        visited[row][col] = true;
        coordinates.add(new Coordinates(row, col));

        exploreGroup(visited, row - 1, col, value, coordinates); // Up
        exploreGroup(visited, row + 1, col, value, coordinates); // Down
        exploreGroup(visited, row, col - 1, value, coordinates); // Left
        exploreGroup(visited, row, col + 1, value, coordinates); // Right
    }

    @JsonIgnore
    public int getPoints() {
        Map<Category, List<Integer>> closedTiles = this.getCloseTiles();
        int total = 0;

        for (var entry : closedTiles.entrySet()) {
            for (int num : entry.getValue()) {
                total += Bookshelf.POINTS[Math.min(num, Bookshelf.POINTS.length-1)];
            }
        }

        return total;
    }

    @Override
    public String toString() {
        return "Bookshelf{" +
                "bookshelf=\n" + Arrays.stream(bookshelf).map((value) -> Arrays.toString(value) + "\n").toList() +
                '}';
    }
}
