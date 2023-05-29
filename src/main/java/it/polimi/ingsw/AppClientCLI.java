package it.polimi.ingsw;

import it.polimi.ingsw.view.CLI.CLIController;
import it.polimi.ingsw.view.ViewController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class AppClientCLI {
    public static void main(String[] args) throws NotBoundException, RemoteException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select your connection type (rmi - socket)...");
        String connectionType = scanner.nextLine().toLowerCase();
        while(!connectionType.equals("rmi") && !connectionType.equals(("socket"))){
            System.out.println("You must enter a valid choice! Try Again");
            connectionType = scanner.nextLine().toLowerCase();
        }


        ViewController viewController = new CLIController();

        switch (connectionType){
            case "rmi" -> AppClientRMI.start(viewController);
            case "socket" -> AppClientSocket.start(viewController);
        }
    }
}
