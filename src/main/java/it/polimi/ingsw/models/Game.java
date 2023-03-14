package it.polimi.ingsw.models;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Map;

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

    public Map<Player, Integer> getScoreBoard() {
        throw new NotImplementedException();
    }

    public void nextPlayer() {
        throw new NotImplementedException();
    }
    public boolean isEnded() {
        throw new NotImplementedException();
    }
}
