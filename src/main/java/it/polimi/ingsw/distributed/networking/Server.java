package it.polimi.ingsw.distributed.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    public void setNickname(String nickname, Client client) throws RemoteException;
}
