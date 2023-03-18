package it.polimi.ingsw.models;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.function.Predicate;

/**
 * Representation in java of a single generic Common Goal
 */
public class CommonGoalCard implements GoalCard{
    private Predicate<Bookshelf> controlFunction;
    private List<Player> orderOfCompletionList;
    private int numPlayers;

    /**
     * @param player
     * @return how many points has a player gotten for completing a goal
     */
    public int checkGoal(Player player) {
        throw new NotImplementedException();
    }
}
