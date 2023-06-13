package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ServerRMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AppServerRMI extends Thread {

    @Override
    public void run() {
        try {
            ServerRMI server = new ServerRMI();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("server", server);
            System.out.println("RMI Server started");
        } catch (RemoteException e) {
            System.out.println("Couldn't start RMI server");
            e.printStackTrace();
        }

    }
}
