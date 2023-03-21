package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {
    private Game game;
    private int[] personalGoalCardScores = new int[]{0,1,2,4,6,9,12};

    @Test
    void getScoreBoard() {
        Map<Player, Integer> scoreboard = game.getScoreBoard();
        for (Player p: game.getPlayers()) {
            int pScore = 0;
            if (p == game.getGameEnder())
                pScore += 1;
            for (CommonGoalCard cgc: game.getCommonGoalCards())
                pScore += cgc.checkGoal(p);
            List<Pair<Category, Integer>> closeTiles = p.getBookshelf().getCloseTiles();
            for (int n: closeTiles.stream().mapToInt(Pair::getRight).toArray()) {
                if (n == 3)
                    pScore += 2;
                if (n == 4)
                    pScore += 3;
                if (n == 5)
                    pScore += 5;
                if (n >= 6)
                    pScore += 8;
            }
            int count = 0;
            for (Pair<Category, Coordinates> pair: p.getPersonalGoalCard().getPositions()) {
                if (p.getBookshelf().getBookshelf()[pair.getRight().x][pair.getRight().y].getCategory() == pair.getLeft())
                    count++;
            }
            pScore += personalGoalCardScores[count];
            assertEquals(pScore, scoreboard.get(p));
        }
    }

    @Test
    void nextPlayer() {
        assertEquals(game.getPlayers()[0], game.getCurrentPlayer());
        for (int i = 0; i < 3; i++) {
            game.nextPlayer();
            assertEquals(game.getPlayers()[i + 1], game.getCurrentPlayer());
        }
        game.nextPlayer();
        assertEquals(game.getPlayers()[0], game.getCurrentPlayer());
    }

    // The game is ended if [gameEnder] is set and the round is complete
    @Test
    void isEnded() {
        assert((game.isEnded() && game.getGameEnder() != null && game.getCurrentPlayer() == game.getPlayers()[0]) ||
                (!game.isEnded() && game.getGameEnder() == null));
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

        // Add tiles to players' bookshelf
        try {
            player1.getBookshelf().insertTiles(0, new ArrayList<Tile>(Arrays.asList(
                    new Tile(Category.GAMES, Icon.VARIATION1, Orientation.UP),
                    new Tile(Category.GAMES, Icon.VARIATION1, Orientation.UP),
                    new Tile(Category.GAMES, Icon.VARIATION1, Orientation.UP)
            )));
        } catch (NotEnoughCellsException | PickTilesException e) {
            throw new RuntimeException(e);
        }

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
        game.setGameEnder(player1);
    }
}