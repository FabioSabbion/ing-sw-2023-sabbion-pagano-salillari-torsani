package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.controller.events.EventType;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.models.Coordinates;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;

public class ServerStub implements Server{
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private Socket socket;

    public ServerStub(String ip, int port) {
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNickname(String nickname, Client client) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.CONNECT, nickname));
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNumPlayers(int num, Client client) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.NUM_PLAYERS, num));
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void playerMove(List<Coordinates> coordinates, int column, Client client) throws RemoteException {
        try {
            oos.writeObject(new SocketMessage(EventType.PICK_TILES, (Serializable) coordinates));
            oos.flush();
            oos.writeObject(new SocketMessage(EventType.CHOOSE_COLUMN, column));
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receive(Client client) throws RemoteException {
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
                case KEEP_ALIVE -> {
                    //System.out.println("KEEP_ALIVE received");
                }
            }
        } catch (IOException e) {
            // TODO: handle connection errors
            client.serverError("Connection error");
            throw new RuntimeException(e);
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
