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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerRMI extends UnicastRemoteObject implements Server {
    Executor executor = Executors.newFixedThreadPool(5);
    public ServerRMI() throws RemoteException {
    }

    public ServerRMI(int port) throws RemoteException {
        super(port);
    }

    public ServerRMI(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public synchronized void setNickname(String nickname, Client client) {
        executor.execute(() -> {
            try {
                Lobby.getInstance().setNickname(nickname, client);
            } catch (LobbyException e) {
                try {
                    client.serverError(e.getMessage());
                } catch (RemoteException ex) {
                    Lobby.getInstance().removeDisconnectedClient(client);
                }
            }
        });
    }

    @Override
    public void setNumPlayers(int num, Client client) {
        executor.execute(() -> {
            try {
                Lobby.getInstance().setNumPlayer(num);
            } catch (LobbyException e) {
                try {
                    client.serverError(e.getMessage());
                } catch (RemoteException ex) {
                    Lobby.getInstance().removeDisconnectedClient(client);
                }
            }
        });
    }

    @Override
    public void playerMove(List<Coordinates> coordinates, int column, Client client) {
        executor.execute(() -> {
            try {
                Lobby.getInstance().updateController(client, coordinates, column);
            } catch (LobbyException e) {
                try {
                    client.serverError(e.getMessage());
                } catch (RemoteException ex) {
                    Lobby.getInstance().removeDisconnectedClient(client);
                }
            }
        });
    }

    @Override
    public void sendMessageTo(@Nullable String to, String message, Client client) {
        executor.execute(() -> {
            Lobby.getInstance().sendMessage(client, to, message);
        });
    }

}
