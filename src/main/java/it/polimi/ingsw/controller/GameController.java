package it.polimi.ingsw.controller;

import it.polimi.ingsw.models.*;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;

public class GameController {
    private Game game;


    public GameController(Player[] players) {
        this.game = Game.emptyGame(players, selectCommonGoalCards());
        this.play();
    }

    private void play() {

    }

    private void playerAction(List<Coordinates> coordinatesList, int column) {
        try {
            List<Tile> tiles = this.game.getLivingRoom().chooseTiles(coordinatesList);
            this.game.getCurrentPlayer().getBookshelf().insertTiles(column, tiles);
            this.game.getLivingRoom().removeTiles(coordinatesList);
        } catch (PickTilesException | NotEnoughCellsException e) {
            throw new RuntimeException(e);
        }

    }


    private CommonGoalCard[] selectCommonGoalCards() {
        throw new NotImplementedException();
    }
}
