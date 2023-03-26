package it.polimi.ingsw.controller;

import it.polimi.ingsw.models.*;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import org.apache.commons.lang.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameController {
    private Game game;


    public GameController(Player[] players) {
        this.game = createEmptyGame(players, selectCommonGoalCards());
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

    private Game createEmptyGame(Player[] players, CommonGoalCard[] commonGoalCards) {
        // Create tiles
        Random rand = new Random();
        List<Tile> tiles = new ArrayList<>();
        for (Category c: Category.values()) {
            for (int i = 0; i < 22; i++) {
                tiles.add(new Tile(c, Icon.values()[rand.nextInt()%Icon.values().length], Orientation.values()[rand.nextInt()%Icon.values().length]));
            }
        }
        Collections.shuffle(tiles);
        return new Game(players, commonGoalCards, tiles, new LivingRoom());
    }

    private CommonGoalCard[] selectCommonGoalCards() {
        throw new NotImplementedException();
    }
}
