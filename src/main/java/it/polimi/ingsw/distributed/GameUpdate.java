package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.LivingRoom;
import it.polimi.ingsw.models.Player;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

@Nullable
public record GameUpdate(LivingRoomUpdate livingRoomUpdate, List<PlayerUpdate> players, List<CommonGoalCard> commonGoalCards, PlayerUpdate gameEnder, PlayerUpdate currentPlayer) implements Serializable {
}
