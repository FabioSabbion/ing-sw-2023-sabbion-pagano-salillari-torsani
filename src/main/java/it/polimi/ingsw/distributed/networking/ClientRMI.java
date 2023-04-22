package it.polimi.ingsw.distributed.networking;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

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
    public void newPlayer(String nickname) throws RemoteException {
        System.out.println("New player: " + nickname);
    }

    @Override
    public void setNickname(String nickname) throws RemoteException {
        System.out.println("Client my name is:" + nickname);
        server.setNickname(nickname, this);
    }
}
