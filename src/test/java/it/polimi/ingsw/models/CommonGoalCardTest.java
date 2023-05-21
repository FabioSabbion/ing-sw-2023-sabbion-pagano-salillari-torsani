package it.polimi.ingsw.models;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CommonGoalCardTest {
    private PersonalGoalCard forPlayers;

    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private Player playerFailing;

    @Test
    @DisplayName("Checking what happens when it's impossible to win")
    public void checkGoalFailed() {
        for (int i = 2; i <= 4; i++) {
            CommonGoalCard testedGoal = new CommonGoalCard((bookshelf) -> {return false;}, i, 0);

            Bookshelf tempBookShelf = new Bookshelf();

            assertEquals(0, testedGoal.checkGoal(new Player("", forPlayers)));

        }
    }

    @Test
    @DisplayName("Checking if with two players you get the correct result")
    public void checkGoalsScore2Players() {
        CommonGoalCard testedGoal = new CommonGoalCard((bookshelf) -> {
            Player[] winningPlayers = {
                    player1,
                    player2,
            };

            return Arrays.stream(winningPlayers).anyMatch(player -> {
                return player.getBookshelf().equals(bookshelf);
            });
        }, 2, 0);


        assertEquals(8, testedGoal.checkGoal(player1));
        assertEquals(0, testedGoal.checkGoal(playerFailing));
        assertEquals(4, testedGoal.checkGoal(player2));

        // we have to get the same result if a player has already won previously the goal
        assertEquals(8, testedGoal.checkGoal(player1));
        assertEquals(4, testedGoal.checkGoal(player2));
    }
    @Test
    @DisplayName("Checking if with three players you get the correct result")
    public void checkGoalsScore3Players() {
        CommonGoalCard testedGoal = new CommonGoalCard((bookshelf) -> {
            Player[] winningPlayers = {
                    player1,
                    player2,
                    player3
            };

            return Arrays.stream(winningPlayers).anyMatch(player -> {
                return player.getBookshelf().equals(bookshelf);
            });
        }, 3, 0);


        assertEquals(8, testedGoal.checkGoal(player1));
        assertEquals(0, testedGoal.checkGoal(playerFailing));
        assertEquals(6, testedGoal.checkGoal(player2));
        assertEquals(4, testedGoal.checkGoal(player3));

        // we have to get the same result if a player has already won previously the goal

        assertEquals(8, testedGoal.checkGoal(player1));
        assertEquals(6, testedGoal.checkGoal(player2));
        assertEquals(4, testedGoal.checkGoal(player3));
    }

    @Test
    @DisplayName("Checking if with four players you get the correct result")
    public void checkGoalsScore4Players() {
        CommonGoalCard testedGoal = new CommonGoalCard((bookshelf) -> {
            Player[] winningPlayers = {
                    player1,
                    player2,
                    player3,
                    player4
            };

            return Arrays.stream(winningPlayers).anyMatch(player -> {
                return player.getBookshelf().equals(bookshelf);
            });
        }, 4, 0);


        assertEquals(8, testedGoal.checkGoal(player1));
        assertEquals(0, testedGoal.checkGoal(playerFailing));
        assertEquals(6, testedGoal.checkGoal(player2));
        assertEquals(4, testedGoal.checkGoal(player3));
        assertEquals(2, testedGoal.checkGoal(player4));

        // we have to get the same result if a player has already won previously the goal

        assertEquals(8, testedGoal.checkGoal(player1));
        assertEquals(6, testedGoal.checkGoal(player2));
        assertEquals(4, testedGoal.checkGoal(player3));
        assertEquals(2, testedGoal.checkGoal(player4));
    }

    @BeforeEach
    public void setup() {
        this.forPlayers = new PersonalGoalCard(
                Collections.singletonList(
                        new MutablePair<Category, Coordinates>(Category.CATS, new Coordinates(0, 0))
                )
        );
        this.player1 = new Player("andri", forPlayers);
        this.player2 = new Player("fabio", forPlayers);
        this.player3 = new Player("lp", forPlayers);
        this.player4 = new Player("lorenzo", forPlayers);
        this.playerFailing = new Player("fail", forPlayers);
    }
}
