package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.Category;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.PersonalGoalCard;
import it.polimi.ingsw.models.Player;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Serializable;
import java.util.List;

/**
 * The PersonalGoalCardUpdate class represents an update of a personal goal card that can be transmitted over the network.
 * It contains the positions of the categories, the points, and the card ID.
 */
public record PersonalGoalCardUpdate(List<MutablePair<Category, Coordinates>> positions, int point, String ID) implements Serializable {
    public static PersonalGoalCardUpdate from(PersonalGoalCard personalGoalCard, Player player) {
        return new PersonalGoalCardUpdate(personalGoalCard.getPositions(), personalGoalCard.checkGoal(player), personalGoalCard.cardID);
    }

    public static PersonalGoalCard to(PersonalGoalCardUpdate personalGoalCardUpdate){
        return new PersonalGoalCard(personalGoalCardUpdate.positions, personalGoalCardUpdate.ID);
    }
}
