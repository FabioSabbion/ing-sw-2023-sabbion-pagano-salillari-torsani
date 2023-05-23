package it.polimi.ingsw.models;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

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
                    new MutablePair<>(Category.GAMES, new Coordinates(0, 0)),
                    new MutablePair<>(Category.BOOKS, new Coordinates(1, 1)),
                    new MutablePair<>(Category.PLANTS, new Coordinates(2, 2))
                )
            )
        );
        PersonalGoalCard personalGoalCard2 = new PersonalGoalCard(new ArrayList<>(Arrays.asList(
                    new MutablePair<>(Category.GAMES, new Coordinates(0, 1)),
                    new MutablePair<>(Category.BOOKS, new Coordinates(1, 2)),
                    new MutablePair<>(Category.PLANTS, new Coordinates(2, 4))
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
        CommonGoalCard commonGoalCard1 = new CommonGoalCard((b) -> true, players.length, 0);
        CommonGoalCard commonGoalCard2 = new CommonGoalCard((b) -> false, players.length, 0);

        CommonGoalCard[] commonGoalCards = new CommonGoalCard[]{commonGoalCard1, commonGoalCard2};

        // Create living room
        LivingRoom livingRoom = new LivingRoom();

        // Create game
        this.game = new Game(players, commonGoalCards, new ArrayList<>(), livingRoom, players[0]);

    }
}