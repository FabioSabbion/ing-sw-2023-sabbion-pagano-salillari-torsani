package it.polimi.ingsw.models;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.function.Predicate;

public class CommonGoalCard implements GoalCard{
    private Predicate<Bookshelf> controlFunction;
    private List<Player> orderOfCompletionList;
    public int checkGoal(Player player) {
        throw new NotImplementedException();
    }
}
