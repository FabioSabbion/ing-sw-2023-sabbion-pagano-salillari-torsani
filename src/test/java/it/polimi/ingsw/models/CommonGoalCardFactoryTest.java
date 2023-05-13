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

        var commonGoalCard= CommonGoalCardFactory.buildFromJson(numPlayer, 0);

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
    @DisplayName("Checking commonGoalCard 1")
    void getCommonGoalCard1() {
        int numPlayer = 3;

        var commonGoalCard= CommonGoalCardFactory.buildFromJson(numPlayer, 1);

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
}