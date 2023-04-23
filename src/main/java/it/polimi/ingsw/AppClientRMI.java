package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ClientRMI;
import it.polimi.ingsw.distributed.networking.Server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class AppClientRMI {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();
        Server server = (Server) registry.lookup("server");

        ClientRMI client = new ClientRMI(server);
        client.setNickname("Andri 3");

        Scanner in = new Scanner(System.in);

        in.nextLine();
        client.setNickname("Andri 3pr");
    }
}
