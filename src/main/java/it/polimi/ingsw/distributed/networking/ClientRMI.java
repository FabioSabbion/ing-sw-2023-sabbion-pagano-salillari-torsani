package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.models.Coordinates;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientRMI extends UnicastRemoteObject implements Client {
    Server server;

    public ClientRMI(Server server) throws RemoteException {
        super();
        initialize(server);
    }

    public ClientRMI(Server sever, int port) throws RemoteException {
        super(port);
        initialize(sever);
    }

    public ClientRMI(Server sever, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        initialize(sever);
    }

    public void initialize(Server server) throws RemoteException {
        this.server = server;
    }

    @Override
    public void updatedPlayerList(List<String> players) throws RemoteException {
        System.out.println("New player: ");
    }

    @Override
    public void setNumberOfPlayers(int numberOfPlayers) throws RemoteException {

    }

    @Override
    public void setNickname(String nickname) throws RemoteException {
        System.out.println("Client my name is:" + nickname);
        server.setNickname(nickname, this);
    }

    @Override
    public void playerMove(List<Coordinates> coordinates, int column) throws RemoteException {

    }

    @Override
    public void updateGame(GameUpdate update) throws RemoteException {

    }
}
