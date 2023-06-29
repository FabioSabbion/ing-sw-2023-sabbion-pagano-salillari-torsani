package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.events.EventType;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.distributed.MessageUpdate;
import it.polimi.ingsw.distributed.exceptions.LobbyException;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.Message;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ClientSkeleton implements Client {
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
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException();
        }
    }

    @Override
    public void updateGame(GameUpdate update) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.GAME_STATE, update));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException();
        }
    }

    @Override
    public void serverError(String message) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.LOBBY_ERROR, message));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException();
        }
    }

    @Override
    public void askNumPlayers() throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.NUM_PLAYERS, null));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException();
        }
    }

    @Override
    public void keepAlive() throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.KEEP_ALIVE, null));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException();
        }
    }

    @Override
    public void showEndingScoreboard(GameUpdate gameUpdate) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.GAME_END, gameUpdate));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendMessagesUpdate(List<Message> messageList) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.MESSAGE_EVENT, new ArrayList<>(messageList)));
            oos.reset();
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

                    Lobby.getInstance().updateController(this, pickedTiles, col);
                }
                case MESSAGE_EVENT -> {
                    MessageUpdate mess = (MessageUpdate) message.data;

                    Lobby.getInstance().sendMessage(this, mess.to(), mess.message());
                }
                case KEEP_ALIVE -> {
//                    System.err.println("KEEP_ALIVE received");
                }
            }

        } catch (IOException | ClassNotFoundException ignored) {
            // Client disconnection is managed inside Lobby
        } catch (LobbyException | PickTilesException | NotEnoughCellsException e) {
            serverError(e.getMessage());
        }

    }
}
