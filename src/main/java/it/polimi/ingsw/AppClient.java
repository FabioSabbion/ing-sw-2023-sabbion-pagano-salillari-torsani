package it.polimi.ingsw;

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

        switch (connectionType){
            case "rmi" -> AppClientRMI.start();
            case "socket" -> AppClientSocket.start();
        }
    }
}
