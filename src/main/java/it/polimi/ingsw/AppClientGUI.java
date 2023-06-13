package it.polimi.ingsw;

import it.polimi.ingsw.view.GUI.GUI;

import java.util.Scanner;

public class AppClientGUI {
    public static void main(String[] args) {
        String connectionType = args[0].toLowerCase();
        if(!connectionType.equals("rmi") && !connectionType.equals(("socket"))){
            System.out.println("Invalid argument.\nValid values are \n - socket \n - rmi");
            System.exit(-1);
        }

        String[] connectionArgs = {connectionType};

        GUI.main(connectionArgs);
    }
}
