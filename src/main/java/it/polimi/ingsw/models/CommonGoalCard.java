package it.polimi.ingsw.models;

import org.apache.commons.lang.NotImplementedException;

import java.util.List;
import java.util.function.Predicate;

/**
 * Representation in java of a single generic Common Goal
 */
public class CommonGoalCard implements GoalCard{
    private Predicate<Bookshelf> controlFunction;
    private List<Player> orderOfCompletionList;
    private int numPlayers;

    public int checkGoal(Player player) {
        throw new NotImplementedException();
    }
}
