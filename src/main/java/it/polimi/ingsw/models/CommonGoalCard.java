package it.polimi.ingsw.models;

import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Representation in java of a single generic Common Goal
 */
public class CommonGoalCard implements GoalCard{
    private final Predicate<Bookshelf> controlFunction;
    private final List<Player> orderOfCompletionList;
    private final int numPlayers;

    public CommonGoalCard(Predicate<Bookshelf> controlFunction, int numPlayers) {
        this.controlFunction = controlFunction;
        this.numPlayers = numPlayers;
        this.orderOfCompletionList = new ArrayList<>(numPlayers);
    }

    public int checkGoal(Player player) {
        throw new NotImplementedException();
    }
}
