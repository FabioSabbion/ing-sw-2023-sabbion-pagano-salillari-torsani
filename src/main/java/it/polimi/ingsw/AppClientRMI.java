package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.Connection;
import it.polimi.ingsw.distributed.networking.ConnectionRMIClient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AppClientRMI {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();
        Connection server = (Connection) registry.lookup("server");

        ConnectionRMIClient client = new ConnectionRMIClient(server);
        client.setNickname("Andri 3");


    }
}
