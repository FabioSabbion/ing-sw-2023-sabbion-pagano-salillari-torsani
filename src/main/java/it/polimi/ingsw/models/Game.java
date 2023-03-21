package it.polimi.ingsw.models;

import org.apache.commons.lang.NotImplementedException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.*;

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

    private Game(Player[] players, CommonGoalCard[] commonGoalCards, List<Tile> remainingTiles, LivingRoom livingRoom) {
        this.players = players;
        this.commonGoalCards = commonGoalCards;
        this.remainingTiles = remainingTiles;
        this.livingRoom = livingRoom;
        this.currentPlayer = players[0];
    }

    static public Game emptyGame(Player[] players, CommonGoalCard[] commonGoalCards) {
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

    static public Game fromJson(JSONObject json) {
        throw new NotImplementedException();
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
