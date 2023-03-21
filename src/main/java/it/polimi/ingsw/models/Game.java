package it.polimi.ingsw.models;


import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all game information
 */
public class Game {
    private @Nullable Player gameEnder;
    private Player currentPlayer;
    private final Player[] players;
    private final CommonGoalCard[] commonGoalCards;
    private final List<Tile> remainingTiles;
    private final LivingRoom livingRoom;
    public static int[] personalGoalCardScores = new int[]{0,1,2,4,6,9,12};

    public Game(Player[] players, CommonGoalCard[] commonGoalCards, List<Tile> remainingTiles, LivingRoom livingRoom) {
        this.players = players;
        this.commonGoalCards = commonGoalCards;
        this.remainingTiles = remainingTiles;
        this.livingRoom = livingRoom;
        this.currentPlayer = players[0];
    }

    public @Nullable Player getGameEnder() {
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
        Map<Player, Integer> scoreboard = new HashMap<>();
        for (Player p: this.players) {
            int pScore = 0;
            if (p == this.gameEnder)
                pScore += 1;
            for (CommonGoalCard cgc: this.commonGoalCards)
                pScore += cgc.checkGoal(p);
            List<Pair<Category, Integer>> closeTiles = p.getBookshelf().getCloseTiles();
            for (int n: closeTiles.stream().mapToInt(Pair::getRight).toArray()) {
                if (n == 3)
                    pScore += 2;
                if (n == 4)
                    pScore += 3;
                if (n == 5)
                    pScore += 5;
                if (n >= 6)
                    pScore += 8;
            }
            int count = 0;
            for (Pair<Category, Coordinates> pair: p.getPersonalGoalCard().getPositions()) {
                if (p.getBookshelf().getBookshelf()[pair.getRight().x][pair.getRight().y].getCategory() == pair.getLeft())
                    count++;
            }
            pScore += personalGoalCardScores[count];
            scoreboard.put(p, pScore);
        }
        return scoreboard;
    }

    /**
     * Change <b>currentPlayer</b> to the next player. Order is defined by <b>players</b>
     */
    public void nextPlayer() {
        int index = Arrays.asList(this.players).indexOf(this.currentPlayer);
        this.currentPlayer = this.players[(index + 1) % this.players.length];
    }

    /**
     * @return Whether the game is ended
     */
    public boolean isEnded() {
        return (this.gameEnder != null && this.currentPlayer == this.players[0]);
    }

    public void setGameEnder(Player gameEnder) {
        this.gameEnder = gameEnder;
    }
}
