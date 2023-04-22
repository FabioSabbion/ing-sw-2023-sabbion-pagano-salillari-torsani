package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.Lobby;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class ConnectionRMIServer extends UnicastRemoteObject implements Connection {
    Connection client;

    public ConnectionRMIServer() throws RemoteException {
    }

    public ConnectionRMIServer(int port) throws RemoteException {
        super(port);
    }

    public ConnectionRMIServer(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    public void setNickname(String nickname) throws RemoteException {
        Lobby.getInstance().setNickname(nickname, this);
    }

    @Override
    public void initialize(Connection connection) throws RemoteException {
        this.client = connection;
    }

    public void newPlayer(String nickname) throws RemoteException {
        client.newPlayer(nickname);
    }

}
