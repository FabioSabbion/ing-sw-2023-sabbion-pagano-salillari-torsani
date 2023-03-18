package it.polimi.ingsw.models;

import jdk.javadoc.internal.doclets.toolkit.util.Utils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Represents the personal goal card of a Player
 */
public class PersonalGoalCard implements GoalCard {
    private List<Utils.Pair<Category, Coordinates>> positions;
    /**
     * Check if the personal goal has been achieved in the Bookshelf of the Player
     * @param player The player to check
     * @return The score gained by the player. 0 if the player has not achieved the goal
     */
    public int checkGoal(Player player) {
        throw new NotImplementedException();
    }

    public List<Utils.Pair<Category, Coordinates>> getPositions() {
        return positions;
    }
}

