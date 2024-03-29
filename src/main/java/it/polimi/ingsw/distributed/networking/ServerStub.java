package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.events.EventType;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.MessageUpdate;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.Message;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ServerStub implements Server{
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private Socket socket;

    public ServerStub(String ip, int port) throws ConnectException {
        try {
            this.socket = new Socket(ip, port);
            try {
                this.oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RemoteException("Cannot create output stream", e);
            }
            try {
                this.ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new RemoteException("Cannot create input stream", e);
            }
        } catch (IOException e) {
            throw new ConnectException(e.getMessage());
        }
    }

    @Override
    public void setNickname(String nickname, Client client) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.CONNECT, nickname));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNumPlayers(int num, Client client) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.NUM_PLAYERS, num));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void playerMove(List<Coordinates> coordinates, int column, Client client) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.PICK_TILES, (Serializable) coordinates));
            oos.reset();
            oos.flush();
            oos.writeObject(new SocketMessage(EventType.CHOOSE_COLUMN, column));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessageTo(@Nullable String to, String message, Client client) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.MESSAGE_EVENT, new MessageUpdate(to, message)));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkConnection() throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.KEEP_ALIVE, null));
            oos.reset();
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException();
        }
    }

    public void receive(Client client) throws RemoteException, IOException {
        SocketMessage message;
        try {
            message = (SocketMessage) ois.readObject();
            switch (message.eventType) {
                case NUM_PLAYERS -> {
                    client.askNumPlayers();
                }
                case LOBBY_UPDATE -> {
                    List<String> players = (List<String>) message.data;
                    client.updatedPlayerList(players);
                }
                case GAME_STATE -> {
                    GameUpdate gameUpdate = (GameUpdate) message.data;
                    client.updateGame(gameUpdate);
                }
                case LOBBY_ERROR -> {
                    String msg = (String) message.data;
                    client.serverError(msg);
                }
                case GAME_END -> {
                    GameUpdate gameUpdate = (GameUpdate) message.data;
                    client.showEndingScoreboard(gameUpdate);
                }
                case MESSAGE_EVENT -> {
                    List<Message> messageList = (ArrayList<Message>) message.data;
                    client.sendMessagesUpdate(messageList);
                }
            }
        } catch (IOException e) {
            throw new RemoteException();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws RemoteException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RemoteException("Cannot close socket", e);
        }
    }
}
