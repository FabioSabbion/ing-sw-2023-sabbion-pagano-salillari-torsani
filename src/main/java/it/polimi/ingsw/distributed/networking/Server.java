package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.models.Coordinates;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Server extends Remote {
    void setNickname(String nickname, Client client) throws RemoteException;
    void setNumPlayers(int num, Client client) throws RemoteException;
    void playerMove(List<Coordinates> coordinates, int column) throws RemoteException;

}
