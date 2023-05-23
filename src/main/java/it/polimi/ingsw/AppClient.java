package it.polimi.ingsw;

import it.polimi.ingsw.view.CLI.CLIController;
import it.polimi.ingsw.view.GUI.GUIController;
import it.polimi.ingsw.view.ViewController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class AppClient {
    public static void main(String[] args) throws NotBoundException, RemoteException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select your connection type (rmi - socket)...");
        String connectionType = scanner.nextLine().toLowerCase();
        while(!connectionType.equals("rmi") && !connectionType.equals(("socket"))){
            System.out.println("You must enter a valid choice! Try Again");
            connectionType = scanner.nextLine().toLowerCase();
        }

        System.out.println("Select your UI type (cli - gui)...");
        String selectedUI = scanner.nextLine().toLowerCase();
        while(!selectedUI.equals("cli") && !selectedUI.equals(("gui"))){
            System.out.println("You must enter a valid choice! Try Again");
            selectedUI = scanner.nextLine().toLowerCase();
        }

        ViewController viewController;
        if (selectedUI.equals("cli")) {
            viewController = new CLIController();
        } else {
            viewController = new GUIController();
        }

        switch (connectionType){
            case "rmi" -> AppClientRMI.start(viewController);
            case "socket" -> AppClientSocket.start(viewController);
        }
    }
}
