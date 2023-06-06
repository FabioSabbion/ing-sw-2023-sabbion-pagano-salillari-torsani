package it.polimi.ingsw.controller;

import it.polimi.ingsw.models.*;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {
    public Game game;
    public int id;
    public static int ID = 0;

    public GameController(Game game, int id) {
        this.game = game;
        this.id = id;
    }
    public GameController(List<String> nicknames) {
        this.game = Game.createEmptyGame(nicknames);
        this.id = ID++;
    }

    private void gameTurn(List<Coordinates> coordinatesList, int column) throws PickTilesException, NotEnoughCellsException {
        this.playerAction(coordinatesList, column);
        this.updateCommonGoalPoints();


        var prevPlayer = this.game.getCurrentPlayer();

        this.game.getLivingRoom().fillBoardIfNeeded(this.game.getPlayers().length, this.game.getRemainingTiles());

        this.game.nextPlayer();

        if (prevPlayer.getBookshelf().isFull()) {
            this.game.setGameEnder(prevPlayer);
        }
    }

    private void playerAction(List<Coordinates> coordinatesList, int column) throws PickTilesException, NotEnoughCellsException {
        List<Tile> tiles = this.game.getLivingRoom().chooseTiles(coordinatesList);
        this.game.getCurrentPlayer().getBookshelf().insertTiles(column, tiles);
        this.game.getLivingRoom().removeTiles(coordinatesList);

        Arrays.stream(this.game.getCommonGoalCards()).forEach(commonGoalCard ->
                commonGoalCard.checkGoal(this.game.getCurrentPlayer())
        );
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

    public void update(List<Coordinates> coordinatesList, int column, String player) throws PickTilesException, NotEnoughCellsException {
        if (player.equals(game.getCurrentPlayer().getNickname())) {
            this.gameTurn(coordinatesList, column);
        }
    }
}
