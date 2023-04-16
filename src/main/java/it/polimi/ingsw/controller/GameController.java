package it.polimi.ingsw.controller;

import it.polimi.ingsw.models.*;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import org.apache.commons.lang.NotImplementedException;

import java.util.*;

public class GameController {
    private Game game;

    public GameController(Player[] players) {
        this.game = createEmptyGame(players, selectCommonGoalCards());
        this.play();
    }

    private void play() {
        while (true) {
            throw new NotImplementedException();
            /**
             * gets from the socket the coordinate list and a column number, than if that socked mached the current
             * player, calls gameTurn(...)
             */
        }
    }

    private void gameTurn(List<Coordinates> coordinatesList, int column) {
        this.playerAction(coordinatesList, column);
        this.updateCommonGoalPoints();

        if (this.game.getCurrentPlayer().getBookshelf().isFull()) {
            this.game.setGameEnder(this.game.getCurrentPlayer());
        }


        this.game.nextPlayer();
        if (this.game.isEnded()) {
            // TODO CHANGE STATE TO "GAME FINISHED"
            throw new NotImplementedException();
        }

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

    /**
     * Updates the Players who have completed each {@link CommonGoalCard}
     * @return Current common goal points for each player
     */
    private Map<Player, Integer> updateCommonGoalPoints() {
        var players = game.getPlayers();
        var commonGoals = game.getCommonGoalCards();

        Map<Player, Integer> results = new HashMap<>();

        for (var commonGoal: commonGoals) {
            for (var player: players) {
                results.put(player, results.getOrDefault(player, 0) + commonGoal.checkGoal(player));
            }
        }

        return results;
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
