package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.CommonGoalCard;

import java.io.Serializable;
import java.util.List;


/**
 * The CommonGoalCardUpdate class represents an update of a common goal card that can be transmitted over the network.
 * It contains the ID of the common goal card and a list of the players that completed the specific card.
 */
public record CommonGoalCardUpdate(int commonGoalCardID, List<String> playerUpdateList) implements Serializable {
    public static CommonGoalCardUpdate from(CommonGoalCard card) {
        return CommonGoalCard.from(card);
    }
}
