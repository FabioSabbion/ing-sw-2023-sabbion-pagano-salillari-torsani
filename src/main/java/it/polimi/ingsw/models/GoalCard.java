package it.polimi.ingsw.models;

/**
 * Interface for the goal cards of the game
 */
public interface GoalCard {

    /**
     * Check if a goal has been achieved in the Bookshelf of the Player
     * @param player The player to check
     * @return The score gained by the player. 0 if the player has not achieved the goal
     */
    public int checkGoal(Player player);
}
