package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.Bookshelf;
import it.polimi.ingsw.models.PersonalGoalCard;
import it.polimi.ingsw.models.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Nullable
public record PlayerUpdate(@Nonnull String nickname, Bookshelf bookshelf, PersonalGoalCard personalGoalCard) {
    public static PlayerUpdate from(Player player, boolean includePersonalGoalCard) {
        return new PlayerUpdate(player.getNickname(), player.getBookshelf(), includePersonalGoalCard ? player.getPersonalGoalCard() : null);
    }
}