package it.polimi.ingsw.distributed;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import it.polimi.ingsw.GamePersistence;
import it.polimi.ingsw.GameUpdateToFile;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.events.MessageEvent;
import it.polimi.ingsw.events.ViewEvent;
import it.polimi.ingsw.distributed.exceptions.LobbyException;
import it.polimi.ingsw.distributed.networking.Client;
import it.polimi.ingsw.models.*;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import it.polimi.ingsw.utils.Observer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Lobby {
    private final Executor removeDisconnected = Executors.newSingleThreadExecutor();
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

    /**
     * Removes a disconnected client from the server.
     * @param client The client to be removed
     */
    public synchronized void removeDisconnectedClient(Client client) {
        this.removeDisconnected.execute(() -> {
            var disconnected = clientNickname.remove(client);
            Client waitingClient = null;

            if (this.state == State.CREATING_GAME && waitingPlayers != null) {
                waitingClient = waitingPlayers.remove(disconnected);
            }

            if (waitingClient != null) {
                this.updatedWaitingPlayers();
                if (waitingPlayers != null && waitingPlayers.isEmpty()) {
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
        });
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

    /**
     * It sets a timer, lasting <code>Lobby.seconds</code>, after which the last online player is declared
     * the winner of the game. This method is called when only one player remains in the game.
     * @param lastClient remaining the game
     */
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

    /**
     * The method assigns a nickname to a client, while checking if the nickname is already in use,
     * and adds the client to an existing lobby, if <code>Lobby.numPlayer</code> has been defined.
     * @param nickname of the joining player
     * @param client to which assign the nickname above
     * @throws LobbyException in case the number of player is still not known
     */
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

    /**
     * It updates the player list for all waiting players in the lobby
     */
    private void updatedWaitingPlayers() {
        final var waitingPlayersCopy = this.waitingPlayers.entrySet().stream().toList();
        for (var oldConnections: waitingPlayersCopy) {
            try {
                oldConnections.getValue().updatedPlayerList(this.waitingPlayers.keySet().stream().toList());
            } catch (RemoteException e) {
                removeDisconnectedClient(oldConnections.getValue());
            }
        }


    }

    /**
     * Sets up event listeners for a client connected to a game controller.
     * The client is associated with a game controller to receive game updates
     * and a chat observer to handle chat messages.
     * @param client to set up the event listeners for.
     * @param controller The game controller to listen to for game and chat updates.
     */
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

    /**
     * Sets the number of players in the lobby.
     * Only the first player in the lobby can set the number of players.
     * @param numPlayer  The number of players to set.
     * @throws LobbyException If the number of players is invalid or the player is not the first player in the lobby.
     */
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

    /**
     * Updates the game controller with the player's actions.
     * @param client The client making the move.
     * @param coordinatesList The list of coordinates chosen by the player.
     * @param column The column chosen by the player.
     * @throws LobbyException If the client is not present in the lobby.
     * @throws PickTilesException If there is an error in picking the tiles.
     * @throws NotEnoughCellsException If there are not enough cells available for the move.
     */
    public void updateController(Client client, List<Coordinates> coordinatesList, int column) throws LobbyException, PickTilesException, NotEnoughCellsException {
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

    /**
     * Retrieves the nickname and corresponding game controller for a client.
     * @param client The client for which to retrieve the controller.
     * @return A pair containing the client's nickname and the corresponding game controller.
     * @throws LobbyException  If the client is not found in any controller.
     */
    private Pair<String, GameController> getNicknameController(Client client) throws LobbyException {
        if (!this.clientNickname.containsKey(client)
                || !this.nicknameController.containsKey(this.clientNickname.get(client))) {
            throw new LobbyException("Client not found in any controller");
        }

        var nickname = this.clientNickname.get(client);
        return new ImmutablePair<>(nickname, this.nicknameController.get(nickname));
    }

    /**
     * @return the singleton instance of the Lobby.
     */
   public static Lobby getInstance() {
        if(Lobby.instance == null) {
            Lobby.instance = new Lobby();
            instance.persistence.loadOldGames();
        }

        return Lobby.instance;
    }

    /**
     * @return current Lobby state
     */
    public State getState() {
        return this.state;
    }

    /**
     * Loads the lobby state from a map of game updates.
     * @param updates
     */
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

    /**
     * Sets a save file listener for the GameController.
     * The listener saves game updates to a file when the game state
     * changes and removes the game from persistence when it ends.
     * @param controller The GameController to set the listener for.
     */
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

    /**
     * Sends a message from a client to another client or to the entire chat.
     * @param client The client sending the message.
     * @param to The nickname of the recipient client. If null, the message is sent to the entire chat.
     * @param message The content of the message.
     * @throws LobbyException  if the game has ended and messages cannot be sent.
     */
    public void sendMessage(Client client, @Nullable String to, String message) throws LobbyException {
        String from = clientNickname.get(client);
        try {
            nicknameController.get(from).chat.sendMessage(message, from, to);
        } catch (NullPointerException e) {
            throw new LobbyException("The game is ended, you cannot send messages");
        }

    }
}
