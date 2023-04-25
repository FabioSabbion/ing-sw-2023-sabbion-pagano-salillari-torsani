package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.models.Coordinates;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            oos.writeObject(players);
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
        String s;
        try {
            s = (String) ois.readObject();
            System.out.println("Client skeleton read " + s);
            Lobby.getInstance().setNickname(s, this);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
