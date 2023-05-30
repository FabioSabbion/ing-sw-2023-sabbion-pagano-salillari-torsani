package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.models.Message;
import it.polimi.ingsw.view.ViewController;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientImpl extends UnicastRemoteObject implements Client {
    ViewController view;

    public ClientImpl() throws RemoteException {
        super();
    }

    public ClientImpl(int port) throws RemoteException {
        super(port);
    }

    public ClientImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void updatedPlayerList(List<String> players) throws RemoteException {
        System.err.println("Updated player list");
        view.updatedPlayerList(players);
    }

    @Override
    public void updateGame(GameUpdate update) throws RemoteException {
        view.updateGame(update);
        System.err.println("Game has been updated");
    }

    @Override
    public void serverError(String message) throws RemoteException {
        view.serverError(message);
    }

    @Override
    public void askNumPlayers() throws RemoteException {
        System.err.println("Asked number of players");
        view.askNumPlayers();
    }

    @Override
    public void keepAlive() throws RemoteException {
        //System.out.println("Server asked for keepalive");
    }

    @Override
    public void showEndingScoreboard(GameUpdate update) throws RemoteException {
        view.updateGame(update);
        view.showEndingScreen();
    }

    @Override
    public void sendMessagesUpdate(List<Message> messageList) throws RemoteException {
        System.err.println("MESSAGES ARRIVED " + messageList);
    }

    public void run(ViewController view) {
        this.view = view;
    }
}
