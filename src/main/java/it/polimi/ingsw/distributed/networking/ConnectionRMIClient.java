package it.polimi.ingsw.distributed.networking;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class ConnectionRMIClient extends UnicastRemoteObject implements Connection {
    Connection server;

    public ConnectionRMIClient(Connection server) throws RemoteException {
        super();
        initialize(server);
    }

    public ConnectionRMIClient(Connection sever, int port) throws RemoteException {
        super(port);
        initialize(sever);
    }

    public ConnectionRMIClient(Connection sever, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        initialize(sever);
    }

    public void initialize(Connection server) throws RemoteException {
        server.initialize(this);
        this.server = server;
    }

    @Override
    public void newPlayer(String nickname) throws RemoteException {
        System.out.println("New player: " + nickname);
    }

    @Override
    public void setNickname(String nickname) throws RemoteException {
        System.out.println("Client my name is:" + nickname);
        server.setNickname(nickname);
    }
}
