package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.models.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Client extends Remote {
    void updatedPlayerList(List<String> players) throws RemoteException;
    void updateGame(GameUpdate update) throws RemoteException;
    void serverError(String message) throws RemoteException;
    void askNumPlayers() throws RemoteException;
    void keepAlive() throws RemoteException;
    void showEndingScoreboard(GameUpdate gameUpdate) throws RemoteException;
    void sendMessagesUpdate(List<Message> messageList) throws RemoteException;
}
