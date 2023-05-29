package it.polimi.ingsw;

import it.polimi.ingsw.view.GUI.GUI;

import java.util.Scanner;

public class AppClientGUI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select your connection type (rmi - socket)...");
        String connectionType = scanner.nextLine().toLowerCase();
        while(!connectionType.equals("rmi") && !connectionType.equals(("socket"))){
            System.out.println("You must enter a valid choice! Try Again");
            connectionType = scanner.nextLine().toLowerCase();
        }

        String[] connectionArgs = {connectionType};

        GUI.main(connectionArgs);
    }
}
