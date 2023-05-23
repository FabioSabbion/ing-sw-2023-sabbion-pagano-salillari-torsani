package it.polimi.ingsw.distributed;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import it.polimi.ingsw.GamePersistence;
import it.polimi.ingsw.GameUpdateToFile;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.events.ViewEvent;
import it.polimi.ingsw.distributed.exceptions.LobbyException;
import it.polimi.ingsw.distributed.networking.Client;
import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.CommonGoalCardFactory;
import it.polimi.ingsw.models.Game;
import it.polimi.ingsw.models.Player;
import it.polimi.ingsw.utils.Observer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.RemoteException;
import java.util.*;

public class Lobby {
    Map<String, Client> waitingPlayers;
    final BiMap<Client, String> clientNickname;
    Map<String, GameController> nicknameController;
    GamePersistence persistence = new GamePersistence();
    enum State {
        WAITING_FOR_GAME,
        CREATING_GAME
    };

    private State state;
    private int numPlayer;
    static private Lobby instance;

    private Lobby() {
        clientNickname = Maps.synchronizedBiMap(HashBiMap.create());

        this.nicknameController = new HashMap<>();

        (new Thread(() -> {
            while (true) {
                synchronized (this) {
                    List<Client> removableClients = new ArrayList<>();

                    for (var client: clientNickname.entrySet()) {
                        try {
                            client.getKey().keepAlive();
                        } catch (RemoteException e) {
                            removableClients.add(client.getKey());

                            System.out.println("Client disconnected");
                        }
                    }

                    Client waitingClient = null;

                    for (var client: removableClients) {
                        var disconnected = clientNickname.remove(client);

                        if (this.state == State.CREATING_GAME && waitingPlayers != null) {
                            waitingClient = waitingPlayers.remove(disconnected);
                        }
                    }

                    if (waitingClient != null) {
                        this.updatedWaitingPlayers();
                        if (waitingPlayers.isEmpty()) {
                            this.waitingPlayers = null;
                            this.state = State.WAITING_FOR_GAME;
                        }
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
    public synchronized void setNickname(String nickname, Client client) throws LobbyException {
        System.out.println("New nickname from client " + nickname);

        if (clientNickname.containsKey(client)) {
            throw new LobbyException("Client already present");
        }


        if (nicknameController.containsKey(nickname)) {
            if(clientNickname.containsValue(nickname)) {
                // Nickname already in use => send error
                throw new LobbyException("Nickname already in use");
            } else {
//                Nickname not connected to any client, connecting to controller
                clientNickname.put(client, nickname);

                this.setClientListener(client, nicknameController.get(nickname));
                nicknameController.get(nickname).game.emitGameState();
            }
        } else if(this.state == State.CREATING_GAME) {
            if (clientNickname.containsValue(nickname)) {
                throw new LobbyException("Nickname already in lobby");
            }

            if (this.numPlayer == -1) {
                throw new LobbyException("Number of players still not known");
            }

            clientNickname.put(client, nickname);
            this.waitingPlayers.put(nickname, client);

            this.updatedWaitingPlayers();

            if (this.numPlayer == this.waitingPlayers.size()) {
                System.err.println("Same number of player");
                this.state = State.WAITING_FOR_GAME;
                this.numPlayer = -1;

                System.err.println("Waiting players: " + this.waitingPlayers);

                var controller = new GameController(this.waitingPlayers.keySet().stream().toList());

                for (var connection: this.waitingPlayers.entrySet()) {
                    this.nicknameController.put(connection.getKey(), controller);

                    this.setClientListener(connection.getValue(), controller);
                }

                this.setControllerSaveFileListener(controller);

                controller.game.emitGameState();

                this.waitingPlayers = null;
            }
        } else {
            try {
                System.out.println("Asking num players to " + nickname);
                clientNickname.put(client, nickname);
                this.waitingPlayers = new HashMap<>();
                this.waitingPlayers.put(nickname, client);

                this.state = State.CREATING_GAME;

                client.askNumPlayers();

                this.numPlayer = -1;

                System.out.println("Creating game");
            } catch (RemoteException e) {
                this.waitingPlayers = null;
                this.state = State.WAITING_FOR_GAME;
                System.out.println("Client not reachable");
            }
        }
    }

    private void updatedWaitingPlayers() {
        for (var oldConnections: this.waitingPlayers.entrySet()) {
            try {
                oldConnections.getValue().updatedPlayerList(this.waitingPlayers.keySet().stream().toList());
            } catch (RemoteException e) {
                System.out.println("Error");
            }
        }
    }

    private void setClientListener(Client client, GameController controller) {
        controller.game.addObserver(new Observer<GameUpdateToFile, ViewEvent>() {
            @Override
            public void update(GameUpdateToFile value, ViewEvent eventType) {
//                Weird cases in which the client is still subscribed even if already disconnected
                System.err.println("called update");
                if (!clientNickname.containsKey(client)) {
                    controller.game.deleteObserver(this);
                    return;
                }

                try {
                    if (eventType == ViewEvent.GAME_END) {
                        client.showEndingScoreboard(value.update());

                        clientNickname.remove(client);
                        controller.game.deleteObserver(this);
                    } else {
                        var filteredGameUpdate = GameUpdate.filterPersonalGoalCards(value.update(), clientNickname.get(client));
                        client.updateGame(filteredGameUpdate);
                    }
                } catch (RemoteException e) {
                    controller.game.deleteObserver(this);
                }
            }
        });
    }

    public void setNumPlayer(int numPlayer) throws LobbyException {
        System.out.println("New number of players " + numPlayer);
        if (this.waitingPlayers != null && this.waitingPlayers.size() == 1) {
            if (numPlayer >= 2 && numPlayer <= 4) {
                this.numPlayer = numPlayer;
            } else {
                throw new LobbyException("Wrong number of players");
            }

        } else {
            throw new LobbyException("Not first player, impossible to set number of players");
        }
    }

    public Pair<String, GameController> getNicknameController(Client client) throws LobbyException {
        if (!this.clientNickname.containsKey(client)
                || !this.nicknameController.containsKey(this.clientNickname.get(client))) {
            throw new LobbyException("Client not found in any controller");
        }

        var nickname = this.clientNickname.get(client);
        return new ImmutablePair<>(nickname, this.nicknameController.get(nickname));
    }

   public static Lobby getInstance() {
        if(Lobby.instance == null) {
            Lobby.instance = new Lobby();
            instance.persistence.loadOldGames();
        }

        return Lobby.instance;
    }

    public State getState() {
        return this.state;
    }

    public void loadLobbyFromUpdates(Map<Integer, GameUpdateToFile> updates) {
        for (var update : updates.entrySet()) {
            var fileUpdt = update.getValue();

            var players = fileUpdt.update().players().stream().map(PlayerUpdate::to).toList();

            var cards = fileUpdt.update().commonGoalCards().stream().map(commonGoalCardUpdate -> {
                var card = CommonGoalCardFactory.buildFromJson(players.size(), commonGoalCardUpdate.commonGoalCardID());

                commonGoalCardUpdate.playerUpdateList().stream().map(player -> players.stream().filter(player1 ->
                        player1.getNickname().equals(player)).findFirst().get()).forEach(card::checkGoal);

                return card;
            }).toList();


            assert fileUpdt.remainingTiles() != null;
            Game game = new Game(
                    players.toArray(new Player[0]),
                    cards.toArray(new CommonGoalCard[0]),
                    fileUpdt.remainingTiles(),
                    fileUpdt.update().livingRoom(),
                    players.stream().filter(p -> p.getNickname().equals(fileUpdt.update().currentPlayer().nickname())).findFirst().get());

            GameController controller = new GameController(game, update.getKey());

            players.forEach(player -> this.nicknameController.put(player.getNickname(), controller));


            this.setControllerSaveFileListener(controller);
        }
    }


    private void setControllerSaveFileListener(GameController controller) {
        controller.game.addObserver(new Observer<GameUpdateToFile, ViewEvent>() {
            @Override
            public void update(GameUpdateToFile value, ViewEvent event) {
                if(event == ViewEvent.GAME_END) {
                    Arrays.stream(controller.game.getPlayers()).forEach(player -> {
                        nicknameController.remove(player.getNickname());
                    });

                    controller.game.deleteObserver(this);

                    persistence.removeGames(controller.id);
                } else {
                    persistence.saveGames(value, controller.id);
                }
            }
        });
    }
}
