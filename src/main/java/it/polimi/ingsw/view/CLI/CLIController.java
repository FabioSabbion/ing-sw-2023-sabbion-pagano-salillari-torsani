package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.models.*;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CLIController {
    private CLI cli;


    public void start() throws PickTilesException, NotEnoughCellsException {


        var livingRoom = new LivingRoom();
        List<Tile> remainingTiles = new ArrayList<>();
        for (Category c : Category.values()) {
            for (int i = 0; i < 22; i++) {
                remainingTiles.add(new Tile(c, Icon.VARIATION1, Orientation.UP));
            }
        }
        Collections.shuffle(remainingTiles);

        livingRoom.fillBoardIfNeeded(4, remainingTiles);

        List<PersonalGoalCard> personalGoalCardList = PersonalGoalCard.buildFromJson();

        Player Andri = new Player("Andri", personalGoalCardList.get(0));
        Player Fabio = new Player("Fabio", personalGoalCardList.get(1));
        Player Lp = new Player("Lp", personalGoalCardList.get(2));
        Player Lore = new Player("Lore", personalGoalCardList.get(3));

        List<Player> players = new ArrayList<>();
        players.add(Andri);
        players.add(Fabio);
        players.add(Lp);
        players.add(Lore);

        HashMap<Player, Integer> playerPoints = new HashMap<>();
        playerPoints.put(Andri, 69);
        playerPoints.put(Fabio, 42);
        playerPoints.put(Lp, 666);
        playerPoints.put(Lore, 420);

        int[] commonGoalCardsValues = {0, 1};


        CLI cli = new CLI(players, livingRoom, Lp, commonGoalCardsValues);

        boolean choice = cli.initialScreen();

        if(!choice) return;

        cli.menuChoice(playerPoints, Lp);

        List<Coordinates> c = new ArrayList<>();
        c.add(cli.getPlayerTileCoordinate());
        int col = cli.getPlayerColumn();

        List<Tile> removenTile = livingRoom.chooseTiles(c);
        livingRoom.removeTiles(c);
        Lp.getBookshelf().insertTiles(col, removenTile);
        cli.updateBookshelf(Lp);
        cli.setLivingRoom(livingRoom);
        cli.showMain(Lp);

    }
}
