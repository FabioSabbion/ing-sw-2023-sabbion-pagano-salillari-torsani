package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.LivingRoom;
import it.polimi.ingsw.models.Player;

import javax.annotation.Nullable;
import java.util.List;

@Nullable
public record GameUpdate(LivingRoom livingRoom, List<Player> players, List<CommonGoalCard> commonGoalCards, Player gameEnder, Player currentPlayer) {

}
