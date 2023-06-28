package it.polimi.ingsw;

import it.polimi.ingsw.utils.ArgumentChecker;
import it.polimi.ingsw.view.GUI.GUI;

/**
 * Entry point class for the Client GUI
 */
public class AppClientGUI {
    public static void main(String[] args) {
        ArgumentChecker.checkArguments(args);

        GUI.main(args);
    }


}

