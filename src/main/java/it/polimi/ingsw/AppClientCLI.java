package it.polimi.ingsw;

import it.polimi.ingsw.utils.ArgumentChecker;
import it.polimi.ingsw.view.CLI.CLIController;
import it.polimi.ingsw.view.ViewController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class AppClientCLI {
    public static void main(String[] args) throws NotBoundException, RemoteException {
        ArgumentChecker.checkArguments(args);

        ViewController viewController = new CLIController();

        switch (args[0]){
            case "rmi" -> AppClientRMI.start(viewController, args[1]);
            case "socket" -> AppClientSocket.start(viewController, args[1]);
        }
    }
}
