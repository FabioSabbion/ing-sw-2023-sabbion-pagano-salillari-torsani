package it.polimi.ingsw.models;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Represents the personal goal card of a Player
 */
public class PersonalGoalCard implements GoalCard {
    private final int[] points = {0, 1, 2, 4, 6, 9, 12};
    private final List<Pair<Category, Coordinates>> positions;

    public PersonalGoalCard(List<Pair<Category, Coordinates>> positions) {
        this.positions = positions;
    }

    public int checkGoal(Player player) {
        int counterCorrect = 0;

        for (Pair<Category, Coordinates> position :
                this.positions) {
            if (player.getBookshelf().getBookshelf()[position.getRight().y][position.getRight().x].getCategory() == position.getLeft()) {
                counterCorrect++;
            }
        }

        return points[counterCorrect];
    }

    public List<Pair<Category, Coordinates>> getPositions() {
        return positions;
    }
}

