package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ClientImpl;
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

        ClientImpl client = new ClientImpl(server);
        Scanner sc = new Scanner(System.in);

        String s = sc.nextLine();
        server.setNickname(s, client);

        server.setNumPlayers(sc.nextInt(), client);

        // TODO: run view with client.run
    }
}
