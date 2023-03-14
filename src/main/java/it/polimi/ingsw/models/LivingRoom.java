package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.PickTilesException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class LivingRoom {
    private Tile[][] board = new Tile[9][9];

    public Tile[][] getBoard() {
        return board;
    }

    private boolean needRefill() {
        throw new NotImplementedException();
    }
    private void fillBoard(int numPlayers, List<Tile> remainingTiles) {
        throw new NotImplementedException();
    }

    public List<Tile> pickTiles(List<Coordinates> coordinates) throws PickTilesException {
        throw new NotImplementedException();
    }

    public boolean fillBoardIfNeeded(int numPlayers, List<Tile> remainingTiles) {
        throw new NotImplementedException();
    }
}

