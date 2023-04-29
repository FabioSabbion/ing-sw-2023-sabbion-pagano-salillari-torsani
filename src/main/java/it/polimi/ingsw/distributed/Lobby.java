package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.events.ViewEvent;
import it.polimi.ingsw.distributed.exceptions.LobbyException;
import it.polimi.ingsw.distributed.networking.Client;
import it.polimi.ingsw.utils.Observer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Lobby {
    Map<String, Client> waitingPlayers;
    Map<Client, Pair<String, GameController>> clientNicknameController;
    Set<Client> connectedClients;
    enum State {
        WAITING_FOR_GAME,
        CREATING_GAME
    };

    private State state;
    private int numPlayer;
    static private Lobby instance;

    private Lobby() {
        connectedClients = new HashSet<>();

        (new Thread(() -> {
            while (true) {
                for (var client: connectedClients) {
                    try {
                        client.keepAlive();
                    } catch (RemoteException e) {
                        connectedClients.remove(client);


                        System.out.println("Client disconnected");
                    }
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        })).start();
    }
    public synchronized void setNickname(String nickname, Client client) {
        System.out.println("New nickname from client " + nickname);
        connectedClients.add(client);

        if (clientNicknameController == null) {
            this.clientNicknameController = new HashMap<>();
        }

        if (clientNicknameController.containsKey(client)) {
//           TODO WRITE WHAT TO DO WITH A DISCONNECTED PLAYER
//            TEST DISCONNECTION WITH A BLOCKING PATTERN
//            AND ADD TO CONNECTION A VALUE THAT WILL STORE THE WAIT OF THE CURRENT THREAD
        } else if(this.state == State.CREATING_GAME) {
            this.waitingPlayers.put(nickname, client);

            for (var oldConnections: this.waitingPlayers.entrySet()) {
                System.out.println("Sending nickname: " + nickname + " to " + oldConnections.getKey());
                try {
                    oldConnections.getValue().updatedPlayerList(this.waitingPlayers.keySet().stream().toList());
                } catch (RemoteException e) {
                    System.out.println("Error");
                }
            }

            if (this.numPlayer == this.waitingPlayers.size()) {
                this.state = State.WAITING_FOR_GAME;
                this.numPlayer = -1;

                var controller = new GameController(this.waitingPlayers.keySet().stream().toList());

                for (var connection: this.waitingPlayers.entrySet()) {
                    this.clientNicknameController.put(connection.getValue(), new ImmutablePair<>(connection.getKey(), controller));

                    controller.game.addObserver(new Observer<GameUpdate, ViewEvent>() {
                        @Override
                        public void update(GameUpdate value, ViewEvent eventType) {
//                            TODO update client state if not reachable!

                            System.out.println("Implement in the client the connection details, if the connection is down we should change the client state");
                            try {
                                connection.getValue().updateGame(value);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }

                this.waitingPlayers = null;
            }
        } else {
            this.waitingPlayers = new HashMap<>();
            this.waitingPlayers.put(nickname, client);
            this.state = State.CREATING_GAME;
        }
    }

    public void setNumPlayer(int numPlayer) throws LobbyException {
        if (this.waitingPlayers.size() == 1) {
            this.numPlayer = numPlayer;
        } else {
            throw new LobbyException("Not first player, impossible to set number of players");
        }
    }

    public Pair<String, GameController> getNicknameController(Client client) throws LobbyException {
        if (!this.clientNicknameController.containsKey(client)) {
            throw new LobbyException("Client not found in any controller");
        }


        return this.clientNicknameController.get(client);
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
