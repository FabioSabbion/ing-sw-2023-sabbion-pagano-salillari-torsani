package it.polimi.ingsw.distributed;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

@Nullable
public record GameUpdate(LivingRoomUpdate livingRoomUpdate, List<PlayerUpdate> players, List<CommonGoalCardUpdate> commonGoalCards, PlayerUpdate gameEnder, PlayerUpdate currentPlayer) implements Serializable {
    public static GameUpdate filterPersonalGoalCards(GameUpdate gameUpdate, String nickname) {
        return new GameUpdate(
            gameUpdate.livingRoomUpdate,
            gameUpdate.players.stream().map(p -> p.nickname().equals(nickname) ? p : new PlayerUpdate(p.nickname(), p.bookshelf(), null)).toList(),
            gameUpdate.commonGoalCards,
            gameUpdate.gameEnder,
            gameUpdate.currentPlayer
        );
    }
}
