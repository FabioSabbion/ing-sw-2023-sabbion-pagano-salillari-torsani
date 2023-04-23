package it.polimi.ingsw.controller;

import it.polimi.ingsw.models.*;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {
    public Game game;

    public GameController(List<String> nicknames) {
        this.game = Game.createEmptyGame(nicknames);
    }

    private void gameTurn(List<Coordinates> coordinatesList, int column) {
        this.playerAction(coordinatesList, column);
        this.updateCommonGoalPoints();

        if (this.game.getCurrentPlayer().getBookshelf().isFull()) {
            this.game.setGameEnder(this.game.getCurrentPlayer());
        }


        this.game.nextPlayer();
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

    public void update(List<Coordinates> coordinatesList, int column) {
        System.out.println("WARNING, fake, super fake");
        //TODO JUST FOR TEST

        this.gameTurn(coordinatesList, column);
    }
}
