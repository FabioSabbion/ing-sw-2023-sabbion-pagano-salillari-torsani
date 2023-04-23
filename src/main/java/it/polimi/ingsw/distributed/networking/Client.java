package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.models.Coordinates;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Client extends Remote {
    public void updatedPlayerList(List<String> players) throws RemoteException;
    public void setNumberOfPlayers(int numberOfPlayers) throws RemoteException;
    public void setNickname(String nickname) throws RemoteException;
    public void playerMove(List<Coordinates> coordinates, int column) throws RemoteException;
    public void updateGame(GameUpdate update) throws RemoteException;
}
