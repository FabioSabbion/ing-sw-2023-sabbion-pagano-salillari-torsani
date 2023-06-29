package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.Message;
import it.polimi.ingsw.view.ViewController;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;

/**
 * extends the ViewController class, implementing methods to control the GUI
 */
public class GUIController implements ViewController {
    private Server server;
    private ClientImpl client;
    private String myNickname;
    private State currentState;
    private GameUpdate gameUpdate;
    private List<Coordinates> currentPickedTiles = new ArrayList<>();
    private List<String> offlinePlayers = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    /**
     * updates the list of players following a connection/disconnection
     * @param players the list of the players' nicknames
     */
    @Override
    public void updatedPlayerList(List<String> players) {
        if (currentState == State.GAME) {
            if ((this.gameUpdate.players().size() - players.size()) < offlinePlayers.size() && offlinePlayers.size() != 0){
                //Means someone has reconnected

                for (String updatedPlayer: players) {
                    if (offlinePlayers.contains(updatedPlayer)){
                        serverError(updatedPlayer + " has reconnected");
                        offlinePlayers.remove(updatedPlayer);
                    }
                }
            } else {
                this.offlinePlayers = new ArrayList<>(this.gameUpdate.players().stream().map(PlayerUpdate::nickname).toList());
                this.offlinePlayers.removeAll(players);

                for (String offlinePlayer: offlinePlayers) {
                    serverError(offlinePlayer + " has disconnected");
                }

                if (this.gameUpdate.players().size() - offlinePlayers.size() == 1){
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(() -> {
                        serverError("You are the only player left!" +
                                " After %d seconds you will win if no one reconnects".formatted(Lobby.seconds));
                        // Shutdown the executor service
                        executorService.shutdown();
                    }, 3500, TimeUnit.MILLISECONDS);
                }
            }
            return;
        }
        if (currentState != State.LOBBY) {
            GUI.showLobbyView(players);
            currentState = State.LOBBY;
        } else {
            GUI.updateLobby(players);
        }
    }

    /**
     * calls a method on the GUI in order to update the windows of the game
     * @param update a GameUpdate object containing the game params to be updated
     */
    @Override
    public void updateGame(GameUpdate update) {
        if (currentState != State.GAME) {
            gameUpdate = update;
            GUI.showGameView();
            currentState = State.GAME;
            GUI.updateGameView(gameUpdate, null);

        } else {
            // update [gameUpdate] with changes
            List<GuiParts> toRefresh = new ArrayList<>();
            this.gameUpdate = gameUpdate.copyWith(
                    myNickname,
                    toRefresh,
                    update.livingRoom(),
                    update.players(),
                    update.commonGoalCards(),
                    update.gameEnder(),
                    update.currentPlayer()
            );

            GUI.updateGameView(gameUpdate, toRefresh);

        }

        if (isMyTurn()) {
            currentPickedTiles = new ArrayList<>();
        }


    }

    /**
     * shows an error message in the GUI by calling the GUI's showToast method
     * @param message the body of the message
     */
    @Override
    public void serverError(String message) {
        GUI.showToast(message);
    }

    /**
     * asks the number of players to the user by calling the GUI's showNumPlayersView method
     */
    @Override
    public void askNumPlayers() {
        GUI.showNumPlayersView();
        currentState = State.ASKNUMPLAYERS;
    }

