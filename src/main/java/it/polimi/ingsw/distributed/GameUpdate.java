package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.LivingRoom;
import it.polimi.ingsw.view.GUI.GuiParts;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The GameUpdate class represents an update of the game state that can be transmitted over the network.
 * It contains information about the living room, players, common goal cards, game ender, current player, and an ID.
 */
@Nullable
public record GameUpdate(LivingRoom livingRoom, List<PlayerUpdate> players, List<CommonGoalCardUpdate> commonGoalCards, PlayerUpdate gameEnder, PlayerUpdate currentPlayer, int ID) implements Serializable {
    /**
     * Filters the personal goal cards from the GameUpdate based on the specified nickname.
     * @param gameUpdate gameUpdate The original GameUpdate object.
     * @param nickname The nickname of the player to filter the personal goal cards for.
     * @return A new GameUpdate object with the filtered personal goal cards.
     */
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

    /**
     Creates a copy of the GameUpdate object with the specified parameters.
     @param myNickname The nickname of the player making the copy.
     @param toRefresh The list of GUI parts to refresh.
     @param livingRoom The new living room state.
     @param players The new list of player updates.
     @param commonGoalCards The new list of common goal card updates.
     @param gameEnder The new player update for the game ender.
     @param currentPlayer The new player update for the current player.
     @return A new GameUpdate object with the specified parameters.
     */
    public GameUpdate copyWith(String myNickname, List<GuiParts> toRefresh, LivingRoom livingRoom, List<PlayerUpdate> players, List<CommonGoalCardUpdate> commonGoalCards, PlayerUpdate gameEnder, PlayerUpdate currentPlayer) {
        List<PlayerUpdate> newPlayerList = new ArrayList<>();
        for (int i = 0; i < this.players.size(); i++) {
            int finalI = i;
            if (players != null) {
                Optional<PlayerUpdate> playerUpdateOptional = players.stream().filter(p -> p.nickname().equals(this.players.get(finalI).nickname())).findFirst();
                if (playerUpdateOptional.isPresent()) {
                    PlayerUpdate playerUpdate = playerUpdateOptional.get();
                    if (playerUpdate.nickname().equals(myNickname)) {
                        toRefresh.add(GuiParts.BOOKSHELF);
                    }
                    newPlayerList.add(playerUpdate);
                } else {
                    newPlayerList.add(this.players.get(i));
                }
            } else {
                newPlayerList.add(this.players.get(i));
            }

        }
        List<CommonGoalCardUpdate> newCommonGoalCards = new ArrayList<>();

        if (commonGoalCards != null) {
            for (CommonGoalCardUpdate commonGoalCard : this.commonGoalCards) {
                Optional<CommonGoalCardUpdate> match = commonGoalCards.stream().filter(c -> c.commonGoalCardID() == commonGoalCard.commonGoalCardID()).findFirst();
                if (match.isPresent()) {
                    newCommonGoalCards.add(match.get());
                } else {
                    newCommonGoalCards.add(commonGoalCard);
                }
            }
        }

        if (livingRoom != null) {
            toRefresh.add(GuiParts.LIVING_ROOM);
        }

        return new GameUpdate(
            livingRoom != null ? livingRoom : this.livingRoom,
            newPlayerList,
            newCommonGoalCards.isEmpty() ? this.commonGoalCards : newCommonGoalCards,
            gameEnder != null ? gameEnder : this.gameEnder,
            currentPlayer != null ? currentPlayer : this.currentPlayer
        );
    }

    private static int id = 0;
}
