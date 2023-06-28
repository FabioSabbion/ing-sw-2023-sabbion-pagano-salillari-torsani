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

        viewController.start(client, server);
    }
}
