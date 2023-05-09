package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.LivingRoom;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

@Nullable
public record GameUpdate(LivingRoom livingRoom, List<PlayerUpdate> players, List<CommonGoalCardUpdate> commonGoalCards, PlayerUpdate gameEnder, PlayerUpdate currentPlayer) implements Serializable {
    public static GameUpdate filterPersonalGoalCards(GameUpdate gameUpdate, String nickname) {
        return new GameUpdate(
            gameUpdate.livingRoom,
            gameUpdate.players == null ?
                    null : gameUpdate.players.stream().map(p -> p.nickname().equals(nickname) ?
                        p : new PlayerUpdate(p.nickname(), p.bookshelf(), null)).toList(),
            gameUpdate.commonGoalCards,
            gameUpdate.gameEnder,
            gameUpdate.currentPlayer
        );
    }
}
