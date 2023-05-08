package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.CommonGoalCard;

import java.io.Serializable;
import java.util.List;

public record CommonGoalCardUpdate(int commonGoalCardID, List<PlayerUpdate> playerUpdateList) implements Serializable {
    public static CommonGoalCardUpdate from(CommonGoalCard card) {
        return new CommonGoalCardUpdate(card.cardID,
                card.orderOfCompletionList.stream().map((player -> PlayerUpdate.from(player, false))).toList());
    }
}
