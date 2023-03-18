package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.PickTilesException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Represents the living room of the game. It contains Tiles that can be picked up by Players
 */
public class LivingRoom {
    private Tile[][] board = new Tile[9][9];

    public Tile[][] getBoard() {
        return board;
    }

    /**
     * @return Whether the board needs to be refilled
     */
    private boolean needRefill() {
        throw new NotImplementedException();
    }

    /**
     * Fill the board with new <b>Tile</b>s, accordingly to the number of players.
     * @param numPlayers
     * @param remainingTiles
     */
    private void fillBoard(int numPlayers, List<Tile> remainingTiles) {
        throw new NotImplementedException();
    }

    /**
     * Removes 1 to 3 Tiles from the board. Picked Tiles must be adjacent to each other, and must have at least a free edge
     * @param coordinates Represents the coordinates of the Tiles to pick up
     * @return The list of Tiles that have been removed from the board
     * @throws PickTilesException The picked up Tiles are in invalid positions
     */
    public List<Tile> pickTiles(List<Coordinates> coordinates) throws PickTilesException {
        throw new NotImplementedException();
    }

    /**
     * Fills the board with new <b>Tile</b>s if needed, accordingly to the number of players.
     * @param numPlayers The number of players in the game
     * @param remainingTiles The set of Tiles that can be placed on the board
     * @return Whether the board has been refilled or not
     */
    public boolean fillBoardIfNeeded(int numPlayers, List<Tile> remainingTiles) {
        throw new NotImplementedException();
    }
}

