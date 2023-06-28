package it.polimi.ingsw;

import it.polimi.ingsw.utils.ArgumentChecker;
import it.polimi.ingsw.view.CLI.CLIController;
import it.polimi.ingsw.view.ViewController;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


/**
 * Entry point class for the Client CLI
 */
public class AppClientCLI {
    public static void main(String[] args) throws NotBoundException, RemoteException {
        ArgumentChecker.checkArguments(args);

        ViewController viewController = new CLIController();
        try {
            switch (args[0]){
                case "rmi" -> AppClientRMI.start(viewController, args[1]);
                case "socket" -> AppClientSocket.start(viewController, args[1]);
            }
        } catch (ConnectException e){
            System.err.println("Server unreachable! Try again");
        }

    }
}
