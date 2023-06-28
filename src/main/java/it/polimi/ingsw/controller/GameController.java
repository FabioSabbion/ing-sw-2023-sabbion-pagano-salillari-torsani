package it.polimi.ingsw.controller;

import it.polimi.ingsw.models.*;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the main controller for the game, that implements various methods to control the actions of the players throughout
 * the game.
 */
public class GameController {
    public final Game game;
    public final Chat chat;
    public final int id;
    public static int ID = 0;

    public GameController(Game game, int id) {
        this.game = game;
        this.id = id;
        this.chat = new Chat();
    }

    public GameController(List<String> nicknames) {
        this.game = Game.createEmptyGame(nicknames);
        this.id = ID++;
        this.chat = new Chat();
    }

    /**
     * manages the turn of a player, updating the state of the game
     * @param coordinatesList the list of {@link Coordinates} of the tiles picked by the player
     * @param column the column chosen by the player
     * @param offlineNicknames the nicknames of the players that have disconnected
     * @throws PickTilesException if the tiles chosen are not pickable
     * @throws NotEnoughCellsException if the number of cells isn't valid
     */
    private void gameTurn(List<Coordinates> coordinatesList, int column, List<String> offlineNicknames) throws PickTilesException, NotEnoughCellsException  {
        this.playerAction(coordinatesList, column);
        this.updateCommonGoalPoints();


        var prevPlayer = this.game.getCurrentPlayer();

        this.game.getLivingRoom().fillBoardIfNeeded(this.game.getPlayers().length, this.game.getRemainingTiles());

        if (prevPlayer.getBookshelf().isFull() && this.game.getGameEnder() == null) {
            this.game.setGameEnder(prevPlayer);
        }

        this.game.nextPlayer(offlineNicknames);

        if (prevPlayer.getNickname().equals(this.game.getCurrentPlayer().getNickname()) &&
            this.game.getGameEnder() != null) {
            this.game.emitGameState(true);
        }
    }

    /**
     * manages the action of a single player by inserting the chosen {@link Tile}s in the chosen column
     * @param coordinatesList the list of coordinates of the tiles chosen
     * @param column the column chosen by the player
     * @throws PickTilesException if the tiles aren't pickable
     * @throws NotEnoughCellsException if the number of cells isn't valid
     */
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
     *
     * @return Current common goal points for each player
     */
    private Map<Player, Integer> updateCommonGoalPoints() {
        var players = game.getPlayers();
        var commonGoals = game.getCommonGoalCards();

        Map<Player, Integer> results = new HashMap<>();

        for (var commonGoal : commonGoals) {
            for (var player : players) {

                results.put(player, results.getOrDefault(player, 0) + commonGoal.checkGoal(player));
            }
        }

        return results;
    }

    /**
     * calls the method gameTurn if the player is the current player
     * @param coordinatesList the list of coordinates of the tiles picked by the player
     * @param column the column chosen by the player
     * @param player the player that has to execute the gameTurn method
     * @param offlineNicknames the nicknames of the players that have disconnected
     * @throws PickTilesException if the tiles aren't pickable
     * @throws NotEnoughCellsException if the number of cells isn't valid
     */
    public void update(List<Coordinates> coordinatesList, int column, String player, List<String> offlineNicknames) throws PickTilesException, NotEnoughCellsException {
        if (player.equals(game.getCurrentPlayer().getNickname())) {
            this.gameTurn(coordinatesList, column, offlineNicknames);
        }
    }
}

