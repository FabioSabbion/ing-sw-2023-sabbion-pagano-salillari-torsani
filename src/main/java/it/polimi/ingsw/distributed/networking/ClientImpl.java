package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.GameUpdate;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientImpl extends UnicastRemoteObject implements Client, Runnable {

    public ClientImpl(Server server) throws RemoteException {
        super();
        initialize(server);
    }

    public ClientImpl(Server sever, int port) throws RemoteException {
        super(port);
        initialize(sever);
    }

    public ClientImpl(Server sever, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        initialize(sever);
    }

    public void initialize(Server server) throws RemoteException {
        // TODO: observe view events and call methods on server
    }

    @Override
    public void updatedPlayerList(List<String> players) throws RemoteException {
        System.out.println("Updating players: " + players.toString());
    }

    @Override
    public void updateGame(GameUpdate update) throws RemoteException {
        System.out.println("Received game update");
    }

    @Override
    public void serverError(String message) throws RemoteException {
        System.out.println("Error: " + message);
    }

    @Override
    public void askNumPlayers() throws RemoteException {
        System.out.println("Server asked for the number of players");
    }

    @Override
    public void run() {
        // TODO: run view

    }
}
