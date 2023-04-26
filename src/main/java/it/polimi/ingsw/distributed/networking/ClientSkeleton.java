package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.controller.events.EventType;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.models.Coordinates;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
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

    }

    @Override
    public void askNumPlayers() throws RemoteException {

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

                }
                case PLAYER_ACTION -> {

                }
            }

        } catch (IOException | ClassNotFoundException e) {
            // TODO IOException fires when socket connection fails
            // We need to notify GameServer/Lobby that the client has disconnected

            throw new RuntimeException(e);
        }

    }
}
