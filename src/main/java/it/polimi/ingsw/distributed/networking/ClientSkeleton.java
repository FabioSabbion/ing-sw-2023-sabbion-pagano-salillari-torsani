package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.controller.events.EventType;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.distributed.exceptions.LobbyException;
import it.polimi.ingsw.models.Coordinates;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

public class ClientSkeleton implements Client{
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    public ClientSkeleton(ObjectInputStream ois, ObjectOutputStream oos) throws RemoteException {
        this.ois = ois;
        this.oos = oos;
    }

    @Override
    public void updatedPlayerList(List<String> players) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.LOBBY_UPDATE, (Serializable) players));
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateGame(GameUpdate update) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.GAME_STATE, update));
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void serverError(String message) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.LOBBY_ERROR, message));
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void askNumPlayers() throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.NUM_PLAYERS, null));
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receive() throws RemoteException {
        SocketMessage message;
        try {
            message = (SocketMessage) ois.readObject();
            switch (message.eventType) {
                case CONNECT -> {
                    String nickname = (String) message.data;
                    Lobby.getInstance().setNickname(nickname, this);
                }
                case NUM_PLAYERS -> {
                    int n = (int) message.data;
                    Lobby.getInstance().setNumPlayer(n);
                }
                case PICK_TILES -> {
                    List<Coordinates> pickedTiles = (List<Coordinates>) message.data;
                    message = (SocketMessage) ois.readObject();
                    if (message.eventType != EventType.CHOOSE_COLUMN) {
                        throw new LobbyException("Expecting a CHOOSE_COLUMN event");
                    }
                    int col = (int) message.data;

                    var gameData = Lobby.getInstance().getNicknameController(this);
                    gameData.getRight().update(pickedTiles, col, gameData.getLeft());
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            // TODO: call method on Lobby to notify the client is disconnected
            throw new RuntimeException(e);
        } catch (LobbyException e) {
            serverError(e.getMessage());
        }

    }
}
