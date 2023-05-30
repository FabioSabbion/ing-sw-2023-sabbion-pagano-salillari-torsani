package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.distributed.exceptions.LobbyException;
import it.polimi.ingsw.models.Coordinates;

import javax.annotation.Nullable;
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
        try {
            Lobby.getInstance().setNickname(nickname, client);
        } catch (LobbyException e) {
            client.serverError(e.getMessage());
        }
    }

    @Override
    public void setNumPlayers(int num, Client client) throws RemoteException {
        try {
            Lobby.getInstance().setNumPlayer(num);
        } catch (LobbyException e) {
            client.serverError(e.getMessage());
        }
    }

    @Override
    public void playerMove(List<Coordinates> coordinates, int column, Client client) throws RemoteException {
        try {
            Lobby.getInstance().updateController(client, coordinates, column);
        } catch (LobbyException e) {
            client.serverError(e.getMessage());
        }
    }

    @Override
    public void sendMessageTo(@Nullable String to, String message, Client client) throws RemoteException {
        Lobby.getInstance().sendMessage(client, to, message);
    }

}
