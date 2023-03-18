package it.polimi.ingsw.models;


import org.apache.commons.lang.NotImplementedException;

import java.util.List;
import java.util.Map;

/**
 * Contains all game information
 */
public class Game {
    private Player gameEnder;
    private Player currentPlayer;
    private Player[] players;
    private CommonGoalCard[] commonGoalCards;
    private List<Tile> remainingTiles;
    private LivingRoom livingRoom;

    public Player getGameEnder() {
        return gameEnder;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }

    public CommonGoalCard[] getCommonGoalCards() {
        return commonGoalCards;
    }

    public LivingRoom getLivingRoom() {
        return livingRoom;
    }

    /**
     * Calculates the current scores for all the players
     * @return Current scores for all the players
     */
    public Map<Player, Integer> getScoreBoard() {
        throw new NotImplementedException();
    }

    /**
     * Change <b>currentPlayer</b> to the next player. Order is defined by <b>players</b>
     */
    public void nextPlayer() {
        throw new NotImplementedException();
    }

    /**
     * @return Whether the game is ended
     */
    public boolean isEnded() {
        throw new NotImplementedException();
    }
}
