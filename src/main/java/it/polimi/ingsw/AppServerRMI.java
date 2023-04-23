package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ServerRMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AppServerRMI {
    public static void main(String[] args) throws RemoteException {
        ServerRMI server = new ServerRMI();

        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("server", server);
    }
}
