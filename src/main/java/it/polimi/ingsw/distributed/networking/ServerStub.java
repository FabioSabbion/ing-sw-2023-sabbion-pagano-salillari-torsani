package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.controller.events.EventType;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    }

    @Override
    public void playerMove(List<Coordinates> coordinates, int column) throws RemoteException {

    }

    public void receive(Client client) throws RemoteException {
        SocketMessage message;
        try {
            message = (SocketMessage) ois.readObject();
            switch (message.eventType) {
                case NUM_PLAYERS -> {

                }
                case LOBBY_UPDATE -> {
                    List<String> players = (List<String>) message.data;
                    client.updatedPlayerList(players);
                }
                case GAME_STATE -> {

                }
            }
        } catch (IOException e) {
            // TODO: handle connection errors
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
