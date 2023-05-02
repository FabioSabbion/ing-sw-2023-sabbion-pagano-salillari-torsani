package it.polimi.ingsw.models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Representation in java of a single generic Common Goal
 */
public class CommonGoalCard implements GoalCard {
    private final int[][] points = {
            {},
            {},
            {8, 4},
            {8, 6, 4},
            {8, 6, 4, 2}
    };
    private final Predicate<Bookshelf> controlFunction;
    private final List<Player> orderOfCompletionList;
    private final int numPlayers;

    private final int cgcNum;

    public CommonGoalCard(Predicate<Bookshelf> controlFunction, int numPlayers, int cgcNum) {
        this.controlFunction = controlFunction;
        this.numPlayers = numPlayers;
        this.orderOfCompletionList = new ArrayList<>(numPlayers);
        this.cgcNum = cgcNum;
    }

    public int checkGoal(Player player) {
        // if the player has already been registered getting a certain result, we give back the same result
        if(orderOfCompletionList.contains(player)) {
            return points[numPlayers][orderOfCompletionList.indexOf(player)];
        }

        if(controlFunction.test(player.getBookshelf())) {
            orderOfCompletionList.add(player);

            return points[numPlayers][orderOfCompletionList.size() - 1];
        }

        return 0;
    }

    static CommonGoalCard[] createCommonGoalCards(int numPlayers) {
        //TODO to implement
        System.out.println("WARNING, this is fake");

        return new CommonGoalCard[]{new CommonGoalCard((bookshelf) -> {
            return false;
        }, numPlayers, 0)};
    }
}
