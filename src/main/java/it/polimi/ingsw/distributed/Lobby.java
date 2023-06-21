package it.polimi.ingsw.distributed;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import it.polimi.ingsw.GamePersistence;
import it.polimi.ingsw.GameUpdateToFile;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.events.MessageEvent;
import it.polimi.ingsw.controller.events.ViewEvent;
import it.polimi.ingsw.distributed.exceptions.LobbyException;
import it.polimi.ingsw.distributed.networking.Client;
import it.polimi.ingsw.models.*;
import it.polimi.ingsw.utils.Observer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.rmi.RemoteException;
import java.util.*;

public class Lobby {
    public static final int seconds = 30;
    Map<String, Client> waitingPlayers;
    final BiMap<Client, String> clientNickname;
    final Map<String, GameController> nicknameController;
    final Map<String, Thread> lastSurvivingWinner;
    GamePersistence persistence = new GamePersistence();
    enum State {
        WAITING_FOR_GAME,
        CREATING_GAME
    };

    private State state;
    private int numPlayer;
    static private Lobby instance;

    private Lobby() {
        lastSurvivingWinner = new HashMap<>();
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
                        }
                    }

                    for (var client: removableClients) {
                        removeDisconnectedClient(client);
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

    public synchronized void removeDisconnectedClient(Client client) {
        var disconnected = clientNickname.remove(client);
        Client waitingClient = null;

        if (this.state == State.CREATING_GAME && waitingPlayers != null) {
            waitingClient = waitingPlayers.remove(disconnected);
        }

        if (waitingClient != null) {
            this.updatedWaitingPlayers();
            if (waitingPlayers.isEmpty()) {
                this.waitingPlayers = null;
                this.state = State.WAITING_FOR_GAME;
            }
        } else if (nicknameController.containsKey(disconnected)){
            var remainedClients = updatedPlayerList(disconnected);

//            Skip current client if he disconnected with no reason
            if (remainedClients.size() > 0 && nicknameController.get(disconnected).game.getCurrentPlayer().getNickname().equals(disconnected)) {
                List<String> offlineClients = Arrays.stream(nicknameController.get(disconnected).game.getPlayers())
                        .map(Player::getNickname)
                        .filter(player -> !clientNickname.containsValue(player))
                        .toList();

                nicknameController.get(disconnected).game.nextPlayer(offlineClients);
            }
        }
    }

    /**
     * Will send to all connected players of the same controller the update
     * @param nickname of the player that is triggering the update
     * @return remaining connected clients of the same controller
     */
    private List<String> updatedPlayerList(String nickname) {
        var clientWithTheSameController = Arrays.stream(nicknameController.get(nickname).game.getPlayers())
                .map(Player::getNickname).toList();

        List<String> remainedClients = clientWithTheSameController
                .stream()
                .filter(clientNickname::containsValue)
                .toList();

        switch (remainedClients.size()) {
            case 0 -> {
                nicknameController.get(nickname).game.emitGameState(true);
            }
            case 1 -> {
                this.setLastSurvivingWinner(remainedClients.get(0));
            }
            default -> {
                remainedClients.forEach(connectedClient -> {
                    synchronized (lastSurvivingWinner) {
                        if (lastSurvivingWinner.containsKey(connectedClient))
                            lastSurvivingWinner.get(connectedClient).interrupt();
                    }
                });
            }
        }

        remainedClients.stream().map(player -> clientNickname.inverse().get(player)).forEach(client1 -> {
            try {
                client1.updatedPlayerList(remainedClients);
            } catch (RemoteException e) {
                removeDisconnectedClient(client1);
            }
        });

        return remainedClients;
    }


    public void setLastSurvivingWinner(String lastClient) {
        var threadForTimer = new Thread(() -> {
            try {

                Thread.sleep(seconds * 1000);

                Game playerGame = null;
                synchronized (nicknameController) {

                    if (!nicknameController.containsKey(lastClient)) {
                        return;
                    }

                    playerGame = nicknameController.get(lastClient).game;

                    List<String> connectedClients = Arrays.stream(nicknameController.get(lastClient).game.getPlayers())
                            .map(Player::getNickname)
                            .filter(clientNickname::containsValue)
                            .toList();



                    if(connectedClients.size() > 1) {
                        System.err.println(connectedClients);
                        return;
                    }
                }

                if(playerGame != null) {
                    playerGame.emitGameState(true);
                }
            } catch (InterruptedException ignored) {
            }
        });


        synchronized (lastSurvivingWinner) {
            lastSurvivingWinner.put(lastClient, threadForTimer);
        }

        threadForTimer.start();
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
                GameController controller = nicknameController.get(nickname);
//                Nickname not connected to any client, connecting to controller
                clientNickname.put(client, nickname);

                this.setClientListener(client, controller);

                this.updatedPlayerList(nickname);
                controller.game.emitGameState();
                controller.chat.emitAllMessages();
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
                removeDisconnectedClient(client);
            }
        }
    }

    private void updatedWaitingPlayers() {
        for (var oldConnections: this.waitingPlayers.entrySet()) {
            try {
                oldConnections.getValue().updatedPlayerList(this.waitingPlayers.keySet().stream().toList());
            } catch (RemoteException e) {
                removeDisconnectedClient(oldConnections.getValue());
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
                    removeDisconnectedClient(client);
                }
            }
        });

        controller.chat.addObserver(new Observer<List<Message>, MessageEvent>() {
            @Override
            public void update(List<Message> value, MessageEvent eventType) {
                if (!clientNickname.containsKey(client)) {
                    controller.chat.deleteObserver(this);
                    return;
                }

                if (eventType == MessageEvent.SINGLE_MESSAGE
                        && !(value.get(0).to() == null
                        || Objects.equals(value.get(0).to(), clientNickname.get(client))
                        || Objects.equals(value.get(0).from(), clientNickname.get(client)))) {
                    return;
                }

                try {
                    client.sendMessagesUpdate(value.stream().filter(message ->
                            message.to() == null
                                    || Objects.equals(message.to(), clientNickname.get(client))
                                    || Objects.equals(message.from(), clientNickname.get(client))).toList());
                } catch (RemoteException e) {
                    controller.chat.deleteObserver(this);
                    removeDisconnectedClient(client);
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

    public void updateController(Client client, List<Coordinates> coordinatesList, int column) throws LobbyException {
        var gameData = this.getNicknameController(client);

        var offlinePlayers = Arrays.stream(gameData.getRight().game.getPlayers())
                .map(Player::getNickname)
                .filter(player -> !this.clientNickname.containsValue(player))
                .toList();

        if (gameData.getRight().game.getPlayers().length - offlinePlayers.size() == 1) {
//            we will assume that the last remaining player is this player, if something weird has happened we have to fix it
            synchronized (this.lastSurvivingWinner) {
                if(!this.lastSurvivingWinner.containsKey(this.clientNickname.get(client))) {
                    this.setLastSurvivingWinner(this.clientNickname.get(client));
                }
            }
        }

        gameData.getRight().update(coordinatesList, column, gameData.getLeft(), offlinePlayers);
    }

    private Pair<String, GameController> getNicknameController(Client client) throws LobbyException {
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

    public void sendMessage(Client client, @Nullable String to, String message) {
        String from = clientNickname.get(client);

        nicknameController.get(from).chat.sendMessage(message, from, to);
    }
}
