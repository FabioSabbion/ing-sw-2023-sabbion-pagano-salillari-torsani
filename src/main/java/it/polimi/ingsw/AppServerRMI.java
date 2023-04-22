package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.Connection;
import it.polimi.ingsw.distributed.networking.ConnectionRMIServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AppServerRMI {
    public static void main(String[] args) throws RemoteException {
        Connection server = new ConnectionRMIServer();

        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("server", server);
    }
}
