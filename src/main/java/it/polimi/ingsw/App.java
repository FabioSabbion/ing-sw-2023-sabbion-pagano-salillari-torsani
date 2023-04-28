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

            var contr = new CLIController();
            contr.start();
    }
}
