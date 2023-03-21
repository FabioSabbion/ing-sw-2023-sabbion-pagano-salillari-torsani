package it.polimi.ingsw.models;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;
    @Test
    @DisplayName("Check current player is changed correctly")
    void nextPlayer() {
        assertEquals(game.getPlayers()[0], game.getCurrentPlayer());
        game.nextPlayer();
        assertEquals(game.getPlayers()[1], game.getCurrentPlayer());
        game.nextPlayer();
        game.nextPlayer();
        game.nextPlayer();
        assertEquals(game.getPlayers()[0], game.getCurrentPlayer());
    }

    @Test
    @DisplayName("Game should be ended if [gameEnder] is set and the round is complete")
    void isEnded() {
        assertFalse(game.isEnded());
        game.setGameEnder(game.getPlayers()[0]);
        assertTrue(game.isEnded());
    }

    @BeforeEach
    void setup() {
        // Create personal goal cards
        PersonalGoalCard personalGoalCard1 = new PersonalGoalCard(new ArrayList<>(Arrays.asList(
                    new ImmutablePair<>(Category.GAMES, new Coordinates(0, 0)),
                    new ImmutablePair<>(Category.BOOKS, new Coordinates(1, 1)),
                    new ImmutablePair<>(Category.PLANTS, new Coordinates(2, 2))
                )
            )
        );
        PersonalGoalCard personalGoalCard2 = new PersonalGoalCard(new ArrayList<>(Arrays.asList(
                    new ImmutablePair<>(Category.GAMES, new Coordinates(0, 1)),
                    new ImmutablePair<>(Category.BOOKS, new Coordinates(1, 2)),
                    new ImmutablePair<>(Category.PLANTS, new Coordinates(2, 4))
                )
            )
        );

        // Create players
        Player player1 = new Player("fabio", personalGoalCard1);
        Player player2 = new Player("lp", personalGoalCard2);
        Player player3 = new Player("lorenzo", personalGoalCard1);
        Player player4 = new Player("andri", personalGoalCard2);
        Player[] players = new Player[]{player1, player2, player3, player4};

        // Create common goal cards
        CommonGoalCard commonGoalCard1 = new CommonGoalCard((b) -> true, players.length);
        CommonGoalCard commonGoalCard2 = new CommonGoalCard((b) -> false, players.length);

        CommonGoalCard[] commonGoalCards = new CommonGoalCard[]{commonGoalCard1, commonGoalCard2};

        // Create tiles
        List<Tile> tiles = new ArrayList<>();
        for (Category c: Category.values()) {
            for (int i = 0; i < 22; i++) {
                tiles.add(new Tile(c, Icon.VARIATION1, Orientation.UP));
            }
        }
        Collections.shuffle(tiles);

        // Create living room
        LivingRoom livingRoom = new LivingRoom();

        // Create game
        this.game = new Game(players, commonGoalCards, tiles, livingRoom);

    }
}