package it.polimi.ingsw.models;

import it.polimi.ingsw.view.CLI.CLIRenderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonGoalCardFactoryTest {

    @Test
    @DisplayName("Checking commonGoalCard 0")
    void getCommonGoalCard0() {
        int numPlayer = 3;

        var commonGoalCard = CommonGoalCardFactory.buildFromJson(numPlayer, 0);

        System.out.println("Card:");
        System.out.println(CommonGoalCardFactory.getASCIIForCard(0));

        var player = new Player("andri", PersonalGoalCard.buildFromJson().get(0));

        for (int i = 0; i < 5; i++) {
            player.getBookshelf().getBookshelf()[0][i] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
            player.getBookshelf().getBookshelf()[1][i] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        }

        player.getBookshelf().getBookshelf()[2][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[3][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);


        assertTrue(commonGoalCard.checkGoal(player) > 0);
    }

    @Test
    @DisplayName("Checking commonGoalCard 0 (False case)")
    void getCommonGoalCard0_v2() {
        int numPlayer = 3;

        var commonGoalCard = CommonGoalCardFactory.buildFromJson(numPlayer, 0);

        System.out.println("Card:");
        System.out.println(CommonGoalCardFactory.getASCIIForCard(0));

        var player = new Player("andri", PersonalGoalCard.buildFromJson().get(0));

        for (int i = 0; i < 5; i++) {
            player.getBookshelf().getBookshelf()[0][i] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
            player.getBookshelf().getBookshelf()[1][i] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        }

        player.getBookshelf().getBookshelf()[2][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[3][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);

        System.out.println(CLIRenderer.renderBookshelf(player.getBookshelf()));

        assertFalse(commonGoalCard.checkGoal(player) > 0);
    }

    @Test
    @DisplayName("Checking commonGoalCard 1")
    void getCommonGoalCard1() {
        int numPlayer = 3;

        var commonGoalCard = CommonGoalCardFactory.buildFromJson(numPlayer, 1);

        System.out.println("Card:");
        System.out.println(CommonGoalCardFactory.getASCIIForCard(1));

        var player = new Player("andri", PersonalGoalCard.buildFromJson().get(0));

        player.getBookshelf().getBookshelf()[0][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[0][4] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[5][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[5][4] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);


        System.out.println(CLIRenderer.renderBookshelf(player.getBookshelf()));

        assertTrue(commonGoalCard.checkGoal(player) > 0);
    }

    @Test
    @DisplayName("Checking commonGoalCard 2")
    void getCommonGoalCard2() {
        int numPlayer = 3;

        var commonGoalCard = CommonGoalCardFactory.buildFromJson(numPlayer, 2);

        System.out.println("Card:");
        System.out.println(CommonGoalCardFactory.getASCIIForCard(2));

        var player = new Player("andri", PersonalGoalCard.buildFromJson().get(0));

        player.getBookshelf().getBookshelf()[0][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[1][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[2][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[3][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);

        player.getBookshelf().getBookshelf()[0][1] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[1][1] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[2][1] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[3][1] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);

        player.getBookshelf().getBookshelf()[0][2] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[1][2] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[2][2] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[3][2] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);

        player.getBookshelf().getBookshelf()[0][3] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[1][3] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[2][3] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[3][3] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);


        System.out.println(CLIRenderer.renderBookshelf(player.getBookshelf()));
    }


    @Test
    @DisplayName("Checking commonGoalCard 3")
    void getCommonGoalCard3() {
        int numPlayer = 3;

        var commonGoalCard = CommonGoalCardFactory.buildFromJson(numPlayer, 3);

        System.out.println("Card:");
        System.out.println(CommonGoalCardFactory.getASCIIForCard(3));

        var player = new Player("andri", PersonalGoalCard.buildFromJson().get(0));

        player.getBookshelf().getBookshelf()[0][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[1][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[0][1] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[1][1] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);

        player.getBookshelf().getBookshelf()[0][2] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[1][2] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[0][3] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[1][3] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);

        System.out.println(CLIRenderer.renderBookshelf(player.getBookshelf()));

        assertTrue(commonGoalCard.checkGoal(player) > 0);

    }


    @Test
    @DisplayName("Checking commonGoalCard 4")
    void getCommonGoalCard4() {
        int numPlayer = 3;

        var commonGoalCard = CommonGoalCardFactory.buildFromJson(numPlayer, 4);

        System.out.println("Card:");
        System.out.println(CommonGoalCardFactory.getASCIIForCard(4));

        var player = new Player("andri", PersonalGoalCard.buildFromJson().get(0));

        for (int i = 0; i < 6; i++) {
            player.getBookshelf().getBookshelf()[i][0] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        }

        for (int i = 0; i < 6; i++) {
            player.getBookshelf().getBookshelf()[i][1] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        }

        for (int i = 0; i < 6; i++) {
            player.getBookshelf().getBookshelf()[i][3] = new Tile(Category.TROPHIES, Icon.VARIATION1, Orientation.UP);
        }

        player.getBookshelf().getBookshelf()[0][3] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[1][3] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[2][3] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        player.getBookshelf().getBookshelf()[3][3] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);



        System.out.println(CLIRenderer.renderBookshelf(player.getBookshelf()));

        assertTrue(commonGoalCard.checkGoal(player) > 0);

    }

    @Test
    @DisplayName("Checking commonGoalCard 5")
    void getCommonGoalCard05() {
        int numPlayer = 3;

        var cgc = CommonGoalCardFactory.buildFromJson(numPlayer, 5);

        var player = new Player("andri", PersonalGoalCard.buildFromJson().get(0));

        System.out.println("Card:");
        System.out.println(CommonGoalCardFactory.getASCIIForCard(5));

        for (int i = 0; i < Bookshelf.ROWS; i++) {
            for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                player.getBookshelf().getBookshelf()[i][j] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
            }
        }

        assertTrue(cgc.checkGoal(player) > 0 );
    }

    @Test
    @DisplayName("Checking commonGoalCard 5 wrong case")
    void getCommonGoalCard05_v2() {
        int numPlayer = 3;

        var cgc = CommonGoalCardFactory.buildFromJson(numPlayer, 5);

        var player = new Player("andri", PersonalGoalCard.buildFromJson().get(0));

        System.out.println("Card:");
        System.out.println(CommonGoalCardFactory.getASCIIForCard(5));

        assertEquals(0, cgc.checkGoal(player));
    }

    @Test
    @DisplayName("Checking commonGoalCard 12")
    void getCommonGoalCard12() {
        int numPlayer = 3;

        var commonGoalCard = CommonGoalCardFactory.buildFromJson(numPlayer, 11);

        System.out.println("Card:");
        System.out.println(CommonGoalCardFactory.getASCIIForCard(11));

        var player = new Player("andri", PersonalGoalCard.buildFromJson().get(0));

        for (int i = 0; i < 5; i++) {
            player.getBookshelf().getBookshelf()[i][0] = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        }

        for (int i = 0; i < 4; i++) {
            player.getBookshelf().getBookshelf()[i][1] = new Tile(Category.TROPHIES, Icon.VARIATION1, Orientation.UP);
        }

        for (int i = 0; i < 3; i++) {
            player.getBookshelf().getBookshelf()[i][2] = new Tile(Category.FRAMES, Icon.VARIATION1, Orientation.UP);
        }

        for (int i = 0; i < 2; i++) {
            player.getBookshelf().getBookshelf()[i][3] = new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.UP);
        }

        for (int i = 0; i < 1; i++) {
            player.getBookshelf().getBookshelf()[i][4] = new Tile(Category.PLANTS, Icon.VARIATION1, Orientation.UP);
        }



        System.out.println(CLIRenderer.renderBookshelf(player.getBookshelf()));

        assertTrue(commonGoalCard.checkGoal(player) > 0);

    }
}