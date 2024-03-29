package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.Bookshelf;
import it.polimi.ingsw.models.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
/**
 * The PlayerUpdate class represents an update of a player that can be transmitted over the network.
 * It contains the player's nickname, bookshelf, and optional personal goal card update.
 */
@Nullable
public record PlayerUpdate(@Nonnull String nickname, Bookshelf bookshelf, PersonalGoalCardUpdate personalGoalCard) implements Serializable {
    public static PlayerUpdate from(Player player, boolean includePersonalGoalCard) {
        return new PlayerUpdate(player.getNickname(), player.getBookshelf(), includePersonalGoalCard ? PersonalGoalCardUpdate.from(player.getPersonalGoalCard(), player) : null);
    }

    public static Player to(PlayerUpdate playerUpdate){
        return new Player(playerUpdate.nickname, PersonalGoalCardUpdate.to(playerUpdate.personalGoalCard), playerUpdate.bookshelf);
    }
}
