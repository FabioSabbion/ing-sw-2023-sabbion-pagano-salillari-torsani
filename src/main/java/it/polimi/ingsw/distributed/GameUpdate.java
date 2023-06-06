package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.LivingRoom;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Nullable
public record GameUpdate(LivingRoom livingRoom, List<PlayerUpdate> players, List<CommonGoalCardUpdate> commonGoalCards, PlayerUpdate gameEnder, PlayerUpdate currentPlayer, int ID) implements Serializable {
    public static GameUpdate filterPersonalGoalCards(GameUpdate gameUpdate, String nickname) {
        return new GameUpdate(
            gameUpdate.livingRoom,
            gameUpdate.players == null ?
                    null : gameUpdate.players.stream().map(p -> p.nickname().equals(nickname) ?
                        p : new PlayerUpdate(p.nickname(), p.bookshelf(), null)).toList(),
            gameUpdate.commonGoalCards,
            gameUpdate.gameEnder,
            gameUpdate.currentPlayer,
            gameUpdate.ID
        );
    }

    public GameUpdate(LivingRoom livingRoom, List<PlayerUpdate> players, List<CommonGoalCardUpdate> commonGoalCards, PlayerUpdate gameEnder, PlayerUpdate currentPlayer) {
        this(livingRoom, players, commonGoalCards, gameEnder, currentPlayer, id++);
    }

    public GameUpdate copyWith(LivingRoom livingRoom, List<PlayerUpdate> players, List<CommonGoalCardUpdate> commonGoalCards, PlayerUpdate gameEnder, PlayerUpdate currentPlayer) {
        List<PlayerUpdate> newPlayerList = new ArrayList<>();
        for (int i = 0; i < this.players.size(); i++) {
            int finalI = i;
            if (players != null) {
                Optional<PlayerUpdate> playerUpdateOptional = players.stream().filter(p -> p.nickname().equals(this.players.get(finalI).nickname())).findFirst();
                if (playerUpdateOptional.isPresent()) {
                    newPlayerList.add(playerUpdateOptional.get());
                } else {
                    newPlayerList.add(this.players.get(i));
                }
            } else {
                newPlayerList.add(this.players.get(i));
            }

        }

        return new GameUpdate(
            livingRoom != null ? livingRoom : this.livingRoom,
            newPlayerList,
            commonGoalCards != null ? commonGoalCards : this.commonGoalCards,
            gameEnder != null ? gameEnder : this.gameEnder,
            currentPlayer != null ? currentPlayer : this.currentPlayer
        );
    }

    private static int id = 0;
}
