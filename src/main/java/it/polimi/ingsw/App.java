package it.polimi.ingsw;

import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import it.polimi.ingsw.view.CLI.CLIController;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws PickTilesException, NotEnoughCellsException {

        controller.game.addObserver(new Observer<GameUpdate, ViewEvent>() {
            @Override
            public void update(GameUpdate value, ViewEvent eventType) {
                System.out.println("NEW UPDATE");
                System.out.println(value);
            }
        });

        controller.update(Arrays.asList(new Coordinates(1, 4), new Coordinates(1, 5)), 3);
    }
}
