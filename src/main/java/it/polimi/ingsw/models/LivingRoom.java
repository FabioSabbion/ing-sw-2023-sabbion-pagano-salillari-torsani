package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.NumPlayerException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nullable;
import javax.naming.OperationNotSupportedException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the living room of the game. It contains Tiles that can be picked up by Players
 */
public class LivingRoom {
    private final Tile[][] board;
    private final int[][] validCoords;

    public LivingRoom() {
        this.board = new Tile[9][9];
        // -1 for invalid positions
        this.validCoords = new int[][]{
                {-1, -1, -1,  3,  4, -1, -1, -1, -1},
                {-1, -1, -1,  2,  2,  2, -1, -1, -1},
                {-1, -1,  3,  2,  2,  2,  3, -1, -1},
                {-1,  4,  2,  2,  2,  2,  2,  2,  3},
                { 4,  2,  2,  2,  2,  2,  2,  2,  4},
                { 3,  2,  2,  2,  2,  2,  2,  4, -1},
                {-1, -1,  3,  2,  2,  2,  3, -1, -1},
                {-1, -1, -1,  4,  2,  2, -1, -1, -1},
                {-1, -1, -1, -1,  4,  3, -1, -1, -1}
        };
    }

    public Tile[][] getBoard() {
        return board;
    }

    /**
     * @return Whether the board needs to be refilled
     */
    public boolean needRefill() {
        if (Arrays.stream(this.board).allMatch((row) ->
                Arrays.stream(row).allMatch(Objects::isNull)
        )) {
            return true;
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.board[i][j] != null && this.validCoords[i][j] != -1) {
                    if (!this.hasAdjacentTile(i, j))
                        return true;
                }
            }
        }
        return false;
    }

    private boolean hasAdjacentTile(int i, int j) {
        if (i != 8 && this.board[i + 1][j] != null) return true;
        if (i != 0 && this.board[i - 1][j] != null) return true;
        if (j != 8 && this.board[i][j + 1] != null) return true;
        if (j != 0 && this.board[i][j - 1] != null) return true;
        return false;
    }

    /**
     * Fill the board with new <b>Tile</b>s, accordingly to the number of players.
     *
     * @param numPlayers
     * @param remainingTiles Will remove tiles from remainingTiles and insert them into the board
     */
    public void fillBoard(int numPlayers, List<Tile> remainingTiles) throws NumPlayerException {
        if (numPlayers <= 1 || numPlayers > 4) throw new NumPlayerException("You can't choose " + numPlayers + " players");
        List<Coordinates> validCoordinates = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.board[i][j] == null && this.validCoords[i][j] != -1 && this.validCoords[i][j] <= numPlayers) {
                    validCoordinates.add(new Coordinates(i, j));
                }
            }
        }
        Collections.shuffle(validCoordinates);
        while (!validCoordinates.isEmpty() && !remainingTiles.isEmpty()) {
            Coordinates popped = validCoordinates.remove(0);
            this.board[popped.x][popped.y] = remainingTiles.remove(0);
        }
    }

    /**
     * Removes 1 to 3 Tiles from the board. Picked Tiles must be adjacent to each other, and must have at least a free edge
     *
     * @param coordinates Represents the coordinates of the Tiles to pick up
     * @return The list of Tiles that have been removed from the board
     * @throws PickTilesException The picked up Tiles are in invalid positions or invalid number of coordinates
     */
    public List<Tile> chooseTiles(List<Coordinates> coordinates) throws PickTilesException {
        List<Tile> picked = new ArrayList<>();
        if (coordinates.size() > 3 || coordinates.isEmpty())
            throw new PickTilesException("You can't pick " + coordinates.size() + " tiles");
        for (Coordinates coordinate : coordinates) {
            //Checking for invalid coordinates
            if (!(0 <= coordinate.x && coordinate.x < 9) || !(0 <= coordinate.y && coordinate.y < 9) || this.validCoords[coordinate.x][coordinate.y] == -1)
                throw new PickTilesException("Coordinates outside boundaries");
            //Checking for empty coordinate in the board
            if (this.board[coordinate.x][coordinate.y] == null)
                throw new PickTilesException("No tiles in coordinates: %d %d".formatted(coordinate.x, coordinate.y));
            // Looking if "coordinates" exceeds maximum size
        }
        // Now verify if they are adjacent and form a straight line

            int[] x = new int[coordinates.size()];
            int[] y = new int[coordinates.size()];

            for (int i = 0; i < coordinates.size(); i++) {
                x[i] = coordinates.get(i).x;
                y[i] = coordinates.get(i).y;
            }
            if (Arrays.stream(x).allMatch((val) -> val == x[0])) {
                List<Integer> sortedY = Arrays.stream(y).sorted().boxed().toList();
                for (int i = 1; i < sortedY.size(); i++) {
                    if ((sortedY.get(i - 1) + 1) != (sortedY.get(i))) {
                        throw new PickTilesException("No straight line");
                    }
                }
            } else if (Arrays.stream(y).allMatch((val) -> val == y[0])) {
                List<Integer> sortedX = Arrays.stream(x).sorted().boxed().toList();
                for (int i = 1; i < sortedX.size(); i++) {
                    if ((sortedX.get(i - 1) + 1) != (sortedX.get(i))) {
                        throw new PickTilesException("No straight line");
                    }
                }
            }


        // Removing them from the livingRoom and returning them to the player
        for (Coordinates c : coordinates) {
            picked.add(this.board[c.x][c.y]);
        }

        return picked;
    }

    /**
     * Fills the board with new <b>Tile</b>s if needed, accordingly to the number of players.
     *
     * @param numPlayers     The number of players in the game
     * @param remainingTiles The set of Tiles that can be placed on the board
     * @return Whether the board has been refilled or not
     */
    public boolean fillBoardIfNeeded(int numPlayers, List<Tile> remainingTiles) {
        if (needRefill()) {
            fillBoard(numPlayers, remainingTiles);
            return true;
        }
        return false;
    }

    public void pickTiles(List<Coordinates> coordinates) throws PickTilesException{
        for (Coordinates coords : coordinates) {
            if (this.board[coords.x][coords.y] == null)
                throw new PickTilesException("No tiles in coordinates: %d %d".formatted(coords.x, coords.y));
            this.board[coords.x][coords.y] = null;
        }
    }
}

