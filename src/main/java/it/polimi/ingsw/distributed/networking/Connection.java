package it.polimi.ingsw.distributed.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Connection extends Remote {
    public void newPlayer(String nickname) throws RemoteException;
    public void setNickname(String nickname) throws RemoteException;
    public void initialize(Connection connection) throws RemoteException;
}
