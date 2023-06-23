package it.polimi.ingsw.models;

import it.polimi.ingsw.controller.events.ViewEvent;
import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.utils.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Representation in java of a single generic Common Goal
 */
public class CommonGoalCard extends Observable<CommonGoalCardUpdate, ViewEvent> implements GoalCard {
    public static final int[][] points = {
            {},
            {},
            {8, 4},
            {8, 6, 4},
            {8, 6, 4, 2}
    };
    private final Predicate<Bookshelf> controlFunction;
    public final List<Player> orderOfCompletionList;
    private final int numPlayers;

    public final int cardID;

    public CommonGoalCard(Predicate<Bookshelf> controlFunction, int numPlayers, int cardID) {
        this.controlFunction = controlFunction;
        this.numPlayers = numPlayers;
        this.orderOfCompletionList = new ArrayList<>(numPlayers);
        this.cardID = cardID;
    }

    /**
     * checks the goal for the specified player
     * @param player The player to check
     * @return the points the player hase scored in the CommonGoalCard
     */
    public int checkGoal(Player player) {
        // if the player has already been registered getting a certain result, we give back the same result
        if(orderOfCompletionList.contains(player)) {
            return points[numPlayers][orderOfCompletionList.indexOf(player)];
        }

        if(controlFunction.test(player.getBookshelf())) {
            orderOfCompletionList.add(player);

            this.notifyObservers(from(this), ViewEvent.ACTION_UPDATE);

            return points[numPlayers][orderOfCompletionList.size() - 1];
        }

        return 0;
    }

    /**
     * generates a CommonGoalCardUpdate from a CommonGoalCard
     * @param card the CommonGoalCard of which to generate the update
     * @return the CommonGoalCardUpdate generated
     */
    static public CommonGoalCardUpdate from(CommonGoalCard card) {
        return new CommonGoalCardUpdate(card.cardID,
                card.orderOfCompletionList.stream().map(Player::getNickname).toList());
    }
}
