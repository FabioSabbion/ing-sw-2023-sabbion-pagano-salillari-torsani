package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.distributed.exceptions.LobbyException;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;

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
            var gameData = Lobby.getInstance().getNicknameController(client);

            gameData.getRight().update(coordinates, column, gameData.getLeft());
        } catch (LobbyException | PickTilesException | NotEnoughCellsException e) {
            client.serverError(e.getMessage());
        }
    }

}
