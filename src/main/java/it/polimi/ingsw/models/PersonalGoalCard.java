package it.polimi.ingsw.models;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Represents the personal goal card of a Player
 */
public class PersonalGoalCard implements GoalCard {
    private List<Pair<Category, Coordinates>> positions;

    public int checkGoal(Player player) {
        throw new NotImplementedException();
    }

    public List<Pair<Category, Coordinates>> getPositions() {
        return positions;
    }
}

