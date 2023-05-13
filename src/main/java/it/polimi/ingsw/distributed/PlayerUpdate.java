package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.Bookshelf;
import it.polimi.ingsw.models.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

@Nullable
public record PlayerUpdate(@Nonnull String nickname, Bookshelf bookshelf, PersonalGoalCardUpdate personalGoalCard) implements Serializable {
    public static PlayerUpdate from(Player player, boolean includePersonalGoalCard) {
        return new PlayerUpdate(player.getNickname(), player.getBookshelf(), includePersonalGoalCard ? PersonalGoalCardUpdate.from(player.getPersonalGoalCard(), player) : null);
    }
}