    /**
     * sets the number of players by calling the server's setNumPlayers method
     * @param numPlayers the number if players in the game
     */
    public void setNumPlayers(int numPlayers) {
        try {
            server.setNumPlayers(numPlayers, client);
        } catch (NumberFormatException e) {
            this.serverError("You must choose a number");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * sets the player's nickname by calling the server's setNickname method
     * @param nickname the user's nickname
     */
    @Override
    public void setNickname(String nickname) {
        try {

            if (!nickname.matches("[A-Za-z0-9]+")) {
                System.out.println("Inadmissible choice of characters! Try Again...");
                return;
            }

            server.setNickname(nickname, client);
            myNickname = nickname;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * controls if the number of tiles is legit and calls the currentPickedTiles' add method
     * @param x a coordinate int
     * @param y a coordinate int
     * @return boolean
     */
    public boolean pickTile(int x, int y) {
        if (currentPickedTiles.size() == 3) return false;

        currentPickedTiles.add(new Coordinates(x,y));

        return true;
    }

    /**
     * deposits the tile of coordinates x, y by calling the currentPickedTiles' remove method
     * @param x a coordinate int
     * @param y a coordinate int
     */
    public void depositTile(int x, int y) {
        currentPickedTiles.remove(new Coordinates(x,y));
    }

    /**
     * checks if the column of choice, the turn and the number of tiles are legit and calls the server's playerMove
     * @param c the column index
     */
    public void chooseColumn(int c) {
        if (!gameUpdate.currentPlayer().nickname().equals(myNickname)) {
            GUI.showToast("It is not your turn");
            return;
        }
        if (currentPickedTiles.isEmpty()) {
            GUI.showToast("You must pick at least one tile");
            return;
        }

        try {
            server.playerMove(currentPickedTiles, c, client);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void getPlayerChoice(boolean yourTurn, String menuChoice) {

    }

    /**
     * starts the client by passing him the view
     * @param client a ClientImpl object
     * @param server a Server object
     */
    @Override
    public void start(ClientImpl client, Server server) {
        this.client = client;
        this.server = server;

        client.run(this);
    }

    /**
     * determines the winner and the players' points, then calls the GUI's showScoreboardView method
     */
    @Override
    public void showEndingScreen() {
        Map<String, Integer> playerPoints = new HashMap<>();

        for (PlayerUpdate player: this.gameUpdate.players()) {
            playerPoints.put(player.nickname(), calculatePersonalGoalCardPoints(player.nickname()) + player.bookshelf().getPoints());
        }

        for (CommonGoalCardUpdate card : this.gameUpdate.commonGoalCards()) {
            var cardPoints = calculateCommonGoalCardPoints(card.commonGoalCardID());

            for (String player : new ArrayList<>(playerPoints.keySet())) {
                playerPoints.put(player, playerPoints.get(player) + cardPoints.get(player));
            }
        }

        if (this.gameUpdate.gameEnder() != null) {
            playerPoints.put(this.gameUpdate.gameEnder().nickname(), playerPoints.get(this.gameUpdate.gameEnder().nickname())+1);
        }

        String winner;
        if (offlinePlayers.size() == gameUpdate.players().size() - 1) {
            winner = myNickname;
        } else {
            winner = playerPoints.entrySet().stream().max((a, b) -> a.getValue().compareTo(b.getValue())).orElseThrow().getKey();
        }
        GUI.showScoreboardView(playerPoints, winner);
    }

    /**
     * receives a list of messages and calls the GUI's addMessages method
     * @param messages the list of messages to be added to the chat_view.fxml
     */
    @Override
    public void receiveMessages(List<Message> messages) {
        if (!messages.isEmpty() && messages.get(0).id() == 0) {
            this.messages.clear();
        }
        this.messages.addAll(messages);

        GUI.addMessages(this.messages);
    }

    /**
     * sends a message to one or all players by calling the server's sendMessage method
     * @param to the nickname of the recipient
     * @param message the body of the message
     */
    public void sendMessage(String to, String message) {
        try {
            server.sendMessageTo(to.equals("Everyone") ? null : to, message, client);
        } catch (RemoteException e) {
            serverError(e.getMessage());
        }
    }

    /**
     * calculates the points of a player's CommonGoalCards
     * @param cardID an int determining the card's ID
     * @return playerPoints
     */
    public Map<String, Integer> calculateCommonGoalCardPoints(int cardID) {
        Map<String, Integer> playerPoints = new HashMap<>();

        CommonGoalCardUpdate commonGoalCard = this.gameUpdate.commonGoalCards().stream().filter(card -> card.commonGoalCardID() == cardID).findFirst().orElseThrow();

        for (PlayerUpdate player : this.gameUpdate.players()) {
            if (commonGoalCard.playerUpdateList().contains(player.nickname())){
                playerPoints.put(player.nickname(), CommonGoalCard.points[this.gameUpdate.players().size()][commonGoalCard.playerUpdateList().indexOf(player.nickname())]);
            }
            else {
                playerPoints.put(player.nickname(), 0);
            }
        }

        return playerPoints;
    }

    /**
     * calculates the points of a player's PersonalGoalCard
     * @param playerNickname the nickname of the player you wish to calculate the points of
     * @return
     */
    public int calculatePersonalGoalCardPoints(String playerNickname) {
        return this.gameUpdate.players().stream().filter(p -> p.nickname().equals(playerNickname)).findFirst().orElseThrow().personalGoalCard().point();
    }

    /**
     * determines whether it's the user's turn
     * @return whether it's my turn
     */
    public boolean isMyTurn() {
        return gameUpdate.currentPlayer().nickname().equals(myNickname);
    }

    /**
     * @return user's nickname
     */
    public String getMyNickname() {
        return myNickname;
    }

    /**
     *
     * @return game's current state
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     *
     * @return gameUpdate
     */
    public GameUpdate getGameUpdate() {
        return gameUpdate;
    }

    /**
     *
     * @return a list of messages
     */
    public List<Message> getMessages() {
        return messages;
    }
}

