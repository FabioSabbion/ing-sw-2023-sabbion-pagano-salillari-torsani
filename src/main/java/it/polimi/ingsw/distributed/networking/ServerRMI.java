package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.models.Coordinates;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ServerRMI extends UnicastRemoteObject implements Server {
    public ServerRMI() throws RemoteException {
    }

    public ServerRMI(int port) throws RemoteException {
        super(port);
    }

    public ServerRMI(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public synchronized void setNickname(String nickname, Client client) throws RemoteException {
        Lobby.getInstance().setNickname(nickname, client);
    }

    @Override
    public void setNumPlayers(int num, Client client) throws RemoteException {

    }

    @Override
    public void playerMove(List<Coordinates> coordinates, int column) throws RemoteException {

    }

}
