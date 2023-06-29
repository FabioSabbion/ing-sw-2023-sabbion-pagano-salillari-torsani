package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.view.ViewController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * The AppClientRMI class provides a method to start a client RMI connection with a server.
 */
public class AppClientRMI {
    public static void start(ViewController viewController, String IP) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(IP);
        Server server = (Server) registry.lookup("server");

        ClientImpl client = new ClientImpl();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        server.checkConnection();
                    } catch (RemoteException e) {
                        System.err.println("Cannot establish a connection with the server.\nCheck your internet connection and try again.");
                        System.exit(0);
                    }
                }
            }
        }.start();

        viewController.start(client, server);
    }
}
