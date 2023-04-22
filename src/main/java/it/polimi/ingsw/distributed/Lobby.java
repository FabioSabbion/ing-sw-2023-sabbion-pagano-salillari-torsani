package it.polimi.ingsw.distributed;

import it.polimi.ingsw.distributed.exceptions.LobbyException;
import it.polimi.ingsw.distributed.networking.Connection;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class Lobby {
    Map<String, Connection> connections;
    Map<String, GameServer> nicknameServer;
    enum State {
        WAITING_FOR_GAME,
        CREATING_GAME
    };

    private State state;
    private int numPlayer;
    static private Lobby instance;

    private Lobby() {

    }
    public synchronized void setNickname(String nickname, Connection connection) {
        System.out.println("New nickname from client " + nickname);

        if (nicknameServer == null) {
            this.nicknameServer = new HashMap<>();
        }

        if (nicknameServer.containsKey(nickname)) {
//           TODO WRITE WHAT TO DO WITH A DISCONNECTED PLAYER
//            TEST DISCONNECTION WITH A BLOCKING PATTERN
//            AND ADD TO CONNECTION A VALUE THAT WILL STORE THE WAIT OF THE CURRENT THREAD
        } else if(this.state == State.CREATING_GAME) {
            for (var oldConnections: this.connections.entrySet()) {
                System.out.println("Sending nickname: " + nickname + " to " + oldConnections.getKey());
                try {
                    oldConnections.getValue().newPlayer(nickname);
                } catch (RemoteException e) {
                    System.out.println("Error");
                }
            }

            this.connections.put(nickname, connection);

//            if (this.numPlayer == this.connections.size()) {
//                this.state = State.WAITING_FOR_GAME;
//                this.numPlayer = -1;
//
////                TODO CREATE TRUE CONTROLLER
//            }
        } else {
            this.connections = new HashMap<>();
            this.connections.put(nickname, connection);
            this.state = State.CREATING_GAME;
        }
    }

    private void receivedNumberOfPlayers(int numPlayer) throws LobbyException {
        if (this.connections.size() == 1) {
            this.numPlayer = numPlayer;
        } else {
            throw new LobbyException("Not first player, impossible to set number of players");
        }
    }

   public static Lobby getInstance() {
        if(Lobby.instance == null) {
            Lobby.instance = new Lobby();
        }

        return Lobby.instance;
    }

    public State getState() {
        return this.state;
    }
}
